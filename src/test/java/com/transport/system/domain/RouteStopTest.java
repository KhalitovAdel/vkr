package com.transport.system.domain;

import static com.transport.system.domain.RouteStopTestSamples.*;
import static com.transport.system.domain.RouteTestSamples.*;
import static com.transport.system.domain.StopTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RouteStopTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RouteStop.class);
        RouteStop routeStop1 = getRouteStopSample1();
        RouteStop routeStop2 = new RouteStop();
        assertThat(routeStop1).isNotEqualTo(routeStop2);

        routeStop2.setId(routeStop1.getId());
        assertThat(routeStop1).isEqualTo(routeStop2);

        routeStop2 = getRouteStopSample2();
        assertThat(routeStop1).isNotEqualTo(routeStop2);
    }

    @Test
    void routeTest() {
        RouteStop routeStop = getRouteStopRandomSampleGenerator();
        Route routeBack = getRouteRandomSampleGenerator();

        routeStop.setRoute(routeBack);
        assertThat(routeStop.getRoute()).isEqualTo(routeBack);

        routeStop.route(null);
        assertThat(routeStop.getRoute()).isNull();
    }

    @Test
    void stopTest() {
        RouteStop routeStop = getRouteStopRandomSampleGenerator();
        Stop stopBack = getStopRandomSampleGenerator();

        routeStop.setStop(stopBack);
        assertThat(routeStop.getStop()).isEqualTo(stopBack);

        routeStop.stop(null);
        assertThat(routeStop.getStop()).isNull();
    }
}
