import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { capacityTypeRu, technicalStatusRu, vehicleTypeRu } from 'app/shared/util/enum-labels-ru';

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
        <h2 data-cy="vehicleDetailsHeading">Транспортное средство</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">№</span>
          </dt>
          <dd>{vehicleEntity.id}</dd>
          <dt>
            <span id="stateNumber">Государственный номер</span>
          </dt>
          <dd>{vehicleEntity.stateNumber}</dd>
          <dt>
            <span id="model">Модель</span>
          </dt>
          <dd>{vehicleEntity.model}</dd>
          <dt>
            <span id="vehicleType">Тип ТС</span>
          </dt>
          <dd>{vehicleTypeRu(vehicleEntity.vehicleType)}</dd>
          <dt>
            <span id="capacity">Класс вместимости</span>
          </dt>
          <dd>{capacityTypeRu(vehicleEntity.capacity)}</dd>
          <dt>
            <span id="passengerCapacity">Пассажировместимость</span>
          </dt>
          <dd>{vehicleEntity.passengerCapacity}</dd>
          <dt>
            <span id="year">Год выпуска</span>
          </dt>
          <dd>{vehicleEntity.year}</dd>
          <dt>
            <span id="technicalStatus">Техническое состояние</span>
          </dt>
          <dd>{technicalStatusRu(vehicleEntity.technicalStatus)}</dd>
          <dt>
            <span id="mileage">Пробег</span>
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
