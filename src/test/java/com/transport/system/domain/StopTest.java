package com.transport.system.domain;

import static com.transport.system.domain.RouteStopTestSamples.*;
import static com.transport.system.domain.StopTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StopTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stop.class);
        Stop stop1 = getStopSample1();
        Stop stop2 = new Stop();
        assertThat(stop1).isNotEqualTo(stop2);

        stop2.setId(stop1.getId());
        assertThat(stop1).isEqualTo(stop2);

        stop2 = getStopSample2();
        assertThat(stop1).isNotEqualTo(stop2);
    }

    @Test
    void routeStopTest() {
        Stop stop = getStopRandomSampleGenerator();
        RouteStop routeStopBack = getRouteStopRandomSampleGenerator();

        stop.addRouteStop(routeStopBack);
        assertThat(stop.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getStop()).isEqualTo(stop);

        stop.removeRouteStop(routeStopBack);
        assertThat(stop.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getStop()).isNull();

        stop.routeStops(new HashSet<>(Set.of(routeStopBack)));
        assertThat(stop.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getStop()).isEqualTo(stop);

        stop.setRouteStops(new HashSet<>());
        assertThat(stop.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getStop()).isNull();
    }
}
