import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './waybill.reducer';

export const WaybillDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const waybillEntity = useAppSelector(state => state.waybill.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="waybillDetailsHeading">Путевой лист</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">№</span>
          </dt>
          <dd>{waybillEntity.id}</dd>
          <dt>
            <span id="documentNumber">Номер документа</span>
          </dt>
          <dd>{waybillEntity.documentNumber}</dd>
          <dt>
            <span id="actualDeparture">Фактический выезд</span>
          </dt>
          <dd>
            {waybillEntity.actualDeparture ? (
              <TextFormat value={waybillEntity.actualDeparture} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="actualReturn">Фактический возврат</span>
          </dt>
          <dd>
            {waybillEntity.actualReturn ? <TextFormat value={waybillEntity.actualReturn} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="mileageStart">Пробег на выезд</span>
          </dt>
          <dd>{waybillEntity.mileageStart}</dd>
          <dt>
            <span id="mileageEnd">Пробег на возврат</span>
          </dt>
          <dd>{waybillEntity.mileageEnd}</dd>
          <dt>
            <span id="fuelConsumptionPlan">Расход топлива (план)</span>
          </dt>
          <dd>{waybillEntity.fuelConsumptionPlan}</dd>
          <dt>
            <span id="fuelConsumptionFact">Расход топлива (факт)</span>
          </dt>
          <dd>{waybillEntity.fuelConsumptionFact}</dd>
        </dl>
        <Button as={Link as any} to="/waybill" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/waybill/${waybillEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default WaybillDetail;
