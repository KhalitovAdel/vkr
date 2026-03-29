package com.transport.system.service;

import com.transport.system.domain.enumeration.TechnicalStatus;
import com.transport.system.repository.VehicleRepository;
import com.transport.system.service.dto.DowntimeStatisticsDTO;
import com.transport.system.service.dto.FleetStatisticsDTO;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Возвращает агрегированную статистику по автопарку.
    public FleetStatisticsDTO getFleetStatistics() {
        var total = vehicleRepository.count();
        var operational = vehicleRepository.countByTechnicalStatus(TechnicalStatus.OPERATIONAL);
        var repair = vehicleRepository.countByTechnicalStatus(TechnicalStatus.REPAIR);
        return new FleetStatisticsDTO(total, operational, repair);
    }

    /** Сводка по «простоям»: ремонт, списание, исправные без рейса на дату. */
    public DowntimeStatisticsDTO getDowntimeStatistics(LocalDate date) {
        var repair = vehicleRepository.countByTechnicalStatus(TechnicalStatus.REPAIR);
        var scrapped = vehicleRepository.countByTechnicalStatus(TechnicalStatus.SCRAPPED);
        var opIdle = vehicleRepository.countOperationalWithNoTripOnDate(TechnicalStatus.OPERATIONAL, date);
        return new DowntimeStatisticsDTO(date, repair, scrapped, opIdle);
    }
}
