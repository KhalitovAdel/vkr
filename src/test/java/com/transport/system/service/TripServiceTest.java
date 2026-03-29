package com.transport.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.transport.system.domain.Route;
import com.transport.system.domain.Trip;
import com.transport.system.domain.Vehicle;
import com.transport.system.domain.enumeration.CapacityType;
import com.transport.system.domain.enumeration.RouteType;
import com.transport.system.domain.enumeration.TechnicalStatus;
import com.transport.system.domain.enumeration.TripStatus;
import com.transport.system.repository.DriverRepository;
import com.transport.system.repository.RouteRepository;
import com.transport.system.repository.TripRepository;
import com.transport.system.repository.VehicleRepository;
import com.transport.system.repository.WaybillRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TripServiceTest {

    private TripRepository tripRepository;
    private VehicleRepository vehicleRepository;
    private RouteRepository routeRepository;
    private TripService tripService;

    @BeforeEach
    void setUp() {
        tripRepository = Mockito.mock(TripRepository.class);
        vehicleRepository = Mockito.mock(VehicleRepository.class);
        routeRepository = Mockito.mock(RouteRepository.class);
        tripService = new TripService(
            vehicleRepository,
            tripRepository,
            routeRepository,
            Mockito.mock(DriverRepository.class),
            Mockito.mock(WaybillRepository.class)
        );
    }

    @Test
    void shouldSuggestAvailableVehicle() {
        var route = new Route().id(1L).routeType(RouteType.CITY);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.findRequiredCapacity(1L)).thenReturn(Optional.of(CapacityType.MEDIUM));
        when(tripRepository.findOccupiedVehicleIds(any(), any(), any())).thenReturn(List.of(10L));

        var available = new Vehicle()
            .id(20L)
            .passengerCapacity(80)
            .technicalStatus(TechnicalStatus.OPERATIONAL)
            .capacity(CapacityType.MEDIUM);
        when(vehicleRepository.findAvailableByCapacityAndStatus(any(), any(), any())).thenReturn(List.of(available));

        var suggested = tripService.findAvailableVehicle(1L, LocalDate.now(), LocalTime.NOON);
        assertThat(suggested).isPresent();
        assertThat(suggested.orElseThrow().getId()).isEqualTo(20L);
    }

    @Test
    void shouldRejectInvalidTripTime() {
        var trip = new Trip()
            .tripDate(LocalDate.now())
            .departureTime(LocalTime.of(12, 0))
            .arrivalTime(LocalTime.of(11, 0))
            .tripStatus(TripStatus.SCHEDULED);
        assertThrows(Exception.class, () -> tripService.createTrip(trip));
    }
}
