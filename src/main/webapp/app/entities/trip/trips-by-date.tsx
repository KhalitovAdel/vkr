import React, { useState } from 'react';
import axios from 'axios';

type TripRow = {
  id: number;
  departureTime: string;
  arrivalTime: string;
  tripStatus: string;
};

const TripsByDate = () => {
  const [date, setDate] = useState('');
  const [items, setItems] = useState<TripRow[]>([]);

  const load = async () => {
    const response = await axios.get<TripRow[]>('/api/trips/by-date', { params: { date } });
    setItems(response.data);
  };

  return (
    <div className="container mt-3">
      <h4>Рейсы по дате</h4>
      <input className="form-control mb-2" type="date" data-cy="tripsByDatePicker" value={date} onChange={e => setDate(e.target.value)} />
      <button type="button" className="btn btn-primary mb-3" data-cy="tripsByDateLoad" onClick={load} disabled={!date}>
        Показать
      </button>
      <ul data-cy="tripsByDateList">
        {items.map(item => (
          <li key={item.id} data-cy="tripsByDateRow">
            #{item.id} {item.departureTime}-{item.arrivalTime} ({item.tripStatus})
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TripsByDate;
