import { RouteType } from 'app/shared/model/enumerations/route-type.model';

export interface IRoute {
  id?: number;
  routeNumber?: string;
  routeName?: string;
  length?: number;
  routeType?: keyof typeof RouteType;
}

export const defaultValue: Readonly<IRoute> = {};
