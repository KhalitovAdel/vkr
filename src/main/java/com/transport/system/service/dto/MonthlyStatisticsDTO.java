package com.transport.system.service.dto;

import java.math.BigDecimal;

public class MonthlyStatisticsDTO {

    private int tripsCount;
    private BigDecimal totalMileage;
    private BigDecimal totalFuelConsumption;

    public MonthlyStatisticsDTO(int tripsCount, BigDecimal totalMileage, BigDecimal totalFuelConsumption) {
        this.tripsCount = tripsCount;
        this.totalMileage = totalMileage;
        this.totalFuelConsumption = totalFuelConsumption;
    }

    public int getTripsCount() {
        return tripsCount;
    }

    public void setTripsCount(int tripsCount) {
        this.tripsCount = tripsCount;
    }

    public BigDecimal getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(BigDecimal totalMileage) {
        this.totalMileage = totalMileage;
    }

    public BigDecimal getTotalFuelConsumption() {
        return totalFuelConsumption;
    }

    public void setTotalFuelConsumption(BigDecimal totalFuelConsumption) {
        this.totalFuelConsumption = totalFuelConsumption;
    }
}
