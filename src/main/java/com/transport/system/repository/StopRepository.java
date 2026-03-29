package com.transport.system.repository;

import com.transport.system.domain.Stop;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Stop entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {}
