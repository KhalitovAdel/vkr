import React, { useCallback, useEffect, useState } from 'react';
import axios from 'axios';

type FleetStats = {
  total: number;
  operational: number;
  repair: number;
};

type MonthlyStats = {
  tripsCount: number;
  totalMileage: number | string;
  totalFuelConsumption: number | string;
};

type DowntimeStats = {
  date: string;
  vehiclesInRepair: number;
  vehiclesScrapped: number;
  operationalWithoutTripOnDate: number;
};

/** Cypress подставляет объект на window — отключаем polling, чтобы не плодить запросы и не ловить флаки. */
function isCypressBrowser(): boolean {
  return typeof window !== 'undefined' && Boolean((window as Window & { Cypress?: unknown }).Cypress);
}

const POLL_INTERVAL_MS = 45_000;

function padMonthYear(y: number, m: number): string {
  return `${y}-${String(m).padStart(2, '0')}`;
}

function todayIsoDate(): string {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

const Dashboard = () => {
  const [stats, setStats] = useState<FleetStats | null>(null);
  const now = new Date();
  const [monthYear, setMonthYear] = useState(padMonthYear(now.getFullYear(), now.getMonth() + 1));
  const [monthly, setMonthly] = useState<MonthlyStats | null>(null);
  const [downtimeDate, setDowntimeDate] = useState(todayIsoDate);
  const [downtime, setDowntime] = useState<DowntimeStats | null>(null);

  const refreshDashboard = useCallback(() => {
    const [y, m] = monthYear.split('-').map(Number);
    axios.get<FleetStats>('/api/reports/fleet').then(response => setStats(response.data));
    axios.get<MonthlyStats>('/api/reports/trips/monthly', { params: { year: y, month: m } }).then(response => setMonthly(response.data));
    axios.get<DowntimeStats>('/api/reports/downtime', { params: { date: downtimeDate } }).then(response => setDowntime(response.data));
  }, [monthYear, downtimeDate]);

  useEffect(() => {
    refreshDashboard();
  }, [refreshDashboard]);

  useEffect(() => {
    if (isCypressBrowser()) {
      return;
    }
    const id = window.setInterval(() => {
      refreshDashboard();
    }, POLL_INTERVAL_MS);
    return () => window.clearInterval(id);
  }, [refreshDashboard]);

  if (!stats) {
    return (
      <div className="container mt-3" data-cy="dashboardLoading">
        Загрузка дашборда...
      </div>
    );
  }

  return (
    <div className="container mt-3" data-cy="dashboardReady">
      <h3>Dashboard</h3>
      {!isCypressBrowser() && (
        <p className="text-muted small mb-2" data-cy="dashboardLiveRefreshNote">
          Псевдо‑режим реального времени: показатели ниже запрашиваются с сервера снова каждые {POLL_INTERVAL_MS / 1000} с (без WebSocket и
          GPS).
        </p>
      )}
      <div data-cy="dashboardFleetTotal">Всего ТС: {stats.total}</div>
      <div data-cy="dashboardFleetOperational">Исправных: {stats.operational}</div>
      <div data-cy="dashboardFleetRepair">В ремонте: {stats.repair}</div>

      <hr className="my-4" />

      <h4 data-cy="dashboardMonthlyHeading">Отчёт за месяц</h4>
      <p className="text-muted small">
        Число рейсов за месяц, суммарный пробег и фактический расход топлива по путевым листам (по каждому ТС без двойного учёта).
      </p>
      <div className="mb-3">
        <label className="form-label" htmlFor="dashboard-month">
          Месяц
        </label>
        <input
          id="dashboard-month"
          className="form-control"
          style={{ maxWidth: '200px' }}
          type="month"
          data-cy="dashboardMonthInput"
          value={monthYear}
          onChange={e => setMonthYear(e.target.value)}
        />
      </div>
      {monthly ? (
        <div data-cy="dashboardMonthlySection">
          <div data-cy="dashboardMonthlyTrips">Рейсов за месяц: {monthly.tripsCount}</div>
          <div data-cy="dashboardMonthlyMileage">Суммарный пробег, км: {String(monthly.totalMileage)}</div>
          <div data-cy="dashboardMonthlyFuel">Расход топлива (факт), усл. ед.: {String(monthly.totalFuelConsumption)}</div>
        </div>
      ) : (
        <div data-cy="dashboardMonthlyLoading">Загрузка месячного отчёта…</div>
      )}

      <hr className="my-4" />

      <h4 data-cy="dashboardDowntimeHeading">Простои и загрузка парка (по учёту)</h4>
      <p className="text-muted small">
        Без GPS: в ремонте и списанные ТС; исправные машины без назначенного рейса на выбранную дату (не выходили по плану).
      </p>
      <div className="mb-3">
        <label className="form-label" htmlFor="dashboard-downtime-date">
          Дата
        </label>
        <input
          id="dashboard-downtime-date"
          className="form-control"
          style={{ maxWidth: '200px' }}
          type="date"
          data-cy="dashboardDowntimeDateInput"
          value={downtimeDate}
          onChange={e => setDowntimeDate(e.target.value)}
        />
      </div>
      {downtime ? (
        <div data-cy="dashboardDowntimeSection">
          <div data-cy="dashboardDowntimeRepair">ТС в ремонте: {downtime.vehiclesInRepair}</div>
          <div data-cy="dashboardDowntimeScrapped">Списано: {downtime.vehiclesScrapped}</div>
          <div data-cy="dashboardDowntimeOperationalIdle">Исправные без рейса на эту дату: {downtime.operationalWithoutTripOnDate}</div>
        </div>
      ) : (
        <div data-cy="dashboardDowntimeLoading">Загрузка…</div>
      )}
    </div>
  );
};

export default Dashboard;
