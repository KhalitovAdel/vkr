package com.transport.system.service;

import com.transport.system.domain.Waybill;
import com.transport.system.domain.enumeration.TripStatus;
import com.transport.system.repository.TripRepository;
import com.transport.system.repository.WaybillRepository;
import com.transport.system.web.rest.errors.BadRequestAlertException;
import com.transport.system.web.rest.errors.ErrorConstants;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WaybillService {

    private final WaybillRepository waybillRepository;
    private final TripRepository tripRepository;

    public WaybillService(WaybillRepository waybillRepository, TripRepository tripRepository) {
        this.waybillRepository = waybillRepository;
        this.tripRepository = tripRepository;
    }

    // Фиксация фактического выезда ТС на линию.
    public Waybill registerDeparture(Long waybillId, Instant departureTime, BigDecimal mileageStart) {
        var waybill = waybillRepository
            .findById(waybillId)
            .orElseThrow(() -> new BadRequestAlertException("Путевой лист не найден", "waybill", ErrorConstants.ERR_VALIDATION));
        waybill.setActualDeparture(departureTime);
        if (mileageStart != null) {
            waybill.setMileageStart(mileageStart);
        }
        if (waybill.getTrip() != null) {
            waybill.getTrip().setTripStatus(TripStatus.ONGOING);
            tripRepository.save(waybill.getTrip());
        }
        return waybillRepository.save(waybill);
    }

    // Фиксация возврата ТС с линии с проверками пробега/времени.
    public Waybill registerReturn(Long waybillId, Instant returnTime, BigDecimal mileageEnd) {
        var waybill = waybillRepository
            .findById(waybillId)
            .orElseThrow(() -> new BadRequestAlertException("Путевой лист не найден", "waybill", ErrorConstants.ERR_VALIDATION));
        if (waybill.getActualDeparture() != null && returnTime.isBefore(waybill.getActualDeparture())) {
            throw new BadRequestAlertException("Время возвращения не может быть раньше выезда", "waybill", ErrorConstants.ERR_VALIDATION);
        }
        if (waybill.getMileageStart() != null && mileageEnd != null && mileageEnd.compareTo(waybill.getMileageStart()) < 0) {
            throw new BadRequestAlertException("Конечный пробег не может быть меньше начального", "waybill", ErrorConstants.ERR_VALIDATION);
        }
        waybill.setActualReturn(returnTime);
        if (mileageEnd != null) {
            waybill.setMileageEnd(mileageEnd);
        }
        if (waybill.getTrip() != null) {
            waybill.getTrip().setTripStatus(TripStatus.COMPLETED);
            tripRepository.save(waybill.getTrip());
        }
        return waybillRepository.save(waybill);
    }
}
