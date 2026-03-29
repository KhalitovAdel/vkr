package com.transport.system.repository;

import com.transport.system.domain.Route;
import com.transport.system.domain.enumeration.CapacityType;
import com.transport.system.domain.enumeration.RouteType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Route entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Поиск маршрутов по типу.
    List<Route> findByRouteType(RouteType routeType);

    // Требуемая вместимость для маршрута (условное правило по типу маршрута).
    @Query(
        """
        select case
            when r.routeType = com.transport.system.domain.enumeration.RouteType.SUBURB then com.transport.system.domain.enumeration.CapacityType.LARGE
            else com.transport.system.domain.enumeration.CapacityType.MEDIUM
        end
        from Route r
        where r.id = :routeId
        """
    )
    Optional<CapacityType> findRequiredCapacity(@Param("routeId") Long routeId);
}
