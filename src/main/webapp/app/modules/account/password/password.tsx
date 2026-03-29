import React, { useEffect, useState } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import { getSession } from 'app/shared/reducers/authentication';

import { reset, savePassword } from './password.reducer';

export const PasswordPage = () => {
  const [password, setPassword] = useState('');
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(reset());
    dispatch(getSession());
    return () => {
      dispatch(reset());
    };
  }, []);

  const handleValidSubmit = ({ currentPassword, newPassword }) => {
    dispatch(savePassword({ currentPassword, newPassword }));
  };

  const updatePassword = event => setPassword(event.target.value);

  const account = useAppSelector(state => state.authentication.account);
  const successMessage = useAppSelector(state => state.password.successMessage);
  const errorMessage = useAppSelector(state => state.password.errorMessage);

  useEffect(() => {
    if (successMessage) {
      toast.success(successMessage);
    } else if (errorMessage) {
      toast.error(errorMessage);
    }
    dispatch(reset());
  }, [successMessage, errorMessage]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="password-title">
            Пароль для [<strong>{account.login}</strong>]
          </h2>
          <ValidatedForm id="password-form" onSubmit={handleValidSubmit}>
            <ValidatedField
              name="currentPassword"
              label="Текущий пароль"
              placeholder="Текущий пароль"
              type="password"
              validate={{
                required: { value: true, message: 'Пароль должен быть указан.' },
              }}
              data-cy="currentPassword"
            />
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
              data-cy="newPassword"
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
              data-cy="confirmPassword"
            />
            <Button variant="success" type="submit" data-cy="submit">
              Сохранить
            </Button>
          </ValidatedForm>
        </Col>
      </Row>
    </div>
  );
};

export default PasswordPage;
