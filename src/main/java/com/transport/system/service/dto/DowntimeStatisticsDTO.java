package com.transport.system.service.dto;

import java.time.LocalDate;

/** Показатели «простоев»: ТС в ремонте/списании и исправные без рейса на выбранную дату. */
public class DowntimeStatisticsDTO {

    private LocalDate date;
    /** Количество ТС в статусе REPAIR. */
    private long vehiclesInRepair;
    /** Количество ТС в статусе SCRAPPED. */
    private long vehiclesScrapped;
    /**
     * Исправные ТС (OPERATIONAL), на которые не назначен ни один рейс на эту дату.
     */
    private long operationalWithoutTripOnDate;

    public DowntimeStatisticsDTO(
        LocalDate date,
        long vehiclesInRepair,
        long vehiclesScrapped,
        long operationalWithoutTripOnDate
    ) {
        this.date = date;
        this.vehiclesInRepair = vehiclesInRepair;
        this.vehiclesScrapped = vehiclesScrapped;
        this.operationalWithoutTripOnDate = operationalWithoutTripOnDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getVehiclesInRepair() {
        return vehiclesInRepair;
    }

    public void setVehiclesInRepair(long vehiclesInRepair) {
        this.vehiclesInRepair = vehiclesInRepair;
    }

    public long getVehiclesScrapped() {
        return vehiclesScrapped;
    }

    public void setVehiclesScrapped(long vehiclesScrapped) {
        this.vehiclesScrapped = vehiclesScrapped;
    }

    public long getOperationalWithoutTripOnDate() {
        return operationalWithoutTripOnDate;
    }

    public void setOperationalWithoutTripOnDate(long operationalWithoutTripOnDate) {
        this.operationalWithoutTripOnDate = operationalWithoutTripOnDate;
    }
}
