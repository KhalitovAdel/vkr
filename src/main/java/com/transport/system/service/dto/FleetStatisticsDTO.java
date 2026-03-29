package com.transport.system.service.dto;

public class FleetStatisticsDTO {
    private long total;
    private long operational;
    private long repair;

    public FleetStatisticsDTO(long total, long operational, long repair) {
        this.total = total;
        this.operational = operational;
        this.repair = repair;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOperational() {
        return operational;
    }

    public void setOperational(long operational) {
        this.operational = operational;
    }

    public long getRepair() {
        return repair;
    }

    public void setRepair(long repair) {
        this.repair = repair;
    }
}
