import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { routeTypeRu } from 'app/shared/util/enum-labels-ru';

import { getEntity } from './route.reducer';

export const RouteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const routeEntity = useAppSelector(state => state.route.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="routeDetailsHeading">Маршрут</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">№</span>
          </dt>
          <dd>{routeEntity.id}</dd>
          <dt>
            <span id="routeNumber">Номер маршрута</span>
          </dt>
          <dd>{routeEntity.routeNumber}</dd>
          <dt>
            <span id="routeName">Название</span>
          </dt>
          <dd>{routeEntity.routeName}</dd>
          <dt>
            <span id="length">Длина, км</span>
          </dt>
          <dd>{routeEntity.length}</dd>
          <dt>
            <span id="routeType">Тип маршрута</span>
          </dt>
          <dd>{routeTypeRu(routeEntity.routeType)}</dd>
        </dl>
        <Button as={Link as any} to="/route" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/route/${routeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RouteDetail;
