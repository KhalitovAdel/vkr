import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RouteType } from 'app/shared/model/enumerations/route-type.model';

import { createEntity, getEntity, reset, updateEntity } from './route.reducer';

export const RouteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const routeEntity = useAppSelector(state => state.route.entity);
  const loading = useAppSelector(state => state.route.loading);
  const updating = useAppSelector(state => state.route.updating);
  const updateSuccess = useAppSelector(state => state.route.updateSuccess);
  const routeTypeValues = Object.keys(RouteType);

  const handleClose = () => {
    navigate('/route');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.length !== undefined && typeof values.length !== 'number') {
      values.length = Number(values.length);
    }

    const entity = {
      ...routeEntity,
      ...values,
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
          routeType: 'CITY',
          ...routeEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.route.home.createOrEditLabel" data-cy="RouteCreateUpdateHeading">
            Создать или отредактировать Route
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="route-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Route Number"
                id="route-routeNumber"
                name="routeNumber"
                data-cy="routeNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  maxLength: { value: 10, message: 'Это поле не может быть длинее, чем 10 символов.' },
                }}
              />
              <ValidatedField
                label="Route Name"
                id="route-routeName"
                name="routeName"
                data-cy="routeName"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  maxLength: { value: 100, message: 'Это поле не может быть длинее, чем 100 символов.' },
                }}
              />
              <ValidatedField
                label="Length"
                id="route-length"
                name="length"
                data-cy="length"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  validate: v => isNumber(v) || 'Это поле должно быть число.',
                }}
              />
              <ValidatedField label="Route Type" id="route-routeType" name="routeType" data-cy="routeType" type="select">
                {routeTypeValues.map(routeType => (
                  <option value={routeType} key={routeType}>
                    {routeType}
                  </option>
                ))}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/route" replace variant="info">
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

export default RouteUpdate;
