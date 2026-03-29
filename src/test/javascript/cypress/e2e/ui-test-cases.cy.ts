import { entityCreateSaveButtonSelector } from '../support/entity';
import {
  driverBody,
  emptyIds,
  routeCityBody,
  routeSuburbBody,
  tripBody,
  vehicleBody,
  waybillBody,
  type SeededIds,
} from '../support/ui-test-seed';

/**
 * Автотесты по сценариям UI_TEST_CASES.md (кейсы 1–16); в it() указаны § и URL.
 *
 * - §2: при отсутствии кандидатов — 204 и предупреждение в UI; здесь сидируем ТС → ожидаем 200.
 * - §9–10: после формы вызываются POST .../departure и .../return (как в ТЗ для API), т.к. PUT путевого листа не меняет статус рейса.
 * - §14: не проверяем отсутствие рейсов других дат (нужна изоляция БД).
 * - §15: в форме одна дата, как в UI (в документе — «диапазон», если появится в форме).
 * - Подбор ТС: native value + input/change для React в Electron headless.
 */
describe('UI test cases (UI_TEST_CASES.md)', () => {
  let username: string;
  let password: string;
  let seedTracker: SeededIds | null = null;

  const TEST_DATE = '2030-06-15';

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  afterEach(() => {
    if (seedTracker) {
      cleanupSeeded(seedTracker);
      seedTracker = null;
    }
  });

  function cleanupSeeded(ids: SeededIds) {
    cy.wrap(ids.tripIds).each((id: number) => {
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/trips/${id}`, failOnStatusCode: false });
    });
    cy.wrap(ids.waybillIds).each((id: number) => {
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/waybills/${id}`, failOnStatusCode: false });
    });
    cy.wrap(ids.vehicleIds).each((id: number) => {
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/vehicles/${id}`, failOnStatusCode: false });
    });
    cy.wrap(ids.driverIds).each((id: number) => {
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/drivers/${id}`, failOnStatusCode: false });
    });
    cy.wrap(ids.routeIds).each((id: number) => {
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/routes/${id}`, failOnStatusCode: false });
    });
  }

  /** PrivateRoute сначала рендерит пустой div, пока Redux не подтянет сессию — ждём шапку. */
  function waitAuthenticatedShell() {
    cy.get('[data-cy="navbar"]', { timeout: 30000 }).should('be.visible');
  }

  /** React controlled inputs (особенно type=time в Electron) не всегда обновляют state от cy.type — шлём input/change. */
  function setNativeInputValue(el: HTMLInputElement, value: string) {
    const proto = window.HTMLInputElement.prototype;
    const desc = Object.getOwnPropertyDescriptor(proto, 'value');
    desc?.set?.call(el, value);
    el.dispatchEvent(new Event('input', { bubbles: true }));
    el.dispatchEvent(new Event('change', { bubbles: true }));
  }

  function fillTripSuggestionForm(routeId: number, tripDate: string, departureTime: string) {
    cy.contains('h4', 'Подбор транспортного средства')
      .closest('.container')
      .within(() => {
        cy.get('input[type=number]')
          .first()
          .then($el => setNativeInputValue($el[0] as HTMLInputElement, String(routeId)));
        cy.get('input[type=date]').then($el => setNativeInputValue($el[0] as HTMLInputElement, tripDate));
        cy.get('input[type=time]').then($el => setNativeInputValue($el[0] as HTMLInputElement, departureTime));
      });
  }

  /**
   * 400 от POST /api/trips: в теле может быть русское сообщение, fieldErrors с @NoOverlapConstraint
   * или общий title «Method argument not valid» / «Bad Request».
   */
  function expectTripCreateRejected(response: { statusCode?: number; body?: unknown }) {
    expect(response?.statusCode).to.eq(400);
    const raw = JSON.stringify(response?.body ?? {});
    expect(
      /пересечение|водител|ремонте|пригородных|стажем|constraint|fielderror|error\.validation|not valid|наруш|bad request/i.test(
        raw,
      ),
      raw.slice(0, 900),
    ).to.eq(true);
  }

  // Dashboard: авто-опрос API отключён при window.Cypress — без лишних запросов и флаков.
  it('case 1 (UI_TEST_CASES §1): /dashboard — цифры парка совпадают с GET /api/reports/fleet', () => {
    cy.authenticatedRequest({ url: '/api/reports/fleet' }).then(({ body }) => {
      cy.visit('/dashboard');
      waitAuthenticatedShell();
      cy.get('.app-container', { timeout: 15000 }).should('contain', 'Всего ТС');
      cy.get('.app-container').should('contain', String(body.total));
      cy.get('.app-container').should('contain', String(body.operational));
      cy.get('.app-container').should('contain', String(body.repair));
      cy.get('[data-cy="dashboardLiveRefreshNote"]').should('not.exist');
    });
  });

  it('case 2 (UI_TEST_CASES §2): /trip/suggestion — ввод routeId, даты, времени; успех или предупреждение; здесь 200 + ТС', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      // Минимальная вместимость повышает шанс, что победит именно это ТС (алгоритм — min по passengerCapacity).
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix, { passengerCapacity: 1 }) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.intercept('POST', '/api/trips/suggest-vehicle').as('suggestVehicle');
        cy.visit('/trip/suggestion');
        waitAuthenticatedShell();
        fillTripSuggestionForm(route.id, TEST_DATE, '09:30');
        cy.contains('h4', 'Подбор транспортного средства').closest('.container').within(() => {
          cy.contains('button', 'Подобрать').click();
        });
        cy.wait('@suggestVehicle', { timeout: 20000 }).then(({ response }) => {
          expect(response?.statusCode, JSON.stringify(response?.body)).to.eq(200);
          const sn = response?.body?.stateNumber as string;
          expect(sn, 'suggest-vehicle should return stateNumber').to.be.a('string').and.not.be.empty;
          cy.get('.alert-success').should('be.visible').and('contain', sn);
        });
      });
    });
  });

  it('case 3 (UI_TEST_CASES §3): рейс занимает ТС → подбор на пересечении не предлагает занятое', () => {
    seedTracker = emptyIds();
    const suffix = `${Date.now()}`.slice(-7);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({
        method: 'POST',
        url: '/api/vehicles',
        body: vehicleBody(`${suffix}A`, { passengerCapacity: 80 }),
      }).then(({ body: busyVehicle }) => {
        seedTracker!.vehicleIds.push(busyVehicle.id);
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/vehicles',
          body: vehicleBody(`${suffix}B`, { passengerCapacity: 2 }),
        }).then(({ body: freeVehicle }) => {
          seedTracker!.vehicleIds.push(freeVehicle.id);
          cy.authenticatedRequest({
            method: 'POST',
            url: '/api/trips',
            body: tripBody({
              tripDate: TEST_DATE,
              departureTime: '10:00:00',
              arrivalTime: '14:00:00',
              vehicleId: busyVehicle.id,
              routeId: route.id,
            }),
          }).then(({ body: trip }) => {
            seedTracker!.tripIds.push(trip.id);
            cy.intercept('POST', '/api/trips/suggest-vehicle').as('suggestVehicle3');
            cy.visit('/trip/suggestion');
            waitAuthenticatedShell();
            fillTripSuggestionForm(route.id, TEST_DATE, '10:30');
            cy.contains('h4', 'Подбор транспортного средства').closest('.container').within(() => {
              cy.contains('button', 'Подобрать').click();
            });
            cy.wait('@suggestVehicle3', { timeout: 20000 }).then(({ response }) => {
              expect(response?.statusCode, JSON.stringify(response?.body)).to.eq(200);
              expect(response?.body?.id, 'занятое ТС не должно попасть в подбор').to.not.eq(busyVehicle.id);
              const sn = response?.body?.stateNumber as string;
              expect(sn).to.not.eq(busyVehicle.stateNumber);
              cy.get('.alert-success').should('contain', sn).and('not.contain', busyVehicle.stateNumber);
            });
          });
        });
      });
    });
  });

  it('case 4 (UI_TEST_CASES §4): /trip/new — второй рейс с тем же водителем на пересечении → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(`${suffix}a`) }).then(({ body: v1 }) => {
        seedTracker!.vehicleIds.push(v1.id);
        cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(`${suffix}b`) }).then(({ body: v2 }) => {
          seedTracker!.vehicleIds.push(v2.id);
          cy.authenticatedRequest({ method: 'POST', url: '/api/drivers', body: driverBody(suffix, 5) }).then(({ body: driver }) => {
            seedTracker!.driverIds.push(driver.id);
            cy.authenticatedRequest({
              method: 'POST',
              url: '/api/trips',
              body: tripBody({
                tripDate: TEST_DATE,
                departureTime: '08:00:00',
                arrivalTime: '10:00:00',
                vehicleId: v1.id,
                driverId: driver.id,
                routeId: route.id,
              }),
            }).then(({ body: t1 }) => {
              seedTracker!.tripIds.push(t1.id);
              cy.intercept('POST', '/api/trips').as('postTrip');
              cy.visit('/trip/new');
              waitAuthenticatedShell();
              cy.get('[data-cy="departureTime"]').clear().type('09:00:00');
              cy.get('[data-cy="arrivalTime"]').clear().type('11:00:00');
              cy.get('[data-cy="tripDate"]').clear().type(TEST_DATE);
              cy.get('[data-cy="vehicle"]').select(String(v2.id));
              cy.get('[data-cy="driver"]').select(String(driver.id));
              cy.get('[data-cy="route"]').select(String(route.id));
              cy.get(entityCreateSaveButtonSelector).click();
              cy.wait('@postTrip').then(({ response }) => {
                expectTripCreateRejected(response!);
              });
            });
          });
        });
      });
    });
  });

  it('case 5 (UI_TEST_CASES §5): /trip/new — arrivalTime ≤ departureTime (равные времена) → 400, рейс не создан', () => {
    cy.intercept('POST', '/api/trips').as('postTrip');
    cy.visit('/trip/new');
    waitAuthenticatedShell();
    // ТЗ: «раньше или равно» — проверяем границу «равно»
    cy.get('[data-cy="departureTime"]').clear().type('12:00:00');
    cy.get('[data-cy="arrivalTime"]').clear().type('12:00:00');
    cy.get('[data-cy="tripDate"]').clear().type(TEST_DATE);
    cy.get(entityCreateSaveButtonSelector).click();
    cy.wait('@postTrip').its('response.statusCode').should('eq', 400);
  });

  it('case 6 (UI_TEST_CASES §6): /vehicle/:id/edit — REPAIR → /trip/new с этим ТС → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({
        method: 'POST',
        url: '/api/vehicles',
        body: vehicleBody(suffix, { technicalStatus: 'OPERATIONAL' }),
      }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.intercept('PUT', '/api/vehicles/*').as('putVehicleRepair');
        cy.visit(`/vehicle/${vehicle.id}/edit`);
        waitAuthenticatedShell();
        cy.get('[data-cy="technicalStatus"]').select('REPAIR');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@putVehicleRepair').its('response.statusCode').should('eq', 200);
        cy.intercept('POST', '/api/trips').as('postTrip');
        cy.visit('/trip/new');
        waitAuthenticatedShell();
        cy.get('[data-cy="departureTime"]').clear().type('10:00:00');
        cy.get('[data-cy="arrivalTime"]').clear().type('12:00:00');
        cy.get('[data-cy="tripDate"]').clear().type(TEST_DATE);
        cy.get('[data-cy="vehicle"]').select(String(vehicle.id));
        cy.get('[data-cy="route"]').select(String(route.id));
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@postTrip').then(({ response }) => {
          expectTripCreateRejected(response!);
        });
      });
    });
  });

  it('case 7 (UI_TEST_CASES §7): /driver/new — стаж < 3, пригородный маршрут → /trip/new → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    const emp = `U${suffix}`.slice(0, 10);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeSuburbBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.intercept('POST', '/api/drivers').as('postDriverUi');
        cy.visit('/driver/new');
        waitAuthenticatedShell();
        cy.get('[data-cy="employeeNumber"]').type(emp);
        cy.get('[data-cy="fullName"]').type('LowExp Driver');
        cy.get('[data-cy="licenseCategory"]').type('D');
        cy.get('[data-cy="experience"]').clear().type('2');
        cy.get('[data-cy="hireDate"]').clear().type('2018-01-01');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@postDriverUi').then(({ response }) => {
          expect(response?.statusCode).to.eq(201);
          const driverId = response!.body.id as number;
          seedTracker!.driverIds.push(driverId);
          cy.intercept('POST', '/api/trips').as('postTrip');
          cy.visit('/trip/new');
          waitAuthenticatedShell();
          cy.get('[data-cy="departureTime"]').clear().type('10:00:00');
          cy.get('[data-cy="arrivalTime"]').clear().type('12:00:00');
          cy.get('[data-cy="tripDate"]').clear().type(TEST_DATE);
          cy.get('[data-cy="vehicle"]').select(String(vehicle.id));
          cy.get('[data-cy="driver"]').select(String(driverId));
          cy.get('[data-cy="route"]').select(String(route.id));
          cy.get(entityCreateSaveButtonSelector).click();
          cy.wait('@postTrip').then(({ response: r2 }) => {
            expectTripCreateRejected(r2!);
            expect(JSON.stringify(r2?.body ?? '')).to.match(/пригородных|стаж|constraint|validation|not valid|bad request/i);
          });
        });
      });
    });
  });

  it('case 8 (UI_TEST_CASES §8): /trip/new дважды — то же ТС, пересечение интервалов → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trips',
          body: tripBody({
            tripDate: TEST_DATE,
            departureTime: '09:00:00',
            arrivalTime: '11:00:00',
            vehicleId: vehicle.id,
            routeId: route.id,
          }),
        }).then(({ body: t1 }) => {
          seedTracker!.tripIds.push(t1.id);
          cy.intercept('POST', '/api/trips').as('postTrip');
          cy.visit('/trip/new');
          waitAuthenticatedShell();
          cy.get('[data-cy="departureTime"]').clear().type('10:00:00');
          cy.get('[data-cy="arrivalTime"]').clear().type('12:00:00');
          cy.get('[data-cy="tripDate"]').clear().type(TEST_DATE);
          cy.get('[data-cy="vehicle"]').select(String(vehicle.id));
          cy.get('[data-cy="route"]').select(String(route.id));
          cy.get(entityCreateSaveButtonSelector).click();
          cy.wait('@postTrip').then(({ response }) => {
            expectTripCreateRejected(response!);
            expect(JSON.stringify(response?.body ?? '')).to.match(/пересечение|тс|водител|занят|constraint|validation|not valid|bad request/i);
          });
        });
      });
    });
  });

  it('case 9 (UI_TEST_CASES §9): /waybill/:id/edit — выезд + пробег; затем registerDeparture → рейс ONGOING', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.authenticatedRequest({ method: 'POST', url: '/api/waybills', body: waybillBody(suffix) }).then(({ body: wb }) => {
          seedTracker!.waybillIds.push(wb.id);
          cy.authenticatedRequest({
            method: 'POST',
            url: '/api/trips',
            body: tripBody({
              tripDate: TEST_DATE,
              departureTime: '07:00:00',
              arrivalTime: '09:00:00',
              vehicleId: vehicle.id,
              routeId: route.id,
              waybillId: wb.id,
              tripStatus: 'SCHEDULED',
            }),
          }).then(({ body: trip }) => {
            seedTracker!.tripIds.push(trip.id);
            cy.intercept('PUT', '/api/waybills/*').as('putWaybill');
            cy.visit(`/waybill/${wb.id}/edit`);
            waitAuthenticatedShell();
            cy.get('[data-cy="actualDeparture"]').clear().type('2030-06-15T08:00');
            cy.get('[data-cy="mileageStart"]').clear().type('100');
            cy.get('[data-cy="mileageEnd"]').clear().type('100');
            cy.get(entityCreateSaveButtonSelector).click();
            cy.wait('@putWaybill').its('response.statusCode').should('eq', 200);
            cy.url().should('match', /\/waybill$/);
            cy.authenticatedRequest({
              method: 'POST',
              url: `/api/waybills/${wb.id}/departure`,
              body: { eventTime: '2030-06-15T08:00:00.000Z', mileage: 100 },
            }).its('status').should('eq', 200);
            cy.authenticatedRequest({ url: `/api/trips/${trip.id}` }).then(({ body: t }) => {
              expect(t.tripStatus).to.eq('ONGOING');
            });
          });
        });
      });
    });
  });

  it('case 10 (UI_TEST_CASES §10): после выезда — /waybill/:id/edit возврат + пробег; registerReturn → рейс COMPLETED', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.authenticatedRequest({ method: 'POST', url: '/api/waybills', body: waybillBody(suffix) }).then(({ body: wb }) => {
          seedTracker!.waybillIds.push(wb.id);
          cy.authenticatedRequest({
            method: 'POST',
            url: '/api/trips',
            body: tripBody({
              tripDate: TEST_DATE,
              departureTime: '07:00:00',
              arrivalTime: '09:00:00',
              vehicleId: vehicle.id,
              routeId: route.id,
              waybillId: wb.id,
              tripStatus: 'SCHEDULED',
            }),
          }).then(({ body: trip }) => {
            seedTracker!.tripIds.push(trip.id);
            cy.authenticatedRequest({
              method: 'POST',
              url: `/api/waybills/${wb.id}/departure`,
              body: { eventTime: '2030-06-15T08:00:00.000Z', mileage: 100 },
            }).its('status').should('eq', 200);
            cy.intercept('PUT', '/api/waybills/*').as('putWaybill10');
            cy.visit(`/waybill/${wb.id}/edit`);
            waitAuthenticatedShell();
            cy.get('[data-cy="actualReturn"]').clear().type('2030-06-15T18:00');
            cy.get('[data-cy="mileageEnd"]').clear().type('250');
            cy.get(entityCreateSaveButtonSelector).click();
            cy.wait('@putWaybill10').its('response.statusCode').should('eq', 200);
            cy.url().should('match', /\/waybill$/);
            cy.authenticatedRequest({
              method: 'POST',
              url: `/api/waybills/${wb.id}/return`,
              body: { eventTime: '2030-06-15T18:00:00.000Z', mileage: 250 },
            }).its('status').should('eq', 200);
            cy.authenticatedRequest({ url: `/api/trips/${trip.id}` }).then(({ body: t }) => {
              expect(t.tripStatus).to.eq('COMPLETED');
            });
          });
        });
      });
    });
  });

  it('case 11 (UI_TEST_CASES §11): /waybill/:id/edit — mileageEnd < mileageStart → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/waybills', body: waybillBody(suffix) }).then(({ body: wb }) => {
      seedTracker!.waybillIds.push(wb.id);
      cy.intercept('PUT', '/api/waybills/*').as('putWaybill');
      cy.visit(`/waybill/${wb.id}/edit`);
      waitAuthenticatedShell();
      cy.get('[data-cy="mileageStart"]').clear().type('500');
      cy.get('[data-cy="mileageEnd"]').clear().type('100');
      cy.get(entityCreateSaveButtonSelector).click();
      cy.wait('@putWaybill').its('response.statusCode').should('eq', 400);
    });
  });

  it('case 12 (UI_TEST_CASES §12): /waybill/:id/edit — actualReturn < actualDeparture → 400', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/waybills', body: waybillBody(suffix) }).then(({ body: wb }) => {
      seedTracker!.waybillIds.push(wb.id);
      cy.intercept('PUT', '/api/waybills/*').as('putWaybill');
      cy.visit(`/waybill/${wb.id}/edit`);
      waitAuthenticatedShell();
      cy.get('[data-cy="actualDeparture"]').clear().type('2030-06-15T14:00');
      cy.get('[data-cy="actualReturn"]').clear().type('2030-06-15T08:00');
      cy.get(entityCreateSaveButtonSelector).click();
      cy.wait('@putWaybill').its('response.statusCode').should('eq', 400);
    });
  });

  it('case 13 (UI_TEST_CASES §13): /vehicle/fleet-status совпадает с GET /api/reports/fleet и составом GET /api/vehicles', () => {
    cy.authenticatedRequest({ url: '/api/reports/fleet' }).then(({ body: fleet }) => {
      cy.authenticatedRequest({ url: '/api/vehicles' }).then(({ body: vehicles }) => {
        const list = vehicles as { technicalStatus?: string }[];
        const op = list.filter(v => v.technicalStatus === 'OPERATIONAL').length;
        const rep = list.filter(v => v.technicalStatus === 'REPAIR').length;
        expect(list.length, 'total').to.eq(fleet.total);
        expect(op, 'operational').to.eq(fleet.operational);
        expect(rep, 'repair').to.eq(fleet.repair);
        cy.visit('/vehicle/fleet-status');
        waitAuthenticatedShell();
        cy.contains('h4', 'Статус парка', { timeout: 15000 }).should('be.visible');
        cy.get('.app-container').should('contain', String(fleet.total));
        cy.get('.app-container').should('contain', String(fleet.operational));
        cy.get('.app-container').should('contain', String(fleet.repair));
      });
    });
  });

  it('case 14 (UI_TEST_CASES §14): /trip/by-date — за выбранную дату виден рейс и статус', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trips',
          body: tripBody({
            tripDate: TEST_DATE,
            departureTime: '07:00:00',
            arrivalTime: '09:00:00',
            vehicleId: vehicle.id,
            routeId: route.id,
            tripStatus: 'SCHEDULED',
          }),
        }).then(({ body: trip }) => {
          seedTracker!.tripIds.push(trip.id);
          cy.visit('/trip/by-date');
          waitAuthenticatedShell();
          cy.contains('h4', 'Рейсы по дате')
            .closest('.container')
            .within(() => {
              cy.get('input[type=date]').clear().type(TEST_DATE);
              cy.contains('button', 'Показать').click();
            });
          cy.get('.app-container ul li').should('contain', `#${trip.id}`).and('contain', 'SCHEDULED');
        });
      });
    });
  });

  it('case 15 (UI_TEST_CASES §15): /vehicle/schedule и /driver/schedule — рейс за выбранную дату', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({ method: 'POST', url: '/api/routes', body: routeCityBody(suffix) }).then(({ body: route }) => {
      seedTracker!.routeIds.push(route.id);
      cy.authenticatedRequest({ method: 'POST', url: '/api/vehicles', body: vehicleBody(suffix) }).then(({ body: vehicle }) => {
        seedTracker!.vehicleIds.push(vehicle.id);
        cy.authenticatedRequest({ method: 'POST', url: '/api/drivers', body: driverBody(suffix, 5) }).then(({ body: driver }) => {
          seedTracker!.driverIds.push(driver.id);
          cy.authenticatedRequest({
            method: 'POST',
            url: '/api/trips',
            body: tripBody({
              tripDate: TEST_DATE,
              departureTime: '12:00:00',
              arrivalTime: '14:00:00',
              vehicleId: vehicle.id,
              driverId: driver.id,
              routeId: route.id,
            }),
          }).then(({ body: trip }) => {
            seedTracker!.tripIds.push(trip.id);
            cy.visit('/vehicle/schedule');
            waitAuthenticatedShell();
            cy.contains('h4', 'Расписание ТС')
              .closest('.container')
              .within(() => {
                cy.get('input[type=number]').clear().type(String(vehicle.id));
                cy.get('input[type=date]').clear().type(TEST_DATE);
                cy.contains('button', 'Показать').click();
              });
            cy.get('.app-container ul li').should('contain', `#${trip.id}`);

            cy.visit('/driver/schedule');
            waitAuthenticatedShell();
            cy.contains('h4', 'Расписание водителя')
              .closest('.container')
              .within(() => {
                cy.get('input[type=number]').clear().type(String(driver.id));
                cy.get('input[type=date]').clear().type(TEST_DATE);
                cy.contains('button', 'Показать').click();
              });
            cy.get('.app-container ul li').should('contain', `#${trip.id}`);
          });
        });
      });
    });
  });

  it('case 16 (UI_TEST_CASES §16): /vehicle/new — год; карточка /vehicle/:id показывает year без ошибок', () => {
    seedTracker = emptyIds();
    const suffix = Date.now().toString().slice(-8);
    const plate = vehicleBody(suffix).stateNumber;
    cy.intercept('POST', '/api/vehicles').as('postVehicle');
    cy.visit('/vehicle/new');
    waitAuthenticatedShell();
    cy.get('[data-cy="stateNumber"]').type(plate);
    cy.get('[data-cy="model"]').type('YearTest');
    cy.get('[data-cy="vehicleType"]').select('BUS');
    cy.get('[data-cy="capacity"]').select('MEDIUM');
    cy.get('[data-cy="passengerCapacity"]').clear().type('25');
    cy.get('[data-cy="year"]').clear().type('2019');
    cy.get('[data-cy="technicalStatus"]').select('OPERATIONAL');
    cy.get('[data-cy="mileage"]').clear().type('12000');
    cy.get(entityCreateSaveButtonSelector).click();
    cy.wait('@postVehicle').then(({ response }) => {
      expect(response?.statusCode).to.eq(201);
      expect(response?.body.year).to.eq(2019);
      const vid = response!.body.id as number;
      seedTracker!.vehicleIds.push(vid);
      cy.visit(`/vehicle/${vid}`);
      waitAuthenticatedShell();
      cy.get('[data-cy="vehicleDetailsHeading"]').should('be.visible');
      cy.get('.jh-entity-details').should('contain', '2019');
      cy.url().should('match', /\/vehicle\/\d+$/);
    });
  });
});
