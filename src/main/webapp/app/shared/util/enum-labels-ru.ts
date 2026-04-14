/** Подписи перечислений для отображения в UI (значения для API не меняются). */

export function tripStatusRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    SCHEDULED: 'Запланирован',
    ONGOING: 'На линии',
    COMPLETED: 'Завершён',
    CANCELLED: 'Отменён',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}

export function eventTypeRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    BREAKDOWN: 'Поломка',
    ACCIDENT: 'ДТП',
    DELAY: 'Задержка',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}

export function routeTypeRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    CITY: 'Городской',
    SUBURB: 'Пригородный',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}

export function technicalStatusRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    OPERATIONAL: 'Исправно',
    REPAIR: 'В ремонте',
    SCRAPPED: 'Списано',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}

export function vehicleTypeRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    BUS: 'Автобус',
    TROLLEY: 'Троллейбус',
    TRAM: 'Трамвай',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}

export function capacityTypeRu(code: string | undefined | null): string {
  const m: Record<string, string> = {
    SMALL: 'Малая',
    MEDIUM: 'Средняя',
    LARGE: 'Большая',
  };
  return code == null || code === '' ? '' : (m[code] ?? code);
}
