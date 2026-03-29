package com.transport.system.domain;

import static com.transport.system.domain.EventTestSamples.*;
import static com.transport.system.domain.TripTestSamples.*;
import static com.transport.system.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void tripTest() {
        Event event = getEventRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        event.setTrip(tripBack);
        assertThat(event.getTrip()).isEqualTo(tripBack);

        event.trip(null);
        assertThat(event.getTrip()).isNull();
    }

    @Test
    void vehicleTest() {
        Event event = getEventRandomSampleGenerator();
        Vehicle vehicleBack = getVehicleRandomSampleGenerator();

        event.setVehicle(vehicleBack);
        assertThat(event.getVehicle()).isEqualTo(vehicleBack);

        event.vehicle(null);
        assertThat(event.getVehicle()).isNull();
    }
}
