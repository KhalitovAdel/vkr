import dayjs from 'dayjs';

export interface IWaybill {
  id?: number;
  documentNumber?: string;
  actualDeparture?: dayjs.Dayjs | null;
  actualReturn?: dayjs.Dayjs | null;
  mileageStart?: number | null;
  mileageEnd?: number | null;
  fuelConsumptionPlan?: number | null;
  fuelConsumptionFact?: number | null;
}

export const defaultValue: Readonly<IWaybill> = {};
