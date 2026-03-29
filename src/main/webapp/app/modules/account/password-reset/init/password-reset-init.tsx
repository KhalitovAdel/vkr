import React, { useEffect } from 'react';
import { Alert, Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isEmail } from 'react-jhipster';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handlePasswordResetInit, reset } from '../password-reset.reducer';

export const PasswordResetInit = () => {
  const dispatch = useAppDispatch();

  useEffect(
    () => () => {
      dispatch(reset());
    },
    [],
  );

  const handleValidSubmit = ({ email }) => {
    dispatch(handlePasswordResetInit(email));
  };

  const successMessage = useAppSelector(state => state.passwordReset.successMessage);

  useEffect(() => {
    if (successMessage) {
      toast.success(successMessage);
    }
  }, [successMessage]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1>Сброс пароля</h1>
          <Alert variant="warning">
            <p>Введите эл. почту, указанный при регистрации</p>
          </Alert>
          <ValidatedForm onSubmit={handleValidSubmit}>
            <ValidatedField
              name="email"
              label="Эл. почта"
              placeholder="Ваша эл. почта"
              type="email"
              validate={{
                required: { value: true, message: 'Email должен быть указан.' },
                minLength: { value: 5, message: 'Длина email должна быть хотя-бы 5 символов' },
                maxLength: { value: 254, message: 'Email не может быть длиннее чем 50 символов' },
                validate: v => isEmail(v) || 'Email не верен.',
              }}
              data-cy="emailResetPassword"
            />
            <Button variant="primary" type="submit" data-cy="submit">
              Сбросить пароль
            </Button>
          </ValidatedForm>
        </Col>
      </Row>
    </div>
  );
};

export default PasswordResetInit;
