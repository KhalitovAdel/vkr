import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Vehicle e2e test', () => {
  const vehiclePageUrl = '/vehicle';
  const vehiclePageUrlPattern = new RegExp('/vehicle(\\?.*)?$');
  let username: string;
  let password: string;
  const vehicleSample = {
    stateNumber: 'of',
    model: 'shyly',
    vehicleType: 'BUS',
    capacity: 'SMALL',
    passengerCapacity: 24755,
    year: 12512,
    technicalStatus: 'REPAIR',
    mileage: 12162.89,
  };

  let vehicle;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/vehicles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/vehicles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/vehicles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (vehicle) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/vehicles/${vehicle.id}`,
      }).then(() => {
        vehicle = undefined;
      });
    }
  });

  it('Vehicles menu should load Vehicles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('vehicle');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Vehicle').should('exist');
    cy.url().should('match', vehiclePageUrlPattern);
  });

  describe('Vehicle page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(vehiclePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Vehicle page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/vehicle/new$'));
        cy.getEntityCreateUpdateHeading('Vehicle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', vehiclePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/vehicles',
          body: vehicleSample,
        }).then(({ body }) => {
          vehicle = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/vehicles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [vehicle],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(vehiclePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Vehicle page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('vehicle');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', vehiclePageUrlPattern);
      });

      it('edit button click should load edit Vehicle page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vehicle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', vehiclePageUrlPattern);
      });

      it('edit button click should load edit Vehicle page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Vehicle');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', vehiclePageUrlPattern);
      });

      it('last delete button click should delete instance of Vehicle', () => {
        cy.intercept('GET', '/api/vehicles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('vehicle').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', vehiclePageUrlPattern);

        vehicle = undefined;
      });
    });
  });

  describe('new Vehicle page', () => {
    beforeEach(() => {
      cy.visit(vehiclePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Vehicle');
    });

    it('should create an instance of Vehicle', () => {
      cy.get(`[data-cy="stateNumber"]`).type('unless');
      cy.get(`[data-cy="stateNumber"]`).should('have.value', 'unless');

      cy.get(`[data-cy="model"]`).type('grumpy phew knavishly');
      cy.get(`[data-cy="model"]`).should('have.value', 'grumpy phew knavishly');

      cy.get(`[data-cy="vehicleType"]`).select('TRAM');

      cy.get(`[data-cy="capacity"]`).select('LARGE');

      cy.get(`[data-cy="passengerCapacity"]`).type('23949');
      cy.get(`[data-cy="passengerCapacity"]`).should('have.value', '23949');

      cy.get(`[data-cy="year"]`).type('14176');
      cy.get(`[data-cy="year"]`).should('have.value', '14176');

      cy.get(`[data-cy="technicalStatus"]`).select('SCRAPPED');

      cy.get(`[data-cy="mileage"]`).type('4851.58');
      cy.get(`[data-cy="mileage"]`).should('have.value', '4851.58');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        vehicle = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', vehiclePageUrlPattern);
    });
  });
});
