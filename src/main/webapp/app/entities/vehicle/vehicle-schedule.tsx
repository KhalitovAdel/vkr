import React, { useState } from 'react';
import axios from 'axios';

type TripRow = {
  id: number;
  departureTime: string;
  arrivalTime: string;
  tripStatus: string;
};

const VehicleSchedule = () => {
  const [vehicleId, setVehicleId] = useState('');
  const [date, setDate] = useState('');
  const [items, setItems] = useState<TripRow[]>([]);

  const load = async () => {
    const response = await axios.get<TripRow[]>(`/api/trips/vehicle/${vehicleId}`, { params: { date } });
    setItems(response.data);
  };

  return (
    <div className="container mt-3">
      <h4>Расписание ТС</h4>
      <input
        className="form-control mb-2"
        type="number"
        placeholder="Vehicle ID"
        data-cy="vehicleScheduleId"
        value={vehicleId}
        onChange={e => setVehicleId(e.target.value)}
      />
      <input className="form-control mb-2" type="date" data-cy="vehicleScheduleDate" value={date} onChange={e => setDate(e.target.value)} />
      <button type="button" className="btn btn-primary mb-3" data-cy="vehicleScheduleLoad" onClick={load} disabled={!vehicleId || !date}>
        Показать
      </button>
      <ul data-cy="vehicleScheduleList">
        {items.map(item => (
          <li key={item.id} data-cy="vehicleScheduleRow">
            #{item.id} {item.departureTime}-{item.arrivalTime} ({item.tripStatus})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default VehicleSchedule;
