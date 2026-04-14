import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './route-stop.reducer';

export const RouteStop = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const routeStopList = useAppSelector(state => state.routeStop.entities);
  const loading = useAppSelector(state => state.routeStop.loading);

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
      <h2 id="route-stop-heading" data-cy="RouteStopHeading">
        Остановки на маршрутах
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/route-stop/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать запись
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {routeStopList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  № <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('stopOrder')}>
                  Порядок <FontAwesomeIcon icon={getSortIconByFieldName('stopOrder')} />
                </th>
                <th className="hand" onClick={sort('distanceFromPrev')}>
                  Расстояние от пред., км <FontAwesomeIcon icon={getSortIconByFieldName('distanceFromPrev')} />
                </th>
                <th>
                  Маршрут <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Остановка <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {routeStopList.map(routeStop => (
                <tr key={`entity-${routeStop.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/route-stop/${routeStop.id}`} variant="link" size="sm">
                      {routeStop.id}
                    </Button>
                  </td>
                  <td>{routeStop.stopOrder}</td>
                  <td>{routeStop.distanceFromPrev}</td>
                  <td>{routeStop.route ? <Link to={`/route/${routeStop.route.id}`}>{routeStop.route.id}</Link> : ''}</td>
                  <td>{routeStop.stop ? <Link to={`/stop/${routeStop.stop.id}`}>{routeStop.stop.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/route-stop/${routeStop.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/route-stop/${routeStop.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/route-stop/${routeStop.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Записи не найдены</div>
        )}
      </div>
    </div>
  );
};

export default RouteStop;
