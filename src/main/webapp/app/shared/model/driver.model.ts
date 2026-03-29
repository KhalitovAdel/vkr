import dayjs from 'dayjs';

export interface IDriver {
  id?: number;
  employeeNumber?: string;
  fullName?: string;
  licenseCategory?: string;
  experience?: number;
  hireDate?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IDriver> = {};
