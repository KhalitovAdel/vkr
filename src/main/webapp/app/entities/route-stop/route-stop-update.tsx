import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getRoutes } from 'app/entities/route/route.reducer';
import { getEntities as getStops } from 'app/entities/stop/stop.reducer';

import { createEntity, getEntity, reset, updateEntity } from './route-stop.reducer';

export const RouteStopUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const routes = useAppSelector(state => state.route.entities);
  const stops = useAppSelector(state => state.stop.entities);
  const routeStopEntity = useAppSelector(state => state.routeStop.entity);
  const loading = useAppSelector(state => state.routeStop.loading);
  const updating = useAppSelector(state => state.routeStop.updating);
  const updateSuccess = useAppSelector(state => state.routeStop.updateSuccess);

  const handleClose = () => {
    navigate('/route-stop');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getRoutes({}));
    dispatch(getStops({}));
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
    if (values.stopOrder !== undefined && typeof values.stopOrder !== 'number') {
      values.stopOrder = Number(values.stopOrder);
    }
    if (values.distanceFromPrev !== undefined && typeof values.distanceFromPrev !== 'number') {
      values.distanceFromPrev = Number(values.distanceFromPrev);
    }

    const entity = {
      ...routeStopEntity,
      ...values,
      route: routes.find(it => it.id.toString() === values.route?.toString()),
      stop: stops.find(it => it.id.toString() === values.stop?.toString()),
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
          ...routeStopEntity,
          route: routeStopEntity?.route?.id,
          stop: routeStopEntity?.stop?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.routeStop.home.createOrEditLabel" data-cy="RouteStopCreateUpdateHeading">
            Создать или отредактировать Route Stop
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="route-stop-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Stop Order"
                id="route-stop-stopOrder"
                name="stopOrder"
                data-cy="stopOrder"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  validate: v => isNumber(v) || 'Это поле должно быть число.',
                }}
              />
              <ValidatedField
                label="Distance From Prev"
                id="route-stop-distanceFromPrev"
                name="distanceFromPrev"
                data-cy="distanceFromPrev"
                type="text"
              />
              <ValidatedField id="route-stop-route" name="route" data-cy="route" label="Route" type="select">
                <option value="" key="0" />
                {routes
                  ? routes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="route-stop-stop" name="stop" data-cy="stop" label="Stop" type="select">
                <option value="" key="0" />
                {stops
                  ? stops.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/route-stop" replace variant="info">
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

export default RouteStopUpdate;
