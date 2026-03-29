import React, { useEffect, useState } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { useSearchParams } from 'react-router';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import { handlePasswordResetFinish, reset } from '../password-reset.reducer';

export const PasswordResetFinishPage = () => {
  const dispatch = useAppDispatch();

  const [searchParams] = useSearchParams();
  const key = searchParams.get('key');

  const [password, setPassword] = useState('');

  useEffect(
    () => () => {
      dispatch(reset());
    },
    [],
  );

  const handleValidSubmit = ({ newPassword }) => dispatch(handlePasswordResetFinish({ key, newPassword }));

  const updatePassword = event => setPassword(event.target.value);

  const getResetForm = () => {
    return (
      <ValidatedForm onSubmit={handleValidSubmit}>
        <ValidatedField
          name="newPassword"
          label="Новый пароль"
          placeholder="Новый пароль"
          type="password"
          validate={{
            required: { value: true, message: 'Пароль должен быть указан.' },
            minLength: { value: 4, message: 'Длина пароля должна быть хотя-бы 4 символов' },
            maxLength: { value: 50, message: 'Пароль не может быть длиннее чем 50 символов' },
          }}
          onChange={updatePassword}
          data-cy="resetPassword"
        />
        <PasswordStrengthBar password={password} />
        <ValidatedField
          name="confirmPassword"
          label="Подтверждение нового пароля"
          placeholder="Подтвердите новый пароль"
          type="password"
          validate={{
            required: { value: true, message: 'подтверждение пароля должно быть указано.' },
            minLength: { value: 4, message: 'Длина подтверждения пароля должна быть хотя-бы 4 символов' },
            maxLength: { value: 50, message: 'Подтверждение пароля не может быть больше чем 50 символов' },
            validate: v => v === password || 'Пароль и его подтверждение не совпадают!',
          }}
          data-cy="confirmResetPassword"
        />
        <Button variant="success" type="submit" data-cy="submit">
          Подтвердить новый пароль
        </Button>
      </ValidatedForm>
    );
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
        <Col md="4">
          <h1>Сброс пароля</h1>
          <div>{key && getResetForm()}</div>
        </Col>
      </Row>
    </div>
  );
};

export default PasswordResetFinishPage;
