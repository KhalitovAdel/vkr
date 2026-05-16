import dayjs from 'dayjs';

import { formatDriverLabel, formatRouteLabel, formatTripLabel, formatVehicleLabel, formatWaybillLabel } from './entity-labels';

describe('entity-labels', () => {
  it('formats route with number and name', () => {
    expect(formatRouteLabel({ id: 1, routeNumber: '12', routeName: 'Центр' })).toBe('12 — Центр');
  });

  it('formats vehicle with plate and model', () => {
    expect(formatVehicleLabel({ id: 2, stateNumber: 'А123ВС716', model: 'ЛиАЗ-5292' })).toBe('А123ВС716 · ЛиАЗ-5292');
  });

  it('formats driver with name and tab number', () => {
    expect(formatDriverLabel({ id: 3, fullName: 'Иванов И.И.', employeeNumber: '1042' })).toBe('Иванов И.И. (1042)');
  });

  it('formats waybill with document number', () => {
    expect(formatWaybillLabel({ id: 4, documentNumber: 'PL-2026-001' })).toBe('ПЛ PL-2026-001');
  });

  it('formats trip summary', () => {
    expect(
      formatTripLabel({
        id: 5,
        tripDate: dayjs('2026-05-16'),
        departureTime: '09:30',
        route: { id: 1, routeNumber: '12', routeName: 'Центр' },
      }),
    ).toBe('Рейс №5 · 16.05.2026 09:30 · 12 — Центр');
  });
});
