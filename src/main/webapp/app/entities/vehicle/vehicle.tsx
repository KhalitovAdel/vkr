import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './vehicle.reducer';

export const Vehicle = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const vehicleList = useAppSelector(state => state.vehicle.entities);
  const loading = useAppSelector(state => state.vehicle.loading);

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
      <h2 id="vehicle-heading" data-cy="VehicleHeading">
        Vehicles
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/vehicle/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Vehicle
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {vehicleList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('stateNumber')}>
                  State Number <FontAwesomeIcon icon={getSortIconByFieldName('stateNumber')} />
                </th>
                <th className="hand" onClick={sort('model')}>
                  Model <FontAwesomeIcon icon={getSortIconByFieldName('model')} />
                </th>
                <th className="hand" onClick={sort('vehicleType')}>
                  Vehicle Type <FontAwesomeIcon icon={getSortIconByFieldName('vehicleType')} />
                </th>
                <th className="hand" onClick={sort('capacity')}>
                  Capacity <FontAwesomeIcon icon={getSortIconByFieldName('capacity')} />
                </th>
                <th className="hand" onClick={sort('passengerCapacity')}>
                  Passenger Capacity <FontAwesomeIcon icon={getSortIconByFieldName('passengerCapacity')} />
                </th>
                <th className="hand" onClick={sort('year')}>
                  Year <FontAwesomeIcon icon={getSortIconByFieldName('year')} />
                </th>
                <th className="hand" onClick={sort('technicalStatus')}>
                  Technical Status <FontAwesomeIcon icon={getSortIconByFieldName('technicalStatus')} />
                </th>
                <th className="hand" onClick={sort('mileage')}>
                  Mileage <FontAwesomeIcon icon={getSortIconByFieldName('mileage')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {vehicleList.map(vehicle => (
                <tr key={`entity-${vehicle.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/vehicle/${vehicle.id}`} variant="link" size="sm">
                      {vehicle.id}
                    </Button>
                  </td>
                  <td>{vehicle.stateNumber}</td>
                  <td>{vehicle.model}</td>
                  <td>{vehicle.vehicleType}</td>
                  <td>{vehicle.capacity}</td>
                  <td>{vehicle.passengerCapacity}</td>
                  <td>{vehicle.year}</td>
                  <td>{vehicle.technicalStatus}</td>
                  <td>{vehicle.mileage}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/vehicle/${vehicle.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button as={Link as any} to={`/vehicle/${vehicle.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/vehicle/${vehicle.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Vehicles не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Vehicle;
