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
        <h1 className="display-4">Добро пожаловать, Java Hipster!</h1>
        <p className="lead">Это ваша домашняя страница</p>
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
        <p>Если у Вас возникли вопросы по JHipster:</p>

        <ul>
          <li>
            <a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer">
              JHipster домашняя страница
            </a>
          </li>
          <li>
            <a href="https://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer">
              JHipster на Stack Overflow
            </a>
          </li>
          <li>
            <a href="https://github.com/jhipster/generator-jhipster/issues?state=open" target="_blank" rel="noopener noreferrer">
              JHipster баг трекер
            </a>
          </li>
          <li>
            <a href="https://gitter.im/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
              JHipster public chat room
            </a>
          </li>
          <li>
            <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer">
              пользователь @jhipster в Twitter
            </a>
          </li>
        </ul>

        <p>
          Если вам нравится JHipster, не забудьте дать нам звёздочку на{' '}
          <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">
            GitHub
          </a>
          !
        </p>
      </Col>
    </Row>
  );
};

export default Home;
