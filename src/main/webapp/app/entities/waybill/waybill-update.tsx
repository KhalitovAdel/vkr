import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './waybill.reducer';

export const WaybillUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const waybillEntity = useAppSelector(state => state.waybill.entity);
  const loading = useAppSelector(state => state.waybill.loading);
  const updating = useAppSelector(state => state.waybill.updating);
  const updateSuccess = useAppSelector(state => state.waybill.updateSuccess);

  const handleClose = () => {
    navigate('/waybill');
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
    values.actualDeparture = convertDateTimeToServer(values.actualDeparture);
    values.actualReturn = convertDateTimeToServer(values.actualReturn);
    if (values.mileageStart !== undefined && typeof values.mileageStart !== 'number') {
      values.mileageStart = Number(values.mileageStart);
    }
    if (values.mileageEnd !== undefined && typeof values.mileageEnd !== 'number') {
      values.mileageEnd = Number(values.mileageEnd);
    }
    if (values.fuelConsumptionPlan !== undefined && typeof values.fuelConsumptionPlan !== 'number') {
      values.fuelConsumptionPlan = Number(values.fuelConsumptionPlan);
    }
    if (values.fuelConsumptionFact !== undefined && typeof values.fuelConsumptionFact !== 'number') {
      values.fuelConsumptionFact = Number(values.fuelConsumptionFact);
    }

    const entity = {
      ...waybillEntity,
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
      ? {
          actualDeparture: displayDefaultDateTime(),
          actualReturn: displayDefaultDateTime(),
        }
      : {
          ...waybillEntity,
          actualDeparture: convertDateTimeFromServer(waybillEntity.actualDeparture),
          actualReturn: convertDateTimeFromServer(waybillEntity.actualReturn),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="transportSystemApp.waybill.home.createOrEditLabel" data-cy="WaybillCreateUpdateHeading">
            Создать или отредактировать Waybill
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" required readOnly id="waybill-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Document Number"
                id="waybill-documentNumber"
                name="documentNumber"
                data-cy="documentNumber"
                type="text"
                validate={{
                  required: { value: true, message: 'Это поле обязательно к заполнению.' },
                  maxLength: { value: 20, message: 'Это поле не может быть длинее, чем 20 символов.' },
                }}
              />
              <ValidatedField
                label="Actual Departure"
                id="waybill-actualDeparture"
                name="actualDeparture"
                data-cy="actualDeparture"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Actual Return"
                id="waybill-actualReturn"
                name="actualReturn"
                data-cy="actualReturn"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Mileage Start" id="waybill-mileageStart" name="mileageStart" data-cy="mileageStart" type="text" />
              <ValidatedField label="Mileage End" id="waybill-mileageEnd" name="mileageEnd" data-cy="mileageEnd" type="text" />
              <ValidatedField
                label="Fuel Consumption Plan"
                id="waybill-fuelConsumptionPlan"
                name="fuelConsumptionPlan"
                data-cy="fuelConsumptionPlan"
                type="text"
              />
              <ValidatedField
                label="Fuel Consumption Fact"
                id="waybill-fuelConsumptionFact"
                name="fuelConsumptionFact"
                data-cy="fuelConsumptionFact"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/waybill" replace variant="info">
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

export default WaybillUpdate;
