package com.transport.system.domain;

import static com.transport.system.domain.DriverTestSamples.*;
import static com.transport.system.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DriverTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Driver.class);
        Driver driver1 = getDriverSample1();
        Driver driver2 = new Driver();
        assertThat(driver1).isNotEqualTo(driver2);

        driver2.setId(driver1.getId());
        assertThat(driver1).isEqualTo(driver2);

        driver2 = getDriverSample2();
        assertThat(driver1).isNotEqualTo(driver2);
    }

    @Test
    void tripTest() {
        Driver driver = getDriverRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        driver.addTrip(tripBack);
        assertThat(driver.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getDriver()).isEqualTo(driver);

        driver.removeTrip(tripBack);
        assertThat(driver.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getDriver()).isNull();

        driver.trips(new HashSet<>(Set.of(tripBack)));
        assertThat(driver.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getDriver()).isEqualTo(driver);

        driver.setTrips(new HashSet<>());
        assertThat(driver.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getDriver()).isNull();
    }
}
