import dayjs from 'dayjs';

import { IDriver } from 'app/shared/model/driver.model';
import { TripStatus } from 'app/shared/model/enumerations/trip-status.model';
import { IRoute } from 'app/shared/model/route.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { IWaybill } from 'app/shared/model/waybill.model';

export interface ITrip {
  id?: number;
  departureTime?: string;
  arrivalTime?: string;
  tripDate?: dayjs.Dayjs;
  tripStatus?: keyof typeof TripStatus;
  waybill?: IWaybill | null;
  vehicle?: IVehicle | null;
  driver?: IDriver | null;
  route?: IRoute | null;
}

export const defaultValue: Readonly<ITrip> = {};
