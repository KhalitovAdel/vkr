import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/vehicle">
        Транспортные средства
      </MenuItem>
      <MenuItem icon="asterisk" to="/driver">
        Водители
      </MenuItem>
      <MenuItem icon="asterisk" to="/route">
        Маршруты
      </MenuItem>
      <MenuItem icon="asterisk" to="/stop">
        Остановки
      </MenuItem>
      <MenuItem icon="asterisk" to="/route-stop">
        Остановки на маршрутах
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip">
        Рейсы
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip/suggestion">
        Подбор ТС
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip/by-date">
        Рейсы по дате
      </MenuItem>
      <MenuItem icon="asterisk" to="/waybill">
        Путевые листы
      </MenuItem>
      <MenuItem icon="asterisk" to="/event">
        События
      </MenuItem>
      <MenuItem icon="asterisk" to="/vehicle/fleet-status">
        Статус парка
      </MenuItem>
      <MenuItem icon="asterisk" to="/vehicle/schedule">
        Расписание ТС
      </MenuItem>
      <MenuItem icon="asterisk" to="/driver/schedule">
        Расписание водителя
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
