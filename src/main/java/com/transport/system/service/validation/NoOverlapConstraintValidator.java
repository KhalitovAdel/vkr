package com.transport.system.service.validation;

import com.transport.system.domain.Trip;
import com.transport.system.service.TripOverlapQueryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoOverlapConstraintValidator implements ConstraintValidator<NoOverlapConstraint, Trip> {

    private final TripOverlapQueryService tripOverlapQueryService;

    public NoOverlapConstraintValidator(TripOverlapQueryService tripOverlapQueryService) {
        this.tripOverlapQueryService = tripOverlapQueryService;
    }

    @Override
    public boolean isValid(Trip value, ConstraintValidatorContext context) {
        if (value == null || value.getTripDate() == null || value.getDepartureTime() == null || value.getArrivalTime() == null) {
            return true;
        }

        var excludeTripId = value.getId();
        if (value.getVehicle() != null && value.getVehicle().getId() != null) {
            var vehicleOverlap = tripOverlapQueryService.existsVehicleOverlap(
                value.getVehicle().getId(),
                value.getTripDate(),
                value.getDepartureTime(),
                value.getArrivalTime(),
                excludeTripId
            );
            if (vehicleOverlap) {
                return false;
            }
        }

        if (value.getDriver() != null && value.getDriver().getId() != null) {
            var driverOverlap = tripOverlapQueryService.existsDriverOverlap(
                value.getDriver().getId(),
                value.getTripDate(),
                value.getDepartureTime(),
                value.getArrivalTime(),
                excludeTripId
            );
            return !driverOverlap;
        }

        return true;
    }
}
