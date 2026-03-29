import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Waybill from './waybill';
import WaybillDeleteDialog from './waybill-delete-dialog';
import WaybillDetail from './waybill-detail';
import WaybillUpdate from './waybill-update';

const WaybillRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Waybill />} />
    <Route path="new" element={<WaybillUpdate />} />
    <Route path=":id">
      <Route index element={<WaybillDetail />} />
      <Route path="edit" element={<WaybillUpdate />} />
      <Route path="delete" element={<WaybillDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WaybillRoutes;
