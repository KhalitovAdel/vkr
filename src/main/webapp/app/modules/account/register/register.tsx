import React, { useEffect, useState } from 'react';
import { Alert, Button, Col, Row } from 'react-bootstrap';
import { ValidatedField, ValidatedForm, isEmail } from 'react-jhipster';
import { Link } from 'react-router';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';

import { handleRegister, reset } from './register.reducer';

export const RegisterPage = () => {
  const [password, setPassword] = useState('');
  const dispatch = useAppDispatch();

  useEffect(
    () => () => {
      dispatch(reset());
    },
    [],
  );

  const handleValidSubmit = ({ username, email, firstPassword }) => {
    dispatch(handleRegister({ login: username, email, password: firstPassword, langKey: 'en' }));
  };

  const updatePassword = event => setPassword(event.target.value);

  const successMessage = useAppSelector(state => state.register.successMessage);

  useEffect(() => {
    if (successMessage) {
      toast.success(successMessage);
    }
  }, [successMessage]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1 id="register-title" data-cy="registerTitle">
            Регистрация
          </h1>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          <ValidatedForm id="register-form" onSubmit={handleValidSubmit}>
            <ValidatedField
              name="username"
              label="Логин"
              placeholder="Ваш логин"
              validate={{
                required: { value: true, message: 'Необходимо ввести логин.' },
                pattern: {
                  value: /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/,
                  message: 'Ваше имя пользователя недействительно.',
                },
                minLength: { value: 1, message: 'Ваш логин должен содержать хотя бы 1 символ' },
                maxLength: { value: 50, message: 'Ваш логин не может быть длинее чем 50 символов' },
              }}
              data-cy="username"
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
            <ValidatedField
              name="firstPassword"
              label="Новый пароль"
              placeholder="Новый пароль"
              type="password"
              onChange={updatePassword}
              validate={{
                required: { value: true, message: 'Пароль должен быть указан.' },
                minLength: { value: 4, message: 'Длина пароля должна быть хотя-бы 4 символов' },
                maxLength: { value: 50, message: 'Пароль не может быть длиннее чем 50 символов' },
              }}
              data-cy="firstPassword"
            />
            <PasswordStrengthBar password={password} />
            <ValidatedField
              name="secondPassword"
              label="Подтверждение нового пароля"
              placeholder="Подтвердите новый пароль"
              type="password"
              validate={{
                required: { value: true, message: 'подтверждение пароля должно быть указано.' },
                minLength: { value: 4, message: 'Длина подтверждения пароля должна быть хотя-бы 4 символов' },
                maxLength: { value: 50, message: 'Подтверждение пароля не может быть больше чем 50 символов' },
                validate: v => v === password || 'Пароль и его подтверждение не совпадают!',
              }}
              data-cy="secondPassword"
            />
            <Button id="register-submit" color="primary" type="submit" data-cy="submit">
              Зарегистрироваться
            </Button>
          </ValidatedForm>
          <p>&nbsp;</p>
          <Alert variant="warning">
            <span>Если вы хотите </span>
            <Link to="/login" className="alert-link">
              авторизироваться
            </Link>
            <span>
              , вы можете попробовать аккаунты по умолчанию:
              <br />- Администратор (логин=&quot;admin&quot; и пароль=&quot;admin&quot;) <br />- Пользователь (логин=&quot;user&quot; и
              пароль=&quot;user&quot;).
            </span>
          </Alert>
        </Col>
      </Row>
    </div>
  );
};

export default RegisterPage;
