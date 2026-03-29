import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './vehicle.reducer';

export const VehicleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const vehicleEntity = useAppSelector(state => state.vehicle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="vehicleDetailsHeading">Vehicle</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{vehicleEntity.id}</dd>
          <dt>
            <span id="stateNumber">State Number</span>
          </dt>
          <dd>{vehicleEntity.stateNumber}</dd>
          <dt>
            <span id="model">Model</span>
          </dt>
          <dd>{vehicleEntity.model}</dd>
          <dt>
            <span id="vehicleType">Vehicle Type</span>
          </dt>
          <dd>{vehicleEntity.vehicleType}</dd>
          <dt>
            <span id="capacity">Capacity</span>
          </dt>
          <dd>{vehicleEntity.capacity}</dd>
          <dt>
            <span id="passengerCapacity">Passenger Capacity</span>
          </dt>
          <dd>{vehicleEntity.passengerCapacity}</dd>
          <dt>
            <span id="year">Year</span>
          </dt>
          <dd>{vehicleEntity.year}</dd>
          <dt>
            <span id="technicalStatus">Technical Status</span>
          </dt>
          <dd>{vehicleEntity.technicalStatus}</dd>
          <dt>
            <span id="mileage">Mileage</span>
          </dt>
          <dd>{vehicleEntity.mileage}</dd>
        </dl>
        <Button as={Link as any} to="/vehicle" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/vehicle/${vehicleEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default VehicleDetail;
