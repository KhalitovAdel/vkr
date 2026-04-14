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

describe('Waybill e2e test', () => {
  const waybillPageUrl = '/waybill';
  const waybillPageUrlPattern = new RegExp('/waybill(\\?.*)?$');
  let username: string;
  let password: string;
  const waybillSample = { documentNumber: 'aha' };

  let waybill;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/waybills+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/waybills').as('postEntityRequest');
    cy.intercept('DELETE', '/api/waybills/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (waybill) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/waybills/${waybill.id}`,
      }).then(() => {
        waybill = undefined;
      });
    }
  });

  it('Waybills menu should load Waybills page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('waybill');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Waybill').should('exist');
    cy.url().should('match', waybillPageUrlPattern);
  });

  describe('Waybill page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(waybillPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Waybill page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/waybill/new$'));
        cy.getEntityCreateUpdateHeading('Waybill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waybillPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/waybills',
          body: waybillSample,
        }).then(({ body }) => {
          waybill = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/waybills+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [waybill],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(waybillPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Waybill page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('waybill');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waybillPageUrlPattern);
      });

      it('edit button click should load edit Waybill page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Waybill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waybillPageUrlPattern);
      });

      it('edit button click should load edit Waybill page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Waybill');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waybillPageUrlPattern);
      });

      it('last delete button click should delete instance of Waybill', () => {
        cy.intercept('GET', '/api/waybills/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('waybill').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waybillPageUrlPattern);

        waybill = undefined;
      });
    });
  });

  describe('new Waybill page', () => {
    beforeEach(() => {
      cy.visit(waybillPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Waybill');
    });

    it('should create an instance of Waybill', () => {
      cy.get(`[data-cy="documentNumber"]`).type('ouch hence provided');
      cy.get(`[data-cy="documentNumber"]`).should('have.value', 'ouch hence provided');

      cy.get(`[data-cy="actualDeparture"]`).type('2026-03-26T15:11');
      cy.get(`[data-cy="actualDeparture"]`).blur();
      cy.get(`[data-cy="actualDeparture"]`).should('have.value', '2026-03-26T15:11');

      cy.get(`[data-cy="actualReturn"]`).type('2026-03-26T18:00');
      cy.get(`[data-cy="actualReturn"]`).blur();
      cy.get(`[data-cy="actualReturn"]`).should('have.value', '2026-03-26T18:00');

      cy.get(`[data-cy="mileageStart"]`).type('1000');
      cy.get(`[data-cy="mileageStart"]`).should('have.value', '1000');

      cy.get(`[data-cy="mileageEnd"]`).type('2500.4');
      cy.get(`[data-cy="mileageEnd"]`).should('have.value', '2500.4');

      cy.get(`[data-cy="fuelConsumptionPlan"]`).type('24712.33');
      cy.get(`[data-cy="fuelConsumptionPlan"]`).should('have.value', '24712.33');

      cy.get(`[data-cy="fuelConsumptionFact"]`).type('17967.17');
      cy.get(`[data-cy="fuelConsumptionFact"]`).should('have.value', '17967.17');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        waybill = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', waybillPageUrlPattern);
    });
  });
});
