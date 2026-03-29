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

describe('Trip e2e test', () => {
  const tripPageUrl = '/trip';
  const tripPageUrlPattern = new RegExp('/trip(\\?.*)?$');
  let username: string;
  let password: string;
  const tripSample = { departureTime: '11:13:00', arrivalTime: '18:26:00', tripDate: '2026-03-26', tripStatus: 'ONGOING' };

  let trip;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trips+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trips').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trips/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trip) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trips/${trip.id}`,
      }).then(() => {
        trip = undefined;
      });
    }
  });

  it('Trips menu should load Trips page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trip');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Trip').should('exist');
    cy.url().should('match', tripPageUrlPattern);
  });

  describe('Trip page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tripPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Trip page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trip/new$'));
        cy.getEntityCreateUpdateHeading('Trip');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trips',
          body: tripSample,
        }).then(({ body }) => {
          trip = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trips+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [trip],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tripPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Trip page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trip');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });

      it('edit button click should load edit Trip page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Trip');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });

      it('edit button click should load edit Trip page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Trip');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });

      it('last delete button click should delete instance of Trip', () => {
        cy.intercept('GET', '/api/trips/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('trip').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);

        trip = undefined;
      });
    });
  });

  describe('new Trip page', () => {
    beforeEach(() => {
      cy.visit(tripPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Trip');
    });

    it('should create an instance of Trip', () => {
      cy.get(`[data-cy="departureTime"]`).type('17:07:00');
      cy.get(`[data-cy="departureTime"]`).invoke('val').should('match', new RegExp('17:07:00'));

      cy.get(`[data-cy="arrivalTime"]`).type('08:50:00');
      cy.get(`[data-cy="arrivalTime"]`).invoke('val').should('match', new RegExp('08:50:00'));

      cy.get(`[data-cy="tripDate"]`).type('2026-03-25');
      cy.get(`[data-cy="tripDate"]`).blur();
      cy.get(`[data-cy="tripDate"]`).should('have.value', '2026-03-25');

      cy.get(`[data-cy="tripStatus"]`).select('COMPLETED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trip = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', tripPageUrlPattern);
    });
  });
});
