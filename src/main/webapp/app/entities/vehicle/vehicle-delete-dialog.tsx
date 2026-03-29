import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './vehicle.reducer';

export const VehicleDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const vehicleEntity = useAppSelector(state => state.vehicle.entity);
  const updateSuccess = useAppSelector(state => state.vehicle.updateSuccess);

  const handleClose = () => {
    navigate('/vehicle');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(vehicleEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="vehicleDeleteDialogHeading" closeButton>
        Подтвердите операцию удаления
      </ModalHeader>
      <ModalBody id="transportSystemApp.vehicle.delete.question">Вы уверены что хотите удалить Vehicle {vehicleEntity.id}?</ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Отмена
        </Button>
        <Button id="jhi-confirm-delete-vehicle" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp; Удалить
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default VehicleDeleteDialog;
