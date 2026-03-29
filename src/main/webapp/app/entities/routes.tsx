import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Driver from './driver';
import DriverSchedule from './driver/driver-schedule';
import Event from './event';
import RouteEntity from './route';
import RouteStop from './route-stop';
import Stop from './stop';
import Trip from './trip';
import Vehicle from './vehicle';
import FleetStatus from './vehicle/fleet-status';
import VehicleSchedule from './vehicle/vehicle-schedule';
import TripsByDate from './trip/trips-by-date';
import Waybill from './waybill';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/vehicle/*" element={<Vehicle />} />
        <Route path="/driver/*" element={<Driver />} />
        <Route path="/route/*" element={<RouteEntity />} />
        <Route path="/stop/*" element={<Stop />} />
        <Route path="/route-stop/*" element={<RouteStop />} />
        <Route path="/trip/*" element={<Trip />} />
        <Route path="/trip/by-date" element={<TripsByDate />} />
        <Route path="/waybill/*" element={<Waybill />} />
        <Route path="/event/*" element={<Event />} />
        <Route path="/vehicle/fleet-status" element={<FleetStatus />} />
        <Route path="/vehicle/schedule" element={<VehicleSchedule />} />
        <Route path="/driver/schedule" element={<DriverSchedule />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
