import React, { useEffect, useState } from 'react';
import axios from 'axios';

type FleetStats = {
  total: number;
  operational: number;
  repair: number;
};

const FleetStatus = () => {
  const [stats, setStats] = useState<FleetStats | null>(null);

  useEffect(() => {
    axios.get<FleetStats>('/api/reports/fleet').then(response => setStats(response.data));
  }, []);

  return (
    <div className="container mt-3">
      <h4>Статус парка</h4>
      {stats ? (
        <div className="row" data-cy="fleetStatusReady">
          <div className="col-md-4" data-cy="fleetStatusTotal">
            Всего: {stats.total}
          </div>
          <div className="col-md-4" data-cy="fleetStatusOperational">
            Исправные: {stats.operational}
          </div>
          <div className="col-md-4" data-cy="fleetStatusRepair">
            В ремонте: {stats.repair}
          </div>
        </div>
      ) : (
        <div data-cy="fleetStatusLoading">Загрузка...</div>
      )}
    </div>
  );
};

export default FleetStatus;
