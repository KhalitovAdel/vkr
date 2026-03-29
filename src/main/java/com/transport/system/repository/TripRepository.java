package com.transport.system.repository;

import com.transport.system.domain.Trip;
import com.transport.system.domain.enumeration.TripStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Trip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // Поиск рейсов, где ТС занято в указанную дату и момент отправления.
    @Query("select t from Trip t where t.tripDate = :tripDate and :departureTime between t.departureTime and t.arrivalTime")
    List<Trip> findBusyVehiclesByDateTime(@Param("tripDate") LocalDate tripDate, @Param("departureTime") LocalTime departureTime);

    @Query(
        """
        select t.vehicle.id from Trip t
        where t.tripDate = :tripDate
          and t.vehicle is not null
          and t.departureTime < :arrivalTime
          and t.arrivalTime > :departureTime
        """
    )
    List<Long> findOccupiedVehicleIds(
        @Param("tripDate") LocalDate tripDate,
        @Param("departureTime") LocalTime departureTime,
        @Param("arrivalTime") LocalTime arrivalTime
    );

    @Query(
        """
        select t.driver.id from Trip t
        where t.tripDate = :tripDate
          and t.driver is not null
          and t.departureTime < :arrivalTime
          and t.arrivalTime > :departureTime
        """
    )
    List<Long> findOccupiedDriverIds(
        @Param("tripDate") LocalDate tripDate,
        @Param("departureTime") LocalTime departureTime,
        @Param("arrivalTime") LocalTime arrivalTime
    );

    @Query(
        """
        select (count(t) > 0) from Trip t
        where t.tripDate = :tripDate
          and t.vehicle.id = :vehicleId
          and (:excludeTripId is null or t.id <> :excludeTripId)
          and t.departureTime < :arrivalTime
          and t.arrivalTime > :departureTime
        """
    )
    boolean existsVehicleOverlap(
        @Param("vehicleId") Long vehicleId,
        @Param("tripDate") LocalDate tripDate,
        @Param("departureTime") LocalTime departureTime,
        @Param("arrivalTime") LocalTime arrivalTime,
        @Param("excludeTripId") Long excludeTripId
    );

    @Query(
        """
        select (count(t) > 0) from Trip t
        where t.tripDate = :tripDate
          and t.driver.id = :driverId
          and (:excludeTripId is null or t.id <> :excludeTripId)
          and t.departureTime < :arrivalTime
          and t.arrivalTime > :departureTime
        """
    )
    boolean existsDriverOverlap(
        @Param("driverId") Long driverId,
        @Param("tripDate") LocalDate tripDate,
        @Param("departureTime") LocalTime departureTime,
        @Param("arrivalTime") LocalTime arrivalTime,
        @Param("excludeTripId") Long excludeTripId
    );

    List<Trip> findByTripDate(LocalDate tripDate);

    List<Trip> findByVehicleIdAndTripDate(Long vehicleId, LocalDate tripDate);

    List<Trip> findByDriverIdAndTripDate(Long driverId, LocalDate tripDate);

    long countByTripDateAndTripStatus(LocalDate tripDate, TripStatus tripStatus);

    @Query("select count(t) from Trip t where t.tripDate = :tripDate and t.tripStatus = :status")
    long countByTripDateAndStatus(@Param("tripDate") LocalDate tripDate, @Param("status") TripStatus status);

    @Query(
        """
        select coalesce(sum(w.mileageEnd - w.mileageStart), 0)
        from Trip t join t.waybill w
        where t.vehicle.id = :vehicleId
          and t.tripDate between :dateFrom and :dateTo
          and w.mileageStart is not null
          and w.mileageEnd is not null
        """
    )
    BigDecimal sumMileageByVehicleAndPeriod(
        @Param("vehicleId") Long vehicleId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );
}
