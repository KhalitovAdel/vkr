package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transport.system.domain.enumeration.CapacityType;
import com.transport.system.domain.enumeration.TechnicalStatus;
import com.transport.system.domain.enumeration.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Vehicle.
 */
@Entity
@Table(name = "vehicle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Vehicle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "state_number", length = 10, nullable = false, unique = true)
    private String stateNumber;

    @NotNull
    @Size(max = 50)
    @Column(name = "model", length = 50, nullable = false)
    private String model;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "capacity", nullable = false)
    private CapacityType capacity;

    @NotNull
    @Positive
    @Column(name = "passenger_capacity", nullable = false)
    private Integer passengerCapacity;

    @NotNull
    @Positive
    @Column(name = "manufacture_year", nullable = false)
    private Integer year;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "technical_status", nullable = false)
    private TechnicalStatus technicalStatus;

    @NotNull
    @Column(name = "mileage", precision = 21, scale = 2, nullable = false)
    private BigDecimal mileage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vehicle")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "waybill", "events", "vehicle", "driver", "route" }, allowSetters = true)
    private Set<Trip> trips = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vehicle")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "trip", "vehicle" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vehicle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStateNumber() {
        return this.stateNumber;
    }

    public Vehicle stateNumber(String stateNumber) {
        this.setStateNumber(stateNumber);
        return this;
    }

    public void setStateNumber(String stateNumber) {
        this.stateNumber = stateNumber;
    }

    public String getModel() {
        return this.model;
    }

    public Vehicle model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public Vehicle vehicleType(VehicleType vehicleType) {
        this.setVehicleType(vehicleType);
        return this;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public CapacityType getCapacity() {
        return this.capacity;
    }

    public Vehicle capacity(CapacityType capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(CapacityType capacity) {
        this.capacity = capacity;
    }

    public Integer getPassengerCapacity() {
        return this.passengerCapacity;
    }

    public Vehicle passengerCapacity(Integer passengerCapacity) {
        this.setPassengerCapacity(passengerCapacity);
        return this;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Integer getYear() {
        return this.year;
    }

    public Vehicle year(Integer year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public TechnicalStatus getTechnicalStatus() {
        return this.technicalStatus;
    }

    public Vehicle technicalStatus(TechnicalStatus technicalStatus) {
        this.setTechnicalStatus(technicalStatus);
        return this;
    }

    public void setTechnicalStatus(TechnicalStatus technicalStatus) {
        this.technicalStatus = technicalStatus;
    }

    public BigDecimal getMileage() {
        return this.mileage;
    }

    public Vehicle mileage(BigDecimal mileage) {
        this.setMileage(mileage);
        return this;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setVehicle(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setVehicle(this));
        }
        this.trips = trips;
    }

    public Vehicle trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Vehicle addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setVehicle(this);
        return this;
    }

    public Vehicle removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setVehicle(null);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setVehicle(null));
        }
        if (events != null) {
            events.forEach(i -> i.setVehicle(this));
        }
        this.events = events;
    }

    public Vehicle events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Vehicle addEvent(Event event) {
        this.events.add(event);
        event.setVehicle(this);
        return this;
    }

    public Vehicle removeEvent(Event event) {
        this.events.remove(event);
        event.setVehicle(null);
        return this;
    }

    @AssertTrue(message = "Год выпуска не может быть больше текущего года")
    public boolean isYearValid() {
        return year == null || year <= Year.now().getValue();
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehicle)) {
            return false;
        }
        return getId() != null && getId().equals(((Vehicle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + getId() +
            ", stateNumber='" + getStateNumber() + "'" +
            ", model='" + getModel() + "'" +
            ", vehicleType='" + getVehicleType() + "'" +
            ", capacity='" + getCapacity() + "'" +
            ", passengerCapacity=" + getPassengerCapacity() +
            ", year=" + getYear() +
            ", technicalStatus='" + getTechnicalStatus() + "'" +
            ", mileage=" + getMileage() +
            "}";
    }
}
