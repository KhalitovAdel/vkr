package com.transport.system.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class TripSuggestionRequest {

    @NotNull
    private Long routeId;

    @NotNull
    private LocalDate tripDate;

    @NotNull
    private LocalTime departureTime;

    public TripSuggestionRequest() {}

    public TripSuggestionRequest(Long routeId, LocalDate tripDate, LocalTime departureTime) {
        this.routeId = routeId;
        this.tripDate = tripDate;
        this.departureTime = departureTime;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }
}
