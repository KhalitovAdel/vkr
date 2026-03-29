# API Дополнений

## Подбор ТС

- `POST /api/trips/suggest-vehicle`
  - body: `{ "routeId": 1, "tripDate": "2026-03-26", "departureTime": "08:30:00" }`
  - 200: найдено ТС
  - 204: подходящее ТС не найдено

## Рейсы

- `POST /api/trips` - создание рейса с проверкой занятости ТС/водителя
- `GET /api/trips/by-date?date=2026-03-26`
- `GET /api/trips/vehicle/{vehicleId}?date=2026-03-26`
- `GET /api/trips/driver/{driverId}?date=2026-03-26`

## Путевые листы

- `POST /api/waybills/{id}/departure`
  - body: `{ "eventTime": "2026-03-26T08:00:00Z", "mileage": 12345.67 }`
- `POST /api/waybills/{id}/return`
  - body: `{ "eventTime": "2026-03-26T18:00:00Z", "mileage": 12420.11 }`

## Отчеты

- `GET /api/reports/fleet`
- `GET /api/reports/trips?date=2026-03-26`
- `GET /api/reports/trips?date=2026-03-26&vehicleId=1`
- `GET /api/reports/trips?date=2026-03-26&driverId=1`
- `GET /api/reports/trips/monthly?year=2026&month=3`
