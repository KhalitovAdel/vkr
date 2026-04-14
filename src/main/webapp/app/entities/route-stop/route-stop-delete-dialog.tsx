import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './route-stop.reducer';

export const RouteStopDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const routeStopEntity = useAppSelector(state => state.routeStop.entity);
  const updateSuccess = useAppSelector(state => state.routeStop.updateSuccess);

  const handleClose = () => {
    navigate('/route-stop');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(routeStopEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="routeStopDeleteDialogHeading" closeButton>
        Подтвердите операцию удаления
      </ModalHeader>
      <ModalBody id="transportSystemApp.routeStop.delete.question">Удалить запись № {routeStopEntity.id}?</ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Отмена
        </Button>
        <Button id="jhi-confirm-delete-routeStop" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Удалить
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default RouteStopDeleteDialog;
