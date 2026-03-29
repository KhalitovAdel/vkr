import React from 'react';
import { Route } from 'react-router';
import TripSuggestion from './trip-suggestion';

const TripSuggestionRoute = () => <Route path="/trip/suggestion" element={<TripSuggestion />} />;

export default TripSuggestionRoute;
