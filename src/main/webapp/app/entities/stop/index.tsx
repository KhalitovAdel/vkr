import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Stop from './stop';
import StopDeleteDialog from './stop-delete-dialog';
import StopDetail from './stop-detail';
import StopUpdate from './stop-update';

const StopRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Stop />} />
    <Route path="new" element={<StopUpdate />} />
    <Route path=":id">
      <Route index element={<StopDetail />} />
      <Route path="edit" element={<StopUpdate />} />
      <Route path="delete" element={<StopDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StopRoutes;
