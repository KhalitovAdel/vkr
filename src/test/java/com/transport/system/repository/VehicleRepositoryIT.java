package com.transport.system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.IntegrationTest;
import com.transport.system.domain.Vehicle;
import com.transport.system.domain.enumeration.CapacityType;
import com.transport.system.domain.enumeration.TechnicalStatus;
import com.transport.system.domain.enumeration.VehicleType;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
class VehicleRepositoryIT {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    @Transactional
    void shouldCountByStatusAndFilterAvailable() {
        var vehicle = new Vehicle()
            .stateNumber("AA001")
            .model("Test")
            .vehicleType(VehicleType.BUS)
            .capacity(CapacityType.MEDIUM)
            .passengerCapacity(50)
            .year(2020)
            .technicalStatus(TechnicalStatus.OPERATIONAL)
            .mileage(new BigDecimal("1000.00"));
        vehicleRepository.saveAndFlush(vehicle);

        var count = vehicleRepository.countByStatus(TechnicalStatus.OPERATIONAL);
        assertThat(count).isGreaterThan(0);

        var available = vehicleRepository.findAvailableByCapacityAndStatus(CapacityType.MEDIUM, TechnicalStatus.OPERATIONAL, Set.of(-1L));
        assertThat(available).isNotEmpty();
    }
}
