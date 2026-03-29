package com.transport.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DriverTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Driver getDriverSample1() {
        return new Driver()
            .id(1L)
            .employeeNumber("employeeNumber1")
            .fullName("fullName1")
            .licenseCategory("licenseCategory1")
            .experience(1);
    }

    public static Driver getDriverSample2() {
        return new Driver()
            .id(2L)
            .employeeNumber("employeeNumber2")
            .fullName("fullName2")
            .licenseCategory("licenseCategory2")
            .experience(2);
    }

    public static Driver getDriverRandomSampleGenerator() {
        return new Driver()
            .id(longCount.incrementAndGet())
            .employeeNumber(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .licenseCategory(UUID.randomUUID().toString())
            .experience(intCount.incrementAndGet());
    }
}
