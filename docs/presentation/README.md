# Скриншоты для презентации (Приложение4.md)

## Справочники и рейс (`images/`)

| Файл | Слайд |
|------|--------|
| `1.png` | Транспортное средство — список / форма |
| `2.png` | Водитель |
| `3.png` | Маршрут |
| `4.png` | Создание рейса |
| `select.png` | Подбор ТС |
| `waybill.png` | Путевой лист |
| `dash.png` | Dashboard |

После прогона E2E скопируйте PNG из `target/cypress/screenshots/ui-test-cases.cy.ts/vkr/`:

- `Case-01-dashboard-fleet.png` → `dashboard.png`
- `Case-02-suggest-vehicle-success.png` → `trip-suggestion.png`
- `Case-09-waybill-departure-trip-ongoing.png` → `waybill.png`

```bash
npm run e2e
cp target/cypress/screenshots/ui-test-cases.cy.ts/vkr/Case-01-dashboard-fleet.png docs/presentation/dashboard.png
cp target/cypress/screenshots/ui-test-cases.cy.ts/vkr/Case-02-suggest-vehicle-success.png docs/presentation/trip-suggestion.png
cp target/cypress/screenshots/ui-test-cases.cy.ts/vkr/Case-09-waybill-departure-trip-ongoing.png docs/presentation/waybill.png
```
