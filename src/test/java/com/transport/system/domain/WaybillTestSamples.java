package com.transport.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WaybillTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Waybill getWaybillSample1() {
        return new Waybill().id(1L).documentNumber("documentNumber1");
    }

    public static Waybill getWaybillSample2() {
        return new Waybill().id(2L).documentNumber("documentNumber2");
    }

    public static Waybill getWaybillRandomSampleGenerator() {
        return new Waybill().id(longCount.incrementAndGet()).documentNumber(UUID.randomUUID().toString());
    }
}
