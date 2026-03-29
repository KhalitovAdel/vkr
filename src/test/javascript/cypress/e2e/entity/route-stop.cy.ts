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

describe('RouteStop e2e test', () => {
  const routeStopPageUrl = '/route-stop';
  const routeStopPageUrlPattern = new RegExp('/route-stop(\\?.*)?$');
  let username: string;
  let password: string;
  const routeStopSample = { stopOrder: 6059 };

  let routeStop;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/route-stops+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/route-stops').as('postEntityRequest');
    cy.intercept('DELETE', '/api/route-stops/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (routeStop) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/route-stops/${routeStop.id}`,
      }).then(() => {
        routeStop = undefined;
      });
    }
  });

  it('RouteStops menu should load RouteStops page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('route-stop');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RouteStop').should('exist');
    cy.url().should('match', routeStopPageUrlPattern);
  });

  describe('RouteStop page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(routeStopPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RouteStop page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/route-stop/new$'));
        cy.getEntityCreateUpdateHeading('RouteStop');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routeStopPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/route-stops',
          body: routeStopSample,
        }).then(({ body }) => {
          routeStop = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/route-stops+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [routeStop],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(routeStopPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RouteStop page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('routeStop');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routeStopPageUrlPattern);
      });

      it('edit button click should load edit RouteStop page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RouteStop');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routeStopPageUrlPattern);
      });

      it('edit button click should load edit RouteStop page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RouteStop');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routeStopPageUrlPattern);
      });

      it('last delete button click should delete instance of RouteStop', () => {
        cy.intercept('GET', '/api/route-stops/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('routeStop').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routeStopPageUrlPattern);

        routeStop = undefined;
      });
    });
  });

  describe('new RouteStop page', () => {
    beforeEach(() => {
      cy.visit(routeStopPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RouteStop');
    });

    it('should create an instance of RouteStop', () => {
      cy.get(`[data-cy="stopOrder"]`).type('31953');
      cy.get(`[data-cy="stopOrder"]`).should('have.value', '31953');

      cy.get(`[data-cy="distanceFromPrev"]`).type('18046.92');
      cy.get(`[data-cy="distanceFromPrev"]`).should('have.value', '18046.92');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        routeStop = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', routeStopPageUrlPattern);
    });
  });
});
