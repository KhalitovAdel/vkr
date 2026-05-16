import dayjs from 'dayjs';

import { IDriver } from 'app/shared/model/driver.model';
import { IRoute } from 'app/shared/model/route.model';
import { IStop } from 'app/shared/model/stop.model';
import { ITrip } from 'app/shared/model/trip.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { IWaybill } from 'app/shared/model/waybill.model';
import { tripStatusRu } from 'app/shared/util/enum-labels-ru';

export const formatRouteLabel = (route?: IRoute | null): string => {
  if (!route?.id) {
    return '';
  }
  const number = route.routeNumber?.trim();
  const name = route.routeName?.trim();
  if (number && name) {
    return `${number} — ${name}`;
  }
  return number || name || `Маршрут №${route.id}`;
};

export const formatVehicleLabel = (vehicle?: IVehicle | null): string => {
  if (!vehicle?.id) {
    return '';
  }
  const plate = vehicle.stateNumber?.trim();
  const model = vehicle.model?.trim();
  if (plate && model) {
    return `${plate} · ${model}`;
  }
  return plate || model || `ТС №${vehicle.id}`;
};

export const formatDriverLabel = (driver?: IDriver | null): string => {
  if (!driver?.id) {
    return '';
  }
  const name = driver.fullName?.trim();
  const employeeNumber = driver.employeeNumber?.trim();
  if (name && employeeNumber) {
    return `${name} (${employeeNumber})`;
  }
  return name || employeeNumber || `Водитель №${driver.id}`;
};

export const formatWaybillLabel = (waybill?: IWaybill | null): string => {
  if (!waybill?.id) {
    return '';
  }
  const documentNumber = waybill.documentNumber?.trim();
  if (documentNumber) {
    return `ПЛ ${documentNumber}`;
  }
  return `Путевой лист №${waybill.id}`;
};

export const formatStopLabel = (stop?: IStop | null): string => {
  if (!stop?.id) {
    return '';
  }
  return stop.name?.trim() || `Остановка №${stop.id}`;
};

export const formatTripLabel = (trip?: ITrip | null): string => {
  if (!trip?.id) {
    return '';
  }
  const parts: string[] = [`Рейс №${trip.id}`];
  const date = trip.tripDate ? dayjs(trip.tripDate).format('DD.MM.YYYY') : '';
  const time = trip.departureTime?.trim();
  const schedule = [date, time].filter(Boolean).join(' ');
  if (schedule) {
    parts.push(schedule);
  }
  const routeLabel = formatRouteLabel(trip.route);
  if (routeLabel) {
    parts.push(routeLabel);
  } else if (trip.tripStatus) {
    parts.push(tripStatusRu(trip.tripStatus));
  }
  return parts.join(' · ');
};
