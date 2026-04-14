import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { CapacityType } from 'app/shared/model/enumerations/capacity-type.model';
import { TechnicalStatus } from 'app/shared/model/enumerations/technical-status.model';
import { VehicleType } from 'app/shared/model/enumerations/vehicle-type.model';
import { capacityTypeRu, technicalStatusRu, vehicleTypeRu } from 'app/shared/util/enum-labels-ru';

import { createEntity, getEntity, reset, updateEntity } from './vehicle.reducer';

export const VehicleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const vehicleEntity = useAppSelector(state => state.vehicle.entity);
  const loading = useAppSelector(state => state.vehicle.loading);
  const updating = useAppSelector(state => state.vehicle.updating);
  const updateSuccess = useAppSelector(state => state.vehicle.updateSuccess);
  const vehicleTypeValues = Object.keys(VehicleType);
  const capacityTypeValues = Object.keys(CapacityType);
  const technicalStatusValues = Object.keys(TechnicalStatus);

  const handleClose = () => {
    navigate('/vehicle');
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
    if (values.passengerCapacity !== undefined && typeof values.passengerCapacity !== 'number') {
      values.passengerCapacity = Number(values.passengerCapacity);
    }
    if (values.year !== undefined && typeof values.year !== 'number') {
      values.year = Number(values.year);
    }
    if (values.mileage !== undefined && typeof values.mileage !== 'number') {
      values.mileage = Number(values.mileage);
    }

    const entity = {
      ...vehicleEntity,
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
          vehicleType: 'BUS',
          capacity: 'SMALL',
          technicalStatus: 'OPERATIONAL',
          ...vehicleEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.vehicle.home.createOrEditLabel" data-cy="VehicleCreateUpdateHeading">
            Создание или редактирование транспортного средства
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Загрузка…</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="vehicle-id" label="№" validate={{ required: true }} />}
              <ValidatedField
                label="Государственный номер"
                id="vehicle-stateNumber"
                name="stateNumber"
                data-cy="stateNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  maxLength: { value: 10, message: 'Это поле не может быть длинее, чем 10 символов.' },
                }}
              />
              <ValidatedField
                label="Модель"
                id="vehicle-model"
                name="model"
                data-cy="model"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  maxLength: { value: 50, message: 'Это поле не может быть длинее, чем 50 символов.' },
                }}
              />
              <ValidatedField label="Тип ТС" id="vehicle-vehicleType" name="vehicleType" data-cy="vehicleType" type="select">
                {vehicleTypeValues.map(vehicleType => (
                  <option value={vehicleType} key={vehicleType}>
                    {vehicleTypeRu(vehicleType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Класс вместимости" id="vehicle-capacity" name="capacity" data-cy="capacity" type="select">
                {capacityTypeValues.map(capacityType => (
                  <option value={capacityType} key={capacityType}>
                    {capacityTypeRu(capacityType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Пассажировместимость"
                id="vehicle-passengerCapacity"
                name="passengerCapacity"
                data-cy="passengerCapacity"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  validate: v => isNumber(v) || 'Это поле должно быть число.',
                }}
              />
              <ValidatedField
                label="Год выпуска"
                id="vehicle-year"
                name="year"
                data-cy="year"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  validate: v => isNumber(v) || 'Это поле должно быть число.',
                }}
              />
              <ValidatedField
                label="Техническое состояние"
                id="vehicle-technicalStatus"
                name="technicalStatus"
                data-cy="technicalStatus"
                type="select"
              >
                {technicalStatusValues.map(technicalStatus => (
                  <option value={technicalStatus} key={technicalStatus}>
                    {technicalStatusRu(technicalStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Пробег"
                id="vehicle-mileage"
                name="mileage"
                data-cy="mileage"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  validate: v => isNumber(v) || 'Это поле должно быть число.',
                }}
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/vehicle" replace variant="info">
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

export default VehicleUpdate;
