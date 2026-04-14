import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './route-stop.reducer';

export const RouteStopDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const routeStopEntity = useAppSelector(state => state.routeStop.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="routeStopDetailsHeading">Остановка на маршруте</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">№</span>
          </dt>
          <dd>{routeStopEntity.id}</dd>
          <dt>
            <span id="stopOrder">Порядок</span>
          </dt>
          <dd>{routeStopEntity.stopOrder}</dd>
          <dt>
            <span id="distanceFromPrev">Расстояние от пред., км</span>
          </dt>
          <dd>{routeStopEntity.distanceFromPrev}</dd>
          <dt>Маршрут</dt>
          <dd>{routeStopEntity.route ? routeStopEntity.route.id : ''}</dd>
          <dt>Остановка</dt>
          <dd>{routeStopEntity.stop ? routeStopEntity.stop.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/route-stop" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/route-stop/${routeStopEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RouteStopDetail;
