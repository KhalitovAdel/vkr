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

describe('Driver e2e test', () => {
  const driverPageUrl = '/driver';
  const driverPageUrlPattern = new RegExp('/driver(\\?.*)?$');
  let username: string;
  let password: string;
  const driverSample = {
    employeeNumber: 'zowie shocked',
    fullName: 'graduate custom impure',
    licenseCategory: 'oof firm',
    experience: 1428,
    hireDate: '2026-03-26',
  };

  let driver;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/drivers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/drivers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/drivers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (driver) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/drivers/${driver.id}`,
      }).then(() => {
        driver = undefined;
      });
    }
  });

  it('Drivers menu should load Drivers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('driver');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Driver').should('exist');
    cy.url().should('match', driverPageUrlPattern);
  });

  describe('Driver page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(driverPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Driver page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/driver/new$'));
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/drivers',
          body: driverSample,
        }).then(({ body }) => {
          driver = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/drivers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [driver],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(driverPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Driver page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('driver');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('edit button click should load edit Driver page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('edit button click should load edit Driver page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('last delete button click should delete instance of Driver', () => {
        cy.intercept('GET', '/api/drivers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('driver').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);

        driver = undefined;
      });
    });
  });

  describe('new Driver page', () => {
    beforeEach(() => {
      cy.visit(driverPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Driver');
    });

    it('should create an instance of Driver', () => {
      cy.get(`[data-cy="employeeNumber"]`).type('that');
      cy.get(`[data-cy="employeeNumber"]`).should('have.value', 'that');

      cy.get(`[data-cy="fullName"]`).type('worth depend properly');
      cy.get(`[data-cy="fullName"]`).should('have.value', 'worth depend properly');

      cy.get(`[data-cy="licenseCategory"]`).type('worthy fon');
      cy.get(`[data-cy="licenseCategory"]`).should('have.value', 'worthy fon');

      cy.get(`[data-cy="experience"]`).type('18209');
      cy.get(`[data-cy="experience"]`).should('have.value', '18209');

      cy.get(`[data-cy="hireDate"]`).type('2026-03-26');
      cy.get(`[data-cy="hireDate"]`).blur();
      cy.get(`[data-cy="hireDate"]`).should('have.value', '2026-03-26');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        driver = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', driverPageUrlPattern);
    });
  });
});
