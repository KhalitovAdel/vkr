import { vkrScreenshot } from '../support/entity';
import { eventBody } from '../support/ui-test-seed';

/**
 * Скриншоты списков сущностей для ВКР (меню «Сущности»).
 * Файлы: target/cypress/screenshots/vkr-entity-screenshots.cy.ts/vkr/Entity-Driver.png и др.
 */
describe('VKR: списки сущностей', () => {
  let username: string;
  let password: string;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  function waitShell() {
    cy.get('[data-cy="navbar"]', { timeout: 30000 }).should('be.visible');
  }

  /** Ждём ответ списка и отрисовку таблицы либо плашки «Записи не найдены» (не снимаем во время loading). */
  function openEntityList(hrefSegment: string, headingDataCy: string, listGetPattern: string) {
    cy.intercept('GET', listGetPattern).as('vkrEntityList');
    cy.visit('/');
    waitShell();
    cy.clickOnEntityMenuItem(hrefSegment);
    cy.get(`[data-cy="${headingDataCy}"]`, { timeout: 20000 }).should('be.visible');
    cy.wait('@vkrEntityList', { timeout: 20000 });
    cy.get(`[data-cy="${headingDataCy}"]`)
      .closest('.jh-card')
      .find('.table-responsive')
      .first()
      .within(() => {
        cy.get('table, .alert.alert-warning', { timeout: 15000 }).should('be.visible');
      });
  }

  it('Entity-Driver: список водителей', () => {
    openEntityList('driver', 'DriverHeading', '/api/drivers+(?*|)');
    vkrScreenshot('Entity-Driver');
  });

  it('Entity-Event: список событий', () => {
    const suffix = Date.now().toString().slice(-8);
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/events',
      body: eventBody(suffix),
    }).then(({ body }) => {
      const id = body.id as number;
      openEntityList('event', 'EventHeading', '/api/events+(?*|)');
      cy.get('[data-cy="entityTable"]').should('exist');
      vkrScreenshot('Entity-Event');
      cy.authenticatedRequest({ method: 'DELETE', url: `/api/events/${id}`, failOnStatusCode: false });
    });
  });

  it('Entity-RouteStop: остановки на маршрутах', () => {
    openEntityList('route-stop', 'RouteStopHeading', '/api/route-stops+(?*|)');
    vkrScreenshot('Entity-RouteStop');
  });

  it('Entity-Route: список маршрутов', () => {
    openEntityList('route', 'RouteHeading', '/api/routes+(?*|)');
    vkrScreenshot('Entity-Route');
  });

  it('Entity-Stop: список остановок', () => {
    openEntityList('stop', 'StopHeading', '/api/stops+(?*|)');
    vkrScreenshot('Entity-Stop');
  });

  it('Entity-Trip: список рейсов', () => {
    openEntityList('trip', 'TripHeading', '/api/trips+(?*|)');
    vkrScreenshot('Entity-Trip');
  });

  it('Entity-Vehicle: список ТС', () => {
    openEntityList('vehicle', 'VehicleHeading', '/api/vehicles+(?*|)');
    vkrScreenshot('Entity-Vehicle');
  });

  it('Entity-Waybill: путевые листы', () => {
    openEntityList('waybill', 'WaybillHeading', '/api/waybills+(?*|)');
    vkrScreenshot('Entity-Waybill');
  });
});
