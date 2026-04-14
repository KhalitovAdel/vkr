import './home.scss';

import React from 'react';
import { Alert, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h1 className="display-4">Добро пожаловать</h1>
        <p className="lead">Информационная система учёта городского пассажирского транспорта</p>
        {account?.login ? (
          <div>
            <Alert variant="success">Вы вошли как пользователь &quot;{account.login}&quot;.</Alert>
          </div>
        ) : (
          <div>
            <Alert variant="warning">
              Если вы хотите
              <span>&nbsp;</span>
              <Link to="/login" className="alert-link">
                авторизироваться
              </Link>
              , вы можете попробовать аккаунты по умолчанию:
              <br />- Администратор (логин=&quot;admin&quot; и пароль=&quot;admin&quot;) <br />- Пользователь (логин=&quot;user&quot; и
              пароль=&quot;user&quot;).
            </Alert>

            <Alert variant="warning">
              У вас нет аккаунта?&nbsp;
              <Link to="/account/register" className="alert-link">
                Создать новый аккаунт
              </Link>
            </Alert>
          </div>
        )}
        <p className="text-muted small">
          Для работы с данными используйте разделы меню «Сущности» и «Панель мониторинга» (после входа в систему).
        </p>
      </Col>
    </Row>
  );
};

export default Home;
