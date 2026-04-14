import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getTrips } from 'app/entities/trip/trip.reducer';
import { getEntities as getVehicles } from 'app/entities/vehicle/vehicle.reducer';
import { EventType } from 'app/shared/model/enumerations/event-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { eventTypeRu } from 'app/shared/util/enum-labels-ru';

import { createEntity, getEntity, reset, updateEntity } from './event.reducer';

export const EventUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const trips = useAppSelector(state => state.trip.entities);
  const vehicles = useAppSelector(state => state.vehicle.entities);
  const eventEntity = useAppSelector(state => state.event.entity);
  const loading = useAppSelector(state => state.event.loading);
  const updating = useAppSelector(state => state.event.updating);
  const updateSuccess = useAppSelector(state => state.event.updateSuccess);
  const eventTypeValues = Object.keys(EventType);

  const handleClose = () => {
    navigate('/event');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTrips({}));
    dispatch(getVehicles({}));
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
    values.eventTime = convertDateTimeToServer(values.eventTime);

    const entity = {
      ...eventEntity,
      ...values,
      trip: trips.find(it => it.id.toString() === values.trip?.toString()),
      vehicle: vehicles.find(it => it.id.toString() === values.vehicle?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          eventTime: displayDefaultDateTime(),
        }
      : {
          eventType: 'BREAKDOWN',
          ...eventEntity,
          eventTime: convertDateTimeFromServer(eventEntity.eventTime),
          trip: eventEntity?.trip?.id,
          vehicle: eventEntity?.vehicle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.event.home.createOrEditLabel" data-cy="EventCreateUpdateHeading">
            Создание или редактирование события
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Загрузка…</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="event-id" label="№" validate={{ required: true }} />}
              <ValidatedField label="Тип события" id="event-eventType" name="eventType" data-cy="eventType" type="select">
                {eventTypeValues.map(eventType => (
                  <option value={eventType} key={eventType}>
                    {eventTypeRu(eventType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Время события"
                id="event-eventTime"
                name="eventTime"
                data-cy="eventTime"
                type="datetime-local"
                placeholder="ГГГГ-ММ-ДД чч:мм"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                }}
              />
              <ValidatedField label="Описание" id="event-description" name="description" data-cy="description" type="textarea" />
              <ValidatedField id="event-trip" name="trip" data-cy="trip" label="Рейс" type="select">
                <option value="" key="0" />
                {trips
                  ? trips.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="event-vehicle" name="vehicle" data-cy="vehicle" label="Транспортное средство" type="select">
                <option value="" key="0" />
                {vehicles
                  ? vehicles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/event" replace variant="info">
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

export default EventUpdate;
