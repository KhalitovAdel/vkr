import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './trip.reducer';

export const TripDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tripEntity = useAppSelector(state => state.trip.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tripDetailsHeading">Trip</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{tripEntity.id}</dd>
          <dt>
            <span id="departureTime">Departure Time</span>
          </dt>
          <dd>{tripEntity.departureTime}</dd>
          <dt>
            <span id="arrivalTime">Arrival Time</span>
          </dt>
          <dd>{tripEntity.arrivalTime}</dd>
          <dt>
            <span id="tripDate">Trip Date</span>
          </dt>
          <dd>{tripEntity.tripDate ? <TextFormat value={tripEntity.tripDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="tripStatus">Trip Status</span>
          </dt>
          <dd>{tripEntity.tripStatus}</dd>
          <dt>Waybill</dt>
          <dd>{tripEntity.waybill ? tripEntity.waybill.id : ''}</dd>
          <dt>Vehicle</dt>
          <dd>{tripEntity.vehicle ? tripEntity.vehicle.id : ''}</dd>
          <dt>Driver</dt>
          <dd>{tripEntity.driver ? tripEntity.driver.id : ''}</dd>
          <dt>Route</dt>
          <dd>{tripEntity.route ? tripEntity.route.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/trip" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/trip/${tripEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TripDetail;
