package com.transport.system.domain;

import static com.transport.system.domain.EventTestSamples.*;
import static com.transport.system.domain.TripTestSamples.*;
import static com.transport.system.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehicle.class);
        Vehicle vehicle1 = getVehicleSample1();
        Vehicle vehicle2 = new Vehicle();
        assertThat(vehicle1).isNotEqualTo(vehicle2);

        vehicle2.setId(vehicle1.getId());
        assertThat(vehicle1).isEqualTo(vehicle2);

        vehicle2 = getVehicleSample2();
        assertThat(vehicle1).isNotEqualTo(vehicle2);
    }

    @Test
    void tripTest() {
        Vehicle vehicle = getVehicleRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        vehicle.addTrip(tripBack);
        assertThat(vehicle.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getVehicle()).isEqualTo(vehicle);

        vehicle.removeTrip(tripBack);
        assertThat(vehicle.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getVehicle()).isNull();

        vehicle.trips(new HashSet<>(Set.of(tripBack)));
        assertThat(vehicle.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getVehicle()).isEqualTo(vehicle);

        vehicle.setTrips(new HashSet<>());
        assertThat(vehicle.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getVehicle()).isNull();
    }

    @Test
    void eventTest() {
        Vehicle vehicle = getVehicleRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        vehicle.addEvent(eventBack);
        assertThat(vehicle.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getVehicle()).isEqualTo(vehicle);

        vehicle.removeEvent(eventBack);
        assertThat(vehicle.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getVehicle()).isNull();

        vehicle.events(new HashSet<>(Set.of(eventBack)));
        assertThat(vehicle.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getVehicle()).isEqualTo(vehicle);

        vehicle.setEvents(new HashSet<>());
        assertThat(vehicle.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getVehicle()).isNull();
    }
}
