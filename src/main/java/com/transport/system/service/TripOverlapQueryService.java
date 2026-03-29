package com.transport.system.service;

import com.transport.system.repository.TripRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Overlap checks in a separate transaction so Hibernate does not auto-flush the entity currently
 * being validated ({@code preInsert} / {@code preUpdate}), which would recurse into the same
 * {@link jakarta.validation.ConstraintValidator} and cause {@link StackOverflowError}.
 */
@Service
public class TripOverlapQueryService {

    private final TripRepository tripRepository;

    public TripOverlapQueryService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean existsVehicleOverlap(
        Long vehicleId,
        LocalDate tripDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Long excludeTripId
    ) {
        return tripRepository.existsVehicleOverlap(vehicleId, tripDate, departureTime, arrivalTime, excludeTripId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean existsDriverOverlap(
        Long driverId,
        LocalDate tripDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Long excludeTripId
    ) {
        return tripRepository.existsDriverOverlap(driverId, tripDate, departureTime, arrivalTime, excludeTripId);
    }
}
