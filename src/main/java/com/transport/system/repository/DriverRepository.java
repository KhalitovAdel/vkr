package com.transport.system.repository;

import com.transport.system.domain.Driver;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Driver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Поиск свободных водителей на указанную дату и время.
    @Query(
        """
        select d from Driver d
        where d.id not in (
            select t.driver.id from Trip t
            where t.tripDate = :tripDate
              and t.driver is not null
              and :departureTime between t.departureTime and t.arrivalTime
        )
        """
    )
    List<Driver> findAvailableDrivers(@Param("tripDate") LocalDate tripDate, @Param("departureTime") LocalTime departureTime);

    @Query(
        """
        select d from Driver d
        where d.id not in (
            select t.driver.id from Trip t
            where t.tripDate = :tripDate
              and t.driver is not null
              and t.departureTime < :arrivalTime
              and t.arrivalTime > :departureTime
        )
        """
    )
    List<Driver> findAvailableByDateAndTime(
        @Param("tripDate") LocalDate tripDate,
        @Param("departureTime") LocalTime departureTime,
        @Param("arrivalTime") LocalTime arrivalTime
    );
}
