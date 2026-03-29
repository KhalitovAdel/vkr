package com.transport.system.domain;

import static com.transport.system.domain.DriverTestSamples.*;
import static com.transport.system.domain.EventTestSamples.*;
import static com.transport.system.domain.RouteTestSamples.*;
import static com.transport.system.domain.TripTestSamples.*;
import static com.transport.system.domain.VehicleTestSamples.*;
import static com.transport.system.domain.WaybillTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = getTripSample1();
        Trip trip2 = new Trip();
        assertThat(trip1).isNotEqualTo(trip2);

        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);

        trip2 = getTripSample2();
        assertThat(trip1).isNotEqualTo(trip2);
    }

    @Test
    void waybillTest() {
        Trip trip = getTripRandomSampleGenerator();
        Waybill waybillBack = getWaybillRandomSampleGenerator();

        trip.setWaybill(waybillBack);
        assertThat(trip.getWaybill()).isEqualTo(waybillBack);

        trip.waybill(null);
        assertThat(trip.getWaybill()).isNull();
    }

    @Test
    void eventTest() {
        Trip trip = getTripRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        trip.addEvent(eventBack);
        assertThat(trip.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getTrip()).isEqualTo(trip);

        trip.removeEvent(eventBack);
        assertThat(trip.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getTrip()).isNull();

        trip.events(new HashSet<>(Set.of(eventBack)));
        assertThat(trip.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getTrip()).isEqualTo(trip);

        trip.setEvents(new HashSet<>());
        assertThat(trip.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getTrip()).isNull();
    }

    @Test
    void vehicleTest() {
        Trip trip = getTripRandomSampleGenerator();
        Vehicle vehicleBack = getVehicleRandomSampleGenerator();

        trip.setVehicle(vehicleBack);
        assertThat(trip.getVehicle()).isEqualTo(vehicleBack);

        trip.vehicle(null);
        assertThat(trip.getVehicle()).isNull();
    }

    @Test
    void driverTest() {
        Trip trip = getTripRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        trip.setDriver(driverBack);
        assertThat(trip.getDriver()).isEqualTo(driverBack);

        trip.driver(null);
        assertThat(trip.getDriver()).isNull();
    }

    @Test
    void routeTest() {
        Trip trip = getTripRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        trip.setRoute(routeBack);
        assertThat(trip.getRoute()).isEqualTo(routeBack);

        trip.route(null);
        assertThat(trip.getRoute()).isNull();
    }
}
