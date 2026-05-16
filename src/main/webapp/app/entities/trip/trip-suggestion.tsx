import React, { useEffect, useState } from 'react';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getRoutes } from 'app/entities/route/route.reducer';
import { formatRouteLabel } from 'app/shared/util/entity-labels';

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
  const dispatch = useAppDispatch();
  const routes = useAppSelector(state => state.route.entities);
  const routesLoading = useAppSelector(state => state.route.loading);

  const [form, setForm] = useState<Omit<TripSuggestionRequest, 'routeId'> & { routeId: number | '' }>({
    routeId: '',
    tripDate: '',
    departureTime: '',
  });
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<SuggestedVehicle | null>(null);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    dispatch(getRoutes({}));
  }, [dispatch]);

  const handleChange = (key: keyof typeof form, value: string | number) => {
    setForm(prev => ({ ...prev, [key]: value }));
  };

  const suggestVehicle = async () => {
    if (!form.routeId) {
      return;
    }

    setLoading(true);
    setResult(null);
    setNotFound(false);
    const payload: TripSuggestionRequest = {
      routeId: form.routeId,
      tripDate: form.tripDate,
      departureTime: form.departureTime,
    };
    try {
      const response = await axios.post<SuggestedVehicle>('/api/trips/suggest-vehicle', payload);
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
        <label className="form-label" htmlFor="suggestionRouteId">
          Маршрут
        </label>
        <select
          id="suggestionRouteId"
          className="form-select"
          data-cy="suggestionRouteId"
          value={form.routeId}
          onChange={e => handleChange('routeId', e.target.value ? Number(e.target.value) : '')}
          disabled={routesLoading}
        >
          <option value="">{routesLoading ? 'Загрузка маршрутов...' : 'Выберите маршрут'}</option>
          {routes.map(route => (
            <option value={route.id} key={route.id}>
              {formatRouteLabel(route)}
            </option>
          ))}
        </select>
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
      <button
        type="button"
        className="btn btn-primary"
        data-cy="suggestionSubmit"
        onClick={suggestVehicle}
        disabled={loading || !form.routeId || !form.tripDate || !form.departureTime}
      >
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
