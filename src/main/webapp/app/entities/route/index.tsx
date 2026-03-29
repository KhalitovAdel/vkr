import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RouteList from './route';
import RouteDeleteDialog from './route-delete-dialog';
import RouteDetail from './route-detail';
import RouteUpdate from './route-update';

const RouteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RouteList />} />
    <Route path="new" element={<RouteUpdate />} />
    <Route path=":id">
      <Route index element={<RouteDetail />} />
      <Route path="edit" element={<RouteUpdate />} />
      <Route path="delete" element={<RouteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RouteRoutes;
