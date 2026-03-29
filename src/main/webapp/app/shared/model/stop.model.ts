export interface IStop {
  id?: number;
  name?: string;
  latitude?: number | null;
  longitude?: number | null;
}

export const defaultValue: Readonly<IStop> = {};
