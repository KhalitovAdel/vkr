/** IDs created via API during UI scenario tests — delete in order: trips → waybills → vehicles → drivers → routes. */
export interface SeededIds {
  tripIds: number[];
  waybillIds: number[];
  vehicleIds: number[];
  driverIds: number[];
  routeIds: number[];
}

export const emptyIds = (): SeededIds => ({
  tripIds: [],
  waybillIds: [],
  vehicleIds: [],
  driverIds: [],
  routeIds: [],
});

export const routeCityBody = (suffix: string) => ({
  routeNumber: `C${suffix}`.slice(0, 10),
  routeName: `City route ${suffix}`.slice(0, 100),
  length: 10.5,
  routeType: 'CITY',
});

export const routeSuburbBody = (suffix: string) => ({
  routeNumber: `S${suffix}`.slice(0, 10),
  routeName: `Suburb route ${suffix}`.slice(0, 100),
  length: 25,
  routeType: 'SUBURB',
});

export const vehicleBody = (
  suffix: string,
  opts: { technicalStatus?: 'OPERATIONAL' | 'REPAIR'; passengerCapacity?: number; capacity?: string } = {},
) => ({
  stateNumber: `V${suffix}`.slice(0, 10),
  model: 'CypressBus',
  vehicleType: 'BUS',
  capacity: opts.capacity ?? 'MEDIUM',
  passengerCapacity: opts.passengerCapacity ?? 40,
  year: 2020,
  technicalStatus: opts.technicalStatus ?? 'OPERATIONAL',
  mileage: 5000,
});

export const driverBody = (suffix: string, experience: number) => ({
  employeeNumber: `E${suffix}`.slice(0, 10),
  fullName: `Driver ${suffix}`.slice(0, 100),
  licenseCategory: 'D',
  experience,
  hireDate: '2018-01-01',
});

export const waybillBody = (suffix: string) => ({
  documentNumber: `W${suffix}`.slice(0, 20),
});

/** Минимальное тело для POST /api/events (скриншоты списка и тесты). */
export const eventBody = (suffix: string) => ({
  eventType: 'DELAY',
  eventTime: '2030-06-15T12:00:00.000Z',
  description: `Демонстрационное событие ${suffix}`.slice(0, 500),
});

export const tripBody = (params: {
  tripDate: string;
  departureTime: string;
  arrivalTime: string;
  tripStatus?: string;
  vehicleId?: number;
  driverId?: number;
  routeId?: number;
  waybillId?: number;
}) => {
  const body: Record<string, unknown> = {
    tripDate: params.tripDate,
    departureTime: params.departureTime,
    arrivalTime: params.arrivalTime,
    tripStatus: params.tripStatus ?? 'SCHEDULED',
  };
  if (params.vehicleId != null) {
    body.vehicle = { id: params.vehicleId };
  }
  if (params.driverId != null) {
    body.driver = { id: params.driverId };
  }
  if (params.routeId != null) {
    body.route = { id: params.routeId };
  }
  if (params.waybillId != null) {
    body.waybill = { id: params.waybillId };
  }
  return body;
};
