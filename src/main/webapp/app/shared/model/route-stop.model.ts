import { IRoute } from 'app/shared/model/route.model';
import { IStop } from 'app/shared/model/stop.model';

export interface IRouteStop {
  id?: number;
  stopOrder?: number;
  distanceFromPrev?: number | null;
  route?: IRoute | null;
  stop?: IStop | null;
}

export const defaultValue: Readonly<IRouteStop> = {};
