import driver from 'app/entities/driver/driver.reducer';
import event from 'app/entities/event/event.reducer';
import route from 'app/entities/route/route.reducer';
import routeStop from 'app/entities/route-stop/route-stop.reducer';
import stop from 'app/entities/stop/stop.reducer';
import trip from 'app/entities/trip/trip.reducer';
import vehicle from 'app/entities/vehicle/vehicle.reducer';
import waybill from 'app/entities/waybill/waybill.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  vehicle,
  driver,
  route,
  stop,
  routeStop,
  trip,
  waybill,
  event,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
