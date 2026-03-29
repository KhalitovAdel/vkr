import { CapacityType } from 'app/shared/model/enumerations/capacity-type.model';
import { TechnicalStatus } from 'app/shared/model/enumerations/technical-status.model';
import { VehicleType } from 'app/shared/model/enumerations/vehicle-type.model';

export interface IVehicle {
  id?: number;
  stateNumber?: string;
  model?: string;
  vehicleType?: keyof typeof VehicleType;
  capacity?: keyof typeof CapacityType;
  passengerCapacity?: number;
  year?: number;
  technicalStatus?: keyof typeof TechnicalStatus;
  mileage?: number;
}

export const defaultValue: Readonly<IVehicle> = {};
