import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/vehicle">
        Vehicle
      </MenuItem>
      <MenuItem icon="asterisk" to="/driver">
        Driver
      </MenuItem>
      <MenuItem icon="asterisk" to="/route">
        Route
      </MenuItem>
      <MenuItem icon="asterisk" to="/stop">
        Stop
      </MenuItem>
      <MenuItem icon="asterisk" to="/route-stop">
        Route Stop
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip">
        Trip
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip/suggestion">
        Trip Suggestion
      </MenuItem>
      <MenuItem icon="asterisk" to="/trip/by-date">
        Trips By Date
      </MenuItem>
      <MenuItem icon="asterisk" to="/waybill">
        Waybill
      </MenuItem>
      <MenuItem icon="asterisk" to="/event">
        Event
      </MenuItem>
      <MenuItem icon="asterisk" to="/vehicle/fleet-status">
        Fleet Status
      </MenuItem>
      <MenuItem icon="asterisk" to="/vehicle/schedule">
        Vehicle Schedule
      </MenuItem>
      <MenuItem icon="asterisk" to="/driver/schedule">
        Driver Schedule
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
