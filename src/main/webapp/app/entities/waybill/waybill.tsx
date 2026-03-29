import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './waybill.reducer';

export const Waybill = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const waybillList = useAppSelector(state => state.waybill.entities);
  const loading = useAppSelector(state => state.waybill.loading);

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
      <h2 id="waybill-heading" data-cy="WaybillHeading">
        Waybills
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Обновить список
          </Button>
          <Link to="/waybill/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Создать новый Waybill
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {waybillList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('documentNumber')}>
                  Document Number <FontAwesomeIcon icon={getSortIconByFieldName('documentNumber')} />
                </th>
                <th className="hand" onClick={sort('actualDeparture')}>
                  Actual Departure <FontAwesomeIcon icon={getSortIconByFieldName('actualDeparture')} />
                </th>
                <th className="hand" onClick={sort('actualReturn')}>
                  Actual Return <FontAwesomeIcon icon={getSortIconByFieldName('actualReturn')} />
                </th>
                <th className="hand" onClick={sort('mileageStart')}>
                  Mileage Start <FontAwesomeIcon icon={getSortIconByFieldName('mileageStart')} />
                </th>
                <th className="hand" onClick={sort('mileageEnd')}>
                  Mileage End <FontAwesomeIcon icon={getSortIconByFieldName('mileageEnd')} />
                </th>
                <th className="hand" onClick={sort('fuelConsumptionPlan')}>
                  Fuel Consumption Plan <FontAwesomeIcon icon={getSortIconByFieldName('fuelConsumptionPlan')} />
                </th>
                <th className="hand" onClick={sort('fuelConsumptionFact')}>
                  Fuel Consumption Fact <FontAwesomeIcon icon={getSortIconByFieldName('fuelConsumptionFact')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {waybillList.map(waybill => (
                <tr key={`entity-${waybill.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/waybill/${waybill.id}`} variant="link" size="sm">
                      {waybill.id}
                    </Button>
                  </td>
                  <td>{waybill.documentNumber}</td>
                  <td>
                    {waybill.actualDeparture ? <TextFormat type="date" value={waybill.actualDeparture} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{waybill.actualReturn ? <TextFormat type="date" value={waybill.actualReturn} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{waybill.mileageStart}</td>
                  <td>{waybill.mileageEnd}</td>
                  <td>{waybill.fuelConsumptionPlan}</td>
                  <td>{waybill.fuelConsumptionFact}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/waybill/${waybill.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Просмотр</span>
                      </Button>
                      <Button as={Link as any} to={`/waybill/${waybill.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Изменить</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/waybill/${waybill.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Waybills не найдено</div>
        )}
      </div>
    </div>
  );
};

export default Waybill;
