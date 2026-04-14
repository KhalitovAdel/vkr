// ***********************************************************
// This support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

import './account';
import './commands';
import './navbar';
import './entity';
import './management';

function toSafeScreenshotName(input: string) {
  return (
    input
      // Windows reserved chars + common path separators
      .replace(/[<>:"/\\|?*\u0000-\u001F]/g, ' ')
      .replace(/\s+/g, ' ')
      .trim()
      .slice(0, 180)
  );
}

afterEach(function () {
  const currentTest = this.currentTest;
  if (!currentTest || currentTest.state !== 'passed') return;

  const rel = Cypress.spec?.relative ?? '';
  if (rel.includes('ui-test-cases.cy.ts') || rel.includes('vkr-entity-screenshots.cy.ts')) {
    return;
  }

  const spec = Cypress.spec?.name ?? 'spec';
  const titlePath =
    typeof currentTest.titlePath === 'function' ? currentTest.titlePath() : [currentTest.title ?? 'test'];

  const name = toSafeScreenshotName([spec, ...titlePath].join(' -- '));
  // In `cypress run` (headless), `runner` capture may be unavailable — use viewport to always produce files.
  cy.screenshot(name, { capture: 'viewport' });
});
