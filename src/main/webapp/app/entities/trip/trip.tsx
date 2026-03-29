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

import { getEntities } from './trip.reducer';

export const Trip = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const tripList = useAppSelector(state => state.trip.entities);
  const loading = useAppSelector(state => state.trip.loading);

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
      <h2 id="trip-heading" data-cy="TripHeading">
        Trips
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/trip/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Trip
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {tripList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('departureTime')}>
                  Departure Time <FontAwesomeIcon icon={getSortIconByFieldName('departureTime')} />
                </th>
                <th className="hand" onClick={sort('arrivalTime')}>
                  Arrival Time <FontAwesomeIcon icon={getSortIconByFieldName('arrivalTime')} />
                </th>
                <th className="hand" onClick={sort('tripDate')}>
                  Trip Date <FontAwesomeIcon icon={getSortIconByFieldName('tripDate')} />
                </th>
                <th className="hand" onClick={sort('tripStatus')}>
                  Trip Status <FontAwesomeIcon icon={getSortIconByFieldName('tripStatus')} />
                </th>
                <th>
                  Waybill <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Vehicle <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Driver <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Route <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {tripList.map(trip => (
                <tr key={`entity-${trip.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/trip/${trip.id}`} variant="link" size="sm">
                      {trip.id}
                    </Button>
                  </td>
                  <td>{trip.departureTime}</td>
                  <td>{trip.arrivalTime}</td>
                  <td>{trip.tripDate ? <TextFormat type="date" value={trip.tripDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{trip.tripStatus}</td>
                  <td>{trip.waybill ? <Link to={`/waybill/${trip.waybill.id}`}>{trip.waybill.id}</Link> : ''}</td>
                  <td>{trip.vehicle ? <Link to={`/vehicle/${trip.vehicle.id}`}>{trip.vehicle.id}</Link> : ''}</td>
                  <td>{trip.driver ? <Link to={`/driver/${trip.driver.id}`}>{trip.driver.id}</Link> : ''}</td>
                  <td>{trip.route ? <Link to={`/route/${trip.route.id}`}>{trip.route.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/trip/${trip.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button as={Link as any} to={`/trip/${trip.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/trip/${trip.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Trips не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Trip;
