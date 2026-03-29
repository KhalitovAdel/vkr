import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isEmail } from 'react-jhipster';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';

import { reset, saveAccountSettings } from './settings.reducer';

export const SettingsPage = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const successMessage = useAppSelector(state => state.settings.successMessage);

  useEffect(() => {
    dispatch(getSession());
    return () => {
      dispatch(reset());
    };
  }, []);

  useEffect(() => {
    if (successMessage) {
      toast.success(successMessage);
    }
  }, [successMessage]);

  const handleValidSubmit = values => {
    dispatch(
      saveAccountSettings({
        ...account,
        ...values,
      }),
    );
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="settings-title">
            Настройки пользователя для [<strong>{account.login}</strong>]
          </h2>
          <ValidatedForm id="settings-form" onSubmit={handleValidSubmit} defaultValues={account}>
            <ValidatedField
              name="firstName"
              label="Имя"
              id="firstName"
              placeholder="Ваше имя"
              validate={{
                required: { value: true, message: 'Имя должно быть указано.' },
                minLength: { value: 1, message: 'Длина имени должна быть хотя-бы 1 символ' },
                maxLength: { value: 50, message: 'Имя не может быть длиннее чем 50 символов' },
              }}
              data-cy="firstname"
            />
            <ValidatedField
              name="lastName"
              label="Фамилия"
              id="lastName"
              placeholder="Ваша фамилия"
              validate={{
                required: { value: true, message: 'Фамилия должна быть указана.' },
                minLength: { value: 1, message: 'Длина фамилии должна быть хотя-бы 1 символ' },
                maxLength: { value: 50, message: 'Фамилия не может быть длиннее чем 50 символов' },
              }}
              data-cy="lastname"
            />
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
              data-cy="email"
            />
            <Button variant="primary" type="submit" data-cy="submit">
              Сохранить
            </Button>
          </ValidatedForm>
        </Col>
      </Row>
    </div>
  );
};

export default SettingsPage;
