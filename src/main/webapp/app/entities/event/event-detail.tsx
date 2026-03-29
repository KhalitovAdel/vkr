import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './event.reducer';

export const EventDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const eventEntity = useAppSelector(state => state.event.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="eventDetailsHeading">Event</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{eventEntity.id}</dd>
          <dt>
            <span id="eventType">Event Type</span>
          </dt>
          <dd>{eventEntity.eventType}</dd>
          <dt>
            <span id="eventTime">Event Time</span>
          </dt>
          <dd>{eventEntity.eventTime ? <TextFormat value={eventEntity.eventTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{eventEntity.description}</dd>
          <dt>Trip</dt>
          <dd>{eventEntity.trip ? eventEntity.trip.id : ''}</dd>
          <dt>Vehicle</dt>
          <dd>{eventEntity.vehicle ? eventEntity.vehicle.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/event" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/event/${eventEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EventDetail;
