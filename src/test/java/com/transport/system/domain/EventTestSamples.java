package com.transport.system.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class EventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Event getEventSample1() {
        return new Event().id(1L);
    }

    public static Event getEventSample2() {
        return new Event().id(2L);
    }

    public static Event getEventRandomSampleGenerator() {
        return new Event().id(longCount.incrementAndGet());
    }
}
