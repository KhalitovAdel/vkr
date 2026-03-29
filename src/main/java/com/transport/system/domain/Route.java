package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transport.system.domain.enumeration.RouteType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Route.
 */
@Entity
@Table(name = "route")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Route implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "route_number", length = 10, nullable = false, unique = true)
    private String routeNumber;

    @NotNull
    @Size(max = 100)
    @Column(name = "route_name", length = 100, nullable = false)
    private String routeName;

    @NotNull
    @Column(name = "length", precision = 21, scale = 2, nullable = false)
    private BigDecimal length;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false)
    private RouteType routeType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "route")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "waybill", "events", "vehicle", "driver", "route" }, allowSetters = true)
    private Set<Trip> trips = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "route")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "route", "stop" }, allowSetters = true)
    private Set<RouteStop> routeStops = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Route id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return this.routeNumber;
    }

    public Route routeNumber(String routeNumber) {
        this.setRouteNumber(routeNumber);
        return this;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteName() {
        return this.routeName;
    }

    public Route routeName(String routeName) {
        this.setRouteName(routeName);
        return this;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public BigDecimal getLength() {
        return this.length;
    }

    public Route length(BigDecimal length) {
        this.setLength(length);
        return this;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public RouteType getRouteType() {
        return this.routeType;
    }

    public Route routeType(RouteType routeType) {
        this.setRouteType(routeType);
        return this;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setRoute(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setRoute(this));
        }
        this.trips = trips;
    }

    public Route trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Route addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setRoute(this);
        return this;
    }

    public Route removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setRoute(null);
        return this;
    }

    public Set<RouteStop> getRouteStops() {
        return this.routeStops;
    }

    public void setRouteStops(Set<RouteStop> routeStops) {
        if (this.routeStops != null) {
            this.routeStops.forEach(i -> i.setRoute(null));
        }
        if (routeStops != null) {
            routeStops.forEach(i -> i.setRoute(this));
        }
        this.routeStops = routeStops;
    }

    public Route routeStops(Set<RouteStop> routeStops) {
        this.setRouteStops(routeStops);
        return this;
    }

    public Route addRouteStop(RouteStop routeStop) {
        this.routeStops.add(routeStop);
        routeStop.setRoute(this);
        return this;
    }

    public Route removeRouteStop(RouteStop routeStop) {
        this.routeStops.remove(routeStop);
        routeStop.setRoute(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        return getId() != null && getId().equals(((Route) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Route{" +
            "id=" + getId() +
            ", routeNumber='" + getRouteNumber() + "'" +
            ", routeName='" + getRouteName() + "'" +
            ", length=" + getLength() +
            ", routeType='" + getRouteType() + "'" +
            "}";
    }
}
