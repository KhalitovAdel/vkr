package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transport.system.domain.enumeration.TripStatus;
import com.transport.system.service.validation.NoOverlapConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Trip.
 */
@Entity
@Table(name = "trip")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@NoOverlapConstraint
public class Trip implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NotNull
    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status", nullable = false)
    private TripStatus tripStatus;

    @JsonIgnoreProperties(value = { "trip" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Waybill waybill;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "trip", "vehicle" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "trips", "events" }, allowSetters = true)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "trips" }, allowSetters = true)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "trips", "routeStops" }, allowSetters = true)
    private Route route;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Trip id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    public Trip departureTime(LocalTime departureTime) {
        this.setDepartureTime(departureTime);
        return this;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return this.arrivalTime;
    }

    public Trip arrivalTime(LocalTime arrivalTime) {
        this.setArrivalTime(arrivalTime);
        return this;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDate getTripDate() {
        return this.tripDate;
    }

    public Trip tripDate(LocalDate tripDate) {
        this.setTripDate(tripDate);
        return this;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public TripStatus getTripStatus() {
        return this.tripStatus;
    }

    public Trip tripStatus(TripStatus tripStatus) {
        this.setTripStatus(tripStatus);
        return this;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public Waybill getWaybill() {
        return this.waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public Trip waybill(Waybill waybill) {
        this.setWaybill(waybill);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setTrip(null));
        }
        if (events != null) {
            events.forEach(i -> i.setTrip(this));
        }
        this.events = events;
    }

    public Trip events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Trip addEvent(Event event) {
        this.events.add(event);
        event.setTrip(this);
        return this;
    }

    public Trip removeEvent(Event event) {
        this.events.remove(event);
        event.setTrip(null);
        return this;
    }

    @AssertTrue(message = "Время прибытия должно быть позже времени отправления")
    public boolean isArrivalAfterDeparture() {
        return departureTime == null || arrivalTime == null || arrivalTime.isAfter(departureTime);
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Trip vehicle(Vehicle vehicle) {
        this.setVehicle(vehicle);
        return this;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trip driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Trip route(Route route) {
        this.setRoute(route);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trip)) {
            return false;
        }
        return getId() != null && getId().equals(((Trip) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Trip{" +
            "id=" + getId() +
            ", departureTime='" + getDepartureTime() + "'" +
            ", arrivalTime='" + getArrivalTime() + "'" +
            ", tripDate='" + getTripDate() + "'" +
            ", tripStatus='" + getTripStatus() + "'" +
            "}";
    }
}
