import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Vehicle from './vehicle';
import VehicleDeleteDialog from './vehicle-delete-dialog';
import VehicleDetail from './vehicle-detail';
import VehicleUpdate from './vehicle-update';

const VehicleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Vehicle />} />
    <Route path="new" element={<VehicleUpdate />} />
    <Route path=":id">
      <Route index element={<VehicleDetail />} />
      <Route path="edit" element={<VehicleUpdate />} />
      <Route path="delete" element={<VehicleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VehicleRoutes;
