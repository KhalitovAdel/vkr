import dayjs from 'dayjs';

import { EventType } from 'app/shared/model/enumerations/event-type.model';
import { ITrip } from 'app/shared/model/trip.model';
import { IVehicle } from 'app/shared/model/vehicle.model';

export interface IEvent {
  id?: number;
  eventType?: keyof typeof EventType;
  eventTime?: dayjs.Dayjs;
  description?: string | null;
  trip?: ITrip | null;
  vehicle?: IVehicle | null;
}

export const defaultValue: Readonly<IEvent> = {};
