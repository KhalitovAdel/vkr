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

describe('Route e2e test', () => {
  const routePageUrl = '/route';
  const routePageUrlPattern = new RegExp('/route(\\?.*)?$');
  let username: string;
  let password: string;
  const routeSample = { routeNumber: 'usher fuel', routeName: 'upright hmph inventory', length: 1211.37, routeType: 'SUBURB' };

  let route;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/routes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/routes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/routes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (route) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/routes/${route.id}`,
      }).then(() => {
        route = undefined;
      });
    }
  });

  it('Routes menu should load Routes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('route');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Route').should('exist');
    cy.url().should('match', routePageUrlPattern);
  });

  describe('Route page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(routePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Route page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/route/new$'));
        cy.getEntityCreateUpdateHeading('Route');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/routes',
          body: routeSample,
        }).then(({ body }) => {
          route = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/routes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [route],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(routePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Route page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('route');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routePageUrlPattern);
      });

      it('edit button click should load edit Route page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Route');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routePageUrlPattern);
      });

      it('edit button click should load edit Route page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Route');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routePageUrlPattern);
      });

      it('last delete button click should delete instance of Route', () => {
        cy.intercept('GET', '/api/routes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('route').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', routePageUrlPattern);

        route = undefined;
      });
    });
  });

  describe('new Route page', () => {
    beforeEach(() => {
      cy.visit(routePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Route');
    });

    it('should create an instance of Route', () => {
      cy.get(`[data-cy="routeNumber"]`).type('now yippee');
      cy.get(`[data-cy="routeNumber"]`).should('have.value', 'now yippee');

      cy.get(`[data-cy="routeName"]`).type('aha playfully likewise');
      cy.get(`[data-cy="routeName"]`).should('have.value', 'aha playfully likewise');

      cy.get(`[data-cy="length"]`).type('5028.07');
      cy.get(`[data-cy="length"]`).should('have.value', '5028.07');

      cy.get(`[data-cy="routeType"]`).select('CITY');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        route = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', routePageUrlPattern);
    });
  });
});
