import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Link, useParams } from 'react-router';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stop.reducer';

export const StopDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const stopEntity = useAppSelector(state => state.stop.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stopDetailsHeading">Stop</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{stopEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{stopEntity.name}</dd>
          <dt>
            <span id="latitude">Latitude</span>
          </dt>
          <dd>{stopEntity.latitude}</dd>
          <dt>
            <span id="longitude">Longitude</span>
          </dt>
          <dd>{stopEntity.longitude}</dd>
        </dl>
        <Button as={Link as any} to="/stop" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/stop/${stopEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default StopDetail;
