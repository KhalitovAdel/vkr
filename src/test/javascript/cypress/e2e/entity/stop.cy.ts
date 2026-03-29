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

describe('Stop e2e test', () => {
  const stopPageUrl = '/stop';
  const stopPageUrlPattern = new RegExp('/stop(\\?.*)?$');
  let username: string;
  let password: string;
  const stopSample = { name: 'astride pause fuss' };

  let stop;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stops+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stops').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stops/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stop) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stops/${stop.id}`,
      }).then(() => {
        stop = undefined;
      });
    }
  });

  it('Stops menu should load Stops page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stop');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Stop').should('exist');
    cy.url().should('match', stopPageUrlPattern);
  });

  describe('Stop page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stopPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Stop page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stop/new$'));
        cy.getEntityCreateUpdateHeading('Stop');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stopPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stops',
          body: stopSample,
        }).then(({ body }) => {
          stop = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stops+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [stop],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(stopPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Stop page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stop');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stopPageUrlPattern);
      });

      it('edit button click should load edit Stop page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Stop');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stopPageUrlPattern);
      });

      it('edit button click should load edit Stop page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Stop');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stopPageUrlPattern);
      });

      it('last delete button click should delete instance of Stop', () => {
        cy.intercept('GET', '/api/stops/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stop').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stopPageUrlPattern);

        stop = undefined;
      });
    });
  });

  describe('new Stop page', () => {
    beforeEach(() => {
      cy.visit(stopPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Stop');
    });

    it('should create an instance of Stop', () => {
      cy.get(`[data-cy="name"]`).type('wetly championship');
      cy.get(`[data-cy="name"]`).should('have.value', 'wetly championship');

      cy.get(`[data-cy="latitude"]`).type('10568.34');
      cy.get(`[data-cy="latitude"]`).should('have.value', '10568.34');

      cy.get(`[data-cy="longitude"]`).type('1475.7');
      cy.get(`[data-cy="longitude"]`).should('have.value', '1475.7');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        stop = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', stopPageUrlPattern);
    });
  });
});
