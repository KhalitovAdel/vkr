package com.transport.system.service;

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
import com.transport.system.service.dto.MonthlyStatisticsDTO;
import com.transport.system.service.dto.TripSuggestionRequest;
import com.transport.system.web.rest.errors.BadRequestAlertException;
import com.transport.system.web.rest.errors.ErrorConstants;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TripService {

    private static final Logger log = LoggerFactory.getLogger(TripService.class);

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final WaybillRepository waybillRepository;

    public TripService(
        VehicleRepository vehicleRepository,
        TripRepository tripRepository,
        RouteRepository routeRepository,
        DriverRepository driverRepository,
        WaybillRepository waybillRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
        this.driverRepository = driverRepository;
        this.waybillRepository = waybillRepository;
    }

    // Автоматический подбор свободного ТС с учетом типа маршрута и занятости.
    @Transactional(readOnly = true)
    public Optional<Vehicle> findAvailableVehicle(Long routeId, LocalDate tripDate, LocalTime departureTime) {
        var route = routeRepository
            .findById(routeId)
            .orElseThrow(() -> new BadRequestAlertException("Маршрут не найден", "trip", ErrorConstants.ERR_VALIDATION));

        var arrivalTime = departureTime.plusHours(1);
        var busyVehicleIds = tripRepository
            .findOccupiedVehicleIds(tripDate, departureTime, arrivalTime)
            .stream()
            .collect(Collectors.toSet());
        var excludedVehicleIds = busyVehicleIds.isEmpty() ? null : busyVehicleIds;

        var requiredCapacity = routeRepository
            .findRequiredCapacity(routeId)
            .orElse(route.getRouteType() == RouteType.SUBURB ? CapacityType.LARGE : CapacityType.MEDIUM);
        Set<CapacityType> acceptedCapacities =
            requiredCapacity == CapacityType.LARGE
                ? Set.of(CapacityType.LARGE)
                : requiredCapacity == CapacityType.MEDIUM
                    ? Set.of(CapacityType.MEDIUM, CapacityType.LARGE)
                    : Set.of(CapacityType.SMALL, CapacityType.MEDIUM, CapacityType.LARGE);

        var candidates = acceptedCapacities
            .stream()
            .flatMap(capacity ->
                vehicleRepository.findAvailableByCapacityAndStatus(capacity, TechnicalStatus.OPERATIONAL, excludedVehicleIds).stream()
            )
            .toList();
        return candidates.stream().min(Comparator.comparing(Vehicle::getPassengerCapacity));
    }

    // API-метод для фронтенда, возвращающий предложенное ТС.
    @Transactional(readOnly = true)
    public Optional<Vehicle> suggestVehicleForTrip(TripSuggestionRequest request) {
        log.debug("Запрос подбора ТС: {}", request);
        return findAvailableVehicle(request.getRouteId(), request.getTripDate(), request.getDepartureTime());
    }

    // Проверка валидности временного интервала рейса.
    public void validateTripTime(LocalTime departureTime, LocalTime arrivalTime) {
        if (!arrivalTime.isAfter(departureTime)) {
            throw new BadRequestAlertException(
                "Время прибытия должно быть позже времени отправления",
                "trip",
                ErrorConstants.ERR_VALIDATION
            );
        }
    }

    // Бизнес-валидация назначения ТС/водителя на рейс.
    public void validateTripAssignments(Trip trip) {
        validateTripTime(trip.getDepartureTime(), trip.getArrivalTime());

        if (trip.getVehicle() == null || trip.getVehicle().getId() == null) {
            return;
        }

        var vehicle = vehicleRepository
            .findById(trip.getVehicle().getId())
            .orElseThrow(() -> new BadRequestAlertException("Транспортное средство не найдено", "trip", ErrorConstants.ERR_VALIDATION));
        if (vehicle.getTechnicalStatus() == TechnicalStatus.REPAIR) {
            throw new BadRequestAlertException("Нельзя назначить ТС в ремонте", "trip", ErrorConstants.ERR_VALIDATION);
        }

        if (
            tripRepository.existsVehicleOverlap(
                vehicle.getId(),
                trip.getTripDate(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getId()
            )
        ) {
            throw new BadRequestAlertException(
                "Транспортное средство уже занято в указанный период",
                "trip",
                ErrorConstants.ERR_VALIDATION
            );
        }

        if (trip.getDriver() != null && trip.getDriver().getId() != null) {
            var driver = driverRepository
                .findById(trip.getDriver().getId())
                .orElseThrow(() -> new BadRequestAlertException("Водитель не найден", "trip", ErrorConstants.ERR_VALIDATION));
            if (
                tripRepository.existsDriverOverlap(
                    driver.getId(),
                    trip.getTripDate(),
                    trip.getDepartureTime(),
                    trip.getArrivalTime(),
                    trip.getId()
                )
            ) {
                throw new BadRequestAlertException("Водитель уже занят в указанный период", "trip", ErrorConstants.ERR_VALIDATION);
            }
            if (trip.getRoute() != null && trip.getRoute().getRouteType() == RouteType.SUBURB && driver.getExperience() < 3) {
                throw new BadRequestAlertException(
                    "Для пригородных маршрутов требуется водитель со стажем не менее 3 лет",
                    "trip",
                    ErrorConstants.ERR_VALIDATION
                );
            }
        }
    }

    public Trip createTrip(@Valid Trip trip) {
        validateTripAssignments(trip);
        return tripRepository.save(trip);
    }

    private static Sort parseSortParameter(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction =
            parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }

    @Transactional(readOnly = true)
    public List<Trip> findAllTrips(String sort) {
        Sort s = parseSortParameter(sort);
        return s.isUnsorted() ? tripRepository.findAll() : tripRepository.findAll(s);
    }

    @Transactional(readOnly = true)
    public Optional<Trip> findTrip(Long id) {
        return tripRepository.findById(id);
    }

    public Trip updateTrip(Long id, Trip trip) {
        if (trip.getId() == null) {
            throw new BadRequestAlertException("Некорректный id", "trip", ErrorConstants.ERR_VALIDATION);
        }
        if (!Objects.equals(id, trip.getId())) {
            throw new BadRequestAlertException("ID в URL и в теле не совпадают", "trip", ErrorConstants.ERR_VALIDATION);
        }
        if (!tripRepository.existsById(id)) {
            throw new BadRequestAlertException("Рейс не найден", "trip", ErrorConstants.ERR_VALIDATION);
        }
        validateTripAssignments(trip);
        return tripRepository.save(trip);
    }

    public Optional<Trip> partialUpdateTrip(Long id, Trip incoming) {
        return tripRepository
            .findById(id)
            .map(existing -> {
                updateIfPresent(existing::setDepartureTime, incoming.getDepartureTime());
                updateIfPresent(existing::setArrivalTime, incoming.getArrivalTime());
                updateIfPresent(existing::setTripDate, incoming.getTripDate());
                updateIfPresent(existing::setTripStatus, incoming.getTripStatus());
                updateIfPresent(existing::setWaybill, incoming.getWaybill());
                updateIfPresent(existing::setVehicle, incoming.getVehicle());
                updateIfPresent(existing::setDriver, incoming.getDriver());
                updateIfPresent(existing::setRoute, incoming.getRoute());
                validateTripAssignments(existing);
                return tripRepository.save(existing);
            });
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    private static <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Transactional(readOnly = true)
    public List<Trip> getTripsByDate(LocalDate date) {
        return tripRepository.findByTripDate(date);
    }

    @Transactional(readOnly = true)
    public List<Trip> getTripsByVehicleAndDate(Long vehicleId, LocalDate date) {
        return tripRepository.findByVehicleIdAndTripDate(vehicleId, date);
    }

    @Transactional(readOnly = true)
    public List<Trip> getTripsByDriverAndDate(Long driverId, LocalDate date) {
        return tripRepository.findByDriverIdAndTripDate(driverId, date);
    }

    @Transactional(readOnly = true)
    public MonthlyStatisticsDTO getMonthlyStatistics(int year, int month) {
        var from = LocalDate.of(year, month, 1);
        var to = from.withDayOfMonth(from.lengthOfMonth());
        var trips = tripRepository
            .findAll()
            .stream()
            .filter(t -> !t.getTripDate().isBefore(from) && !t.getTripDate().isAfter(to))
            .toList();
        var vehicleIds = trips
            .stream()
            .map(Trip::getVehicle)
            .filter(Objects::nonNull)
            .map(Vehicle::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        var mileage = vehicleIds
            .stream()
            .map(id -> tripRepository.sumMileageByVehicleAndPeriod(id, from, to))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        var fuel = vehicleIds
            .stream()
            .map(id -> waybillRepository.sumFuelConsumptionByVehicleAndPeriod(id, from, to))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MonthlyStatisticsDTO(trips.size(), mileage, fuel);
    }
}
