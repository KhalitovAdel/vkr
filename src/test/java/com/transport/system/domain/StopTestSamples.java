package com.transport.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StopTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Stop getStopSample1() {
        return new Stop().id(1L).name("name1");
    }

    public static Stop getStopSample2() {
        return new Stop().id(2L).name("name2");
    }

    public static Stop getStopRandomSampleGenerator() {
        return new Stop().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
