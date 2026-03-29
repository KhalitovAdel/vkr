import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Trip from './trip';
import TripDeleteDialog from './trip-delete-dialog';
import TripDetail from './trip-detail';
import TripSuggestion from './trip-suggestion';
import TripUpdate from './trip-update';

const TripRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Trip />} />
    <Route path="new" element={<TripUpdate />} />
    <Route path="suggestion" element={<TripSuggestion />} />
    <Route path=":id">
      <Route index element={<TripDetail />} />
      <Route path="edit" element={<TripUpdate />} />
      <Route path="delete" element={<TripDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TripRoutes;
