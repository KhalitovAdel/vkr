import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDrivers } from 'app/entities/driver/driver.reducer';
import { getEntities as getRoutes } from 'app/entities/route/route.reducer';
import { getEntities as getVehicles } from 'app/entities/vehicle/vehicle.reducer';
import { getEntities as getWaybills } from 'app/entities/waybill/waybill.reducer';
import { TripStatus } from 'app/shared/model/enumerations/trip-status.model';
import { tripStatusRu } from 'app/shared/util/enum-labels-ru';

import { createEntity, getEntity, reset, updateEntity } from './trip.reducer';

export const TripUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const waybills = useAppSelector(state => state.waybill.entities);
  const vehicles = useAppSelector(state => state.vehicle.entities);
  const drivers = useAppSelector(state => state.driver.entities);
  const routes = useAppSelector(state => state.route.entities);
  const tripEntity = useAppSelector(state => state.trip.entity);
  const loading = useAppSelector(state => state.trip.loading);
  const updating = useAppSelector(state => state.trip.updating);
  const updateSuccess = useAppSelector(state => state.trip.updateSuccess);
  const tripStatusValues = Object.keys(TripStatus);

  const handleClose = () => {
    navigate('/trip');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getWaybills({}));
    dispatch(getVehicles({}));
    dispatch(getDrivers({}));
    dispatch(getRoutes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...tripEntity,
      ...values,
      waybill: waybills.find(it => it.id.toString() === values.waybill?.toString()),
      vehicle: vehicles.find(it => it.id.toString() === values.vehicle?.toString()),
      driver: drivers.find(it => it.id.toString() === values.driver?.toString()),
      route: routes.find(it => it.id.toString() === values.route?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          tripStatus: 'SCHEDULED',
          ...tripEntity,
          waybill: tripEntity?.waybill?.id,
          vehicle: tripEntity?.vehicle?.id,
          driver: tripEntity?.driver?.id,
          route: tripEntity?.route?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.trip.home.createOrEditLabel" data-cy="TripCreateUpdateHeading">
            Создание или редактирование рейса
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Загрузка…</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="trip-id" label="№" validate={{ required: true }} />}
              <ValidatedField
                label="Время отправления"
                id="trip-departureTime"
                name="departureTime"
                data-cy="departureTime"
                type="time"
                placeholder="HH:mm"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField
                label="Время прибытия"
                id="trip-arrivalTime"
                name="arrivalTime"
                data-cy="arrivalTime"
                type="time"
                placeholder="HH:mm"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField
                label="Дата рейса"
                id="trip-tripDate"
                name="tripDate"
                data-cy="tripDate"
                type="date"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField label="Статус рейса" id="trip-tripStatus" name="tripStatus" data-cy="tripStatus" type="select">
                {tripStatusValues.map(tripStatus => (
                  <option value={tripStatus} key={tripStatus}>
                    {tripStatusRu(tripStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="trip-waybill" name="waybill" data-cy="waybill" label="Путевой лист" type="select">
                <option value="" key="0" />
                {waybills
                  ? waybills.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="trip-vehicle" name="vehicle" data-cy="vehicle" label="Транспортное средство" type="select">
                <option value="" key="0" />
                {vehicles
                  ? vehicles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="trip-driver" name="driver" data-cy="driver" label="Водитель" type="select">
                <option value="" key="0" />
                {drivers
                  ? drivers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="trip-route" name="route" data-cy="route" label="Маршрут" type="select">
                <option value="" key="0" />
                {routes
                  ? routes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/trip" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Назад</span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Сохранить
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TripUpdate;
