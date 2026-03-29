import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RouteStop from './route-stop';
import RouteStopDeleteDialog from './route-stop-delete-dialog';
import RouteStopDetail from './route-stop-detail';
import RouteStopUpdate from './route-stop-update';

const RouteStopRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RouteStop />} />
    <Route path="new" element={<RouteStopUpdate />} />
    <Route path=":id">
      <Route index element={<RouteStopDetail />} />
      <Route path="edit" element={<RouteStopUpdate />} />
      <Route path="delete" element={<RouteStopDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RouteStopRoutes;
