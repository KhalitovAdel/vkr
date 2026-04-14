import React, { useState } from 'react';
import axios from 'axios';

import { tripStatusRu } from 'app/shared/util/enum-labels-ru';

type TripRow = {
  id: number;
  departureTime: string;
  arrivalTime: string;
  tripStatus: string;
};

const DriverSchedule = () => {
  const [driverId, setDriverId] = useState('');
  const [date, setDate] = useState('');
  const [items, setItems] = useState<TripRow[]>([]);

  const load = async () => {
    const response = await axios.get<TripRow[]>(`/api/trips/driver/${driverId}`, { params: { date } });
    setItems(response.data);
  };

  return (
    <div className="container mt-3">
      <h4>Расписание водителя</h4>
      <input
        className="form-control mb-2"
        type="number"
        placeholder="Идентификатор водителя"
        data-cy="driverScheduleId"
        value={driverId}
        onChange={e => setDriverId(e.target.value)}
      />
      <input className="form-control mb-2" type="date" data-cy="driverScheduleDate" value={date} onChange={e => setDate(e.target.value)} />
      <button type="button" className="btn btn-primary mb-3" data-cy="driverScheduleLoad" onClick={load} disabled={!driverId || !date}>
        Показать
      </button>
      <ul data-cy="driverScheduleList">
        {items.map(item => (
          <li key={item.id} data-cy="driverScheduleRow">
            #{item.id} {item.departureTime}-{item.arrivalTime} ({tripStatusRu(item.tripStatus)})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default DriverSchedule;
