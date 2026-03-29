package com.transport.system.web.rest;

import com.transport.system.service.TripService;
import com.transport.system.service.VehicleService;
import com.transport.system.service.dto.DowntimeStatisticsDTO;
import com.transport.system.service.dto.FleetStatisticsDTO;
import com.transport.system.service.dto.MonthlyStatisticsDTO;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportResource {

    private final VehicleService vehicleService;
    private final TripService tripService;

    public ReportResource(VehicleService vehicleService, TripService tripService) {
        this.vehicleService = vehicleService;
        this.tripService = tripService;
    }

    @GetMapping("/fleet")
    public ResponseEntity<FleetStatisticsDTO> getFleetReport() {
        return ResponseEntity.ok(vehicleService.getFleetStatistics());
    }

    @GetMapping("/trips")
    public ResponseEntity<Map<String, Object>> getTripsReport(
        @RequestParam("date") LocalDate date,
        @RequestParam(name = "vehicleId", required = false) Long vehicleId,
        @RequestParam(name = "driverId", required = false) Long driverId
    ) {
        if (vehicleId != null) {
            return ResponseEntity.ok(Map.of("items", tripService.getTripsByVehicleAndDate(vehicleId, date)));
        }
        if (driverId != null) {
            return ResponseEntity.ok(Map.of("items", tripService.getTripsByDriverAndDate(driverId, date)));
        }
        return ResponseEntity.ok(Map.of("items", tripService.getTripsByDate(date)));
    }

    @GetMapping("/trips/monthly")
    public ResponseEntity<MonthlyStatisticsDTO> getMonthlyTripsReport(@RequestParam("year") int year, @RequestParam("month") int month) {
        return ResponseEntity.ok(tripService.getMonthlyStatistics(year, month));
    }

    /**
     * «Простои»: ТС в ремонте и списанные; исправные без рейса на выбранную дату (без телематики — по данным учёта).
     */
    @GetMapping("/downtime")
    public ResponseEntity<DowntimeStatisticsDTO> getDowntimeReport(@RequestParam(value = "date", required = false) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(vehicleService.getDowntimeStatistics(d));
    }
}
