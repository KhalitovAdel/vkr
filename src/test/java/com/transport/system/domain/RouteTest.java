package com.transport.system.domain;

import static com.transport.system.domain.RouteStopTestSamples.*;
import static com.transport.system.domain.RouteTestSamples.*;
import static com.transport.system.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RouteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Route.class);
        Route route1 = getRouteSample1();
        Route route2 = new Route();
        assertThat(route1).isNotEqualTo(route2);

        route2.setId(route1.getId());
        assertThat(route1).isEqualTo(route2);

        route2 = getRouteSample2();
        assertThat(route1).isNotEqualTo(route2);
    }

    @Test
    void tripTest() {
        Route route = getRouteRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        route.addTrip(tripBack);
        assertThat(route.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getRoute()).isEqualTo(route);

        route.removeTrip(tripBack);
        assertThat(route.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getRoute()).isNull();

        route.trips(new HashSet<>(Set.of(tripBack)));
        assertThat(route.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getRoute()).isEqualTo(route);

        route.setTrips(new HashSet<>());
        assertThat(route.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getRoute()).isNull();
    }

    @Test
    void routeStopTest() {
        Route route = getRouteRandomSampleGenerator();
        RouteStop routeStopBack = getRouteStopRandomSampleGenerator();

        route.addRouteStop(routeStopBack);
        assertThat(route.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getRoute()).isEqualTo(route);

        route.removeRouteStop(routeStopBack);
        assertThat(route.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getRoute()).isNull();

        route.routeStops(new HashSet<>(Set.of(routeStopBack)));
        assertThat(route.getRouteStops()).containsOnly(routeStopBack);
        assertThat(routeStopBack.getRoute()).isEqualTo(route);

        route.setRouteStops(new HashSet<>());
        assertThat(route.getRouteStops()).doesNotContain(routeStopBack);
        assertThat(routeStopBack.getRoute()).isNull();
    }
}
