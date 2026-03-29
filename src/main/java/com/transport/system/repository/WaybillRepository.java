package com.transport.system.repository;

import com.transport.system.domain.Waybill;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Waybill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WaybillRepository extends JpaRepository<Waybill, Long> {
    @Query(
        """
        select coalesce(sum(w.fuelConsumptionFact), 0)
        from Waybill w join w.trip t
        where t.vehicle.id = :vehicleId
          and t.tripDate between :dateFrom and :dateTo
          and w.fuelConsumptionFact is not null
        """
    )
    BigDecimal sumFuelConsumptionByVehicleAndPeriod(
        @Param("vehicleId") Long vehicleId,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );
}
