import React, { useState } from 'react';
import axios from 'axios';

type SuggestedVehicle = {
  id: number;
  stateNumber: string;
  model: string;
  passengerCapacity: number;
};

type TripSuggestionRequest = {
  routeId: number;
  tripDate: string;
  departureTime: string;
};

const TripSuggestion = () => {
  const [form, setForm] = useState<TripSuggestionRequest>({ routeId: 0, tripDate: '', departureTime: '' });
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<SuggestedVehicle | null>(null);
  const [notFound, setNotFound] = useState(false);

  const handleChange = (key: keyof TripSuggestionRequest, value: string | number) => {
    setForm(prev => ({ ...prev, [key]: value }));
  };

  const suggestVehicle = async () => {
    setLoading(true);
    setResult(null);
    setNotFound(false);
    try {
      const response = await axios.post<SuggestedVehicle>('/api/trips/suggest-vehicle', form);
      if (response.status === 204 || !response.data) {
        setNotFound(true);
      } else {
        setResult(response.data);
      }
    } catch {
      setNotFound(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-3">
      <h4>Подбор транспортного средства</h4>
      <div className="mb-2">
        <label className="form-label">ID маршрута</label>
        <input
          className="form-control"
          type="number"
          data-cy="suggestionRouteId"
          value={form.routeId}
          onChange={e => handleChange('routeId', Number(e.target.value))}
        />
      </div>
      <div className="mb-2">
        <label className="form-label">Дата рейса</label>
        <input
          className="form-control"
          type="date"
          data-cy="suggestionTripDate"
          value={form.tripDate}
          onChange={e => handleChange('tripDate', e.target.value)}
        />
      </div>
      <div className="mb-3">
        <label className="form-label">Время отправления</label>
        <input
          className="form-control"
          type="time"
          data-cy="suggestionDepartureTime"
          value={form.departureTime}
          onChange={e => handleChange('departureTime', e.target.value)}
        />
      </div>
      <button type="button" className="btn btn-primary" data-cy="suggestionSubmit" onClick={suggestVehicle} disabled={loading}>
        {loading ? 'Подбор...' : 'Подобрать ТС'}
      </button>

      {result && (
        <div className="alert alert-success mt-3" data-cy="suggestionResult">
          <div>Госномер: {result.stateNumber}</div>
          <div>Модель: {result.model}</div>
          <div>Вместимость: {result.passengerCapacity}</div>
        </div>
      )}

      {notFound && (
        <div className="alert alert-warning mt-3" data-cy="suggestionNotFound">
          Подходящее ТС не найдено.
        </div>
      )}
    </div>
  );
};

export default TripSuggestion;
