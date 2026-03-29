package com.transport.system.repository;

import com.transport.system.domain.Vehicle;
import com.transport.system.domain.enumeration.CapacityType;
import com.transport.system.domain.enumeration.TechnicalStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Vehicle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Поиск ТС по вместимости и техническому статусу.
    @Query("select v from Vehicle v where v.capacity = :capacity and v.technicalStatus = :status")
    List<Vehicle> findByCapacityAndStatus(@Param("capacity") CapacityType capacity, @Param("status") TechnicalStatus status);

    // Поиск доступных ТС с исключением занятых.
    @Query(
        """
        select v from Vehicle v
        where v.capacity = :capacity
          and v.technicalStatus = :status
          and (:occupiedIds is null or v.id not in :occupiedIds)
        """
    )
    List<Vehicle> findAvailableByCapacityAndStatus(
        @Param("capacity") CapacityType capacity,
        @Param("status") TechnicalStatus status,
        @Param("occupiedIds") Set<Long> occupiedIds
    );

    // Подсчет количества ТС по техническому статусу.
    long countByTechnicalStatus(TechnicalStatus status);

    @Query("select count(v) from Vehicle v where v.technicalStatus = :status")
    long countByStatus(@Param("status") TechnicalStatus status);

    List<Vehicle> findByTechnicalStatusNot(TechnicalStatus technicalStatus);

    /** Исправные ТС без ни одного рейса на указанную дату (потенциальный «простой» парка по плану). */
    @Query(
        """
        select count(v) from Vehicle v
        where v.technicalStatus = :operational
        and not exists (select 1 from Trip t where t.vehicle.id = v.id and t.tripDate = :date)
        """
    )
    long countOperationalWithNoTripOnDate(@Param("operational") TechnicalStatus operational, @Param("date") LocalDate date);
}
