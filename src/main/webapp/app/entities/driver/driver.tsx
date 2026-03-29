import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './driver.reducer';

export const Driver = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const driverList = useAppSelector(state => state.driver.entities);
  const loading = useAppSelector(state => state.driver.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="driver-heading" data-cy="DriverHeading">
        Drivers
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/driver/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Driver
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {driverList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('employeeNumber')}>
                  Employee Number <FontAwesomeIcon icon={getSortIconByFieldName('employeeNumber')} />
                </th>
                <th className="hand" onClick={sort('fullName')}>
                  Full Name <FontAwesomeIcon icon={getSortIconByFieldName('fullName')} />
                </th>
                <th className="hand" onClick={sort('licenseCategory')}>
                  License Category <FontAwesomeIcon icon={getSortIconByFieldName('licenseCategory')} />
                </th>
                <th className="hand" onClick={sort('experience')}>
                  Experience <FontAwesomeIcon icon={getSortIconByFieldName('experience')} />
                </th>
                <th className="hand" onClick={sort('hireDate')}>
                  Hire Date <FontAwesomeIcon icon={getSortIconByFieldName('hireDate')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {driverList.map(driver => (
                <tr key={`entity-${driver.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/driver/${driver.id}`} variant="link" size="sm">
                      {driver.id}
                    </Button>
                  </td>
                  <td>{driver.employeeNumber}</td>
                  <td>{driver.fullName}</td>
                  <td>{driver.licenseCategory}</td>
                  <td>{driver.experience}</td>
                  <td>{driver.hireDate ? <TextFormat type="date" value={driver.hireDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/driver/${driver.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button as={Link as any} to={`/driver/${driver.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/driver/${driver.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Удалить</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Drivers не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Driver;
