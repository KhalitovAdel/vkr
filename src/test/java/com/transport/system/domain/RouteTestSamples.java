package com.transport.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RouteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Route getRouteSample1() {
        return new Route().id(1L).routeNumber("routeNumber1").routeName("routeName1");
    }

    public static Route getRouteSample2() {
        return new Route().id(2L).routeNumber("routeNumber2").routeName("routeName2");
    }

    public static Route getRouteRandomSampleGenerator() {
        return new Route()
            .id(longCount.incrementAndGet())
            .routeNumber(UUID.randomUUID().toString())
            .routeName(UUID.randomUUID().toString());
    }
}
