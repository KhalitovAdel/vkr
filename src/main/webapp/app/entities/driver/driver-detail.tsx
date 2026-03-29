import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './driver.reducer';

export const DriverDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const driverEntity = useAppSelector(state => state.driver.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="driverDetailsHeading">Driver</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{driverEntity.id}</dd>
          <dt>
            <span id="employeeNumber">Employee Number</span>
          </dt>
          <dd>{driverEntity.employeeNumber}</dd>
          <dt>
            <span id="fullName">Full Name</span>
          </dt>
          <dd>{driverEntity.fullName}</dd>
          <dt>
            <span id="licenseCategory">License Category</span>
          </dt>
          <dd>{driverEntity.licenseCategory}</dd>
          <dt>
            <span id="experience">Experience</span>
          </dt>
          <dd>{driverEntity.experience}</dd>
          <dt>
            <span id="hireDate">Hire Date</span>
          </dt>
          <dd>{driverEntity.hireDate ? <TextFormat value={driverEntity.hireDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button as={Link as any} to="/driver" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Назад</span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/driver/${driverEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DriverDetail;
