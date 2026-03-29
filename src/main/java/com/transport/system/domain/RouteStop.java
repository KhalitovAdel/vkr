package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RouteStop.
 */
@Entity
@Table(name = "route_stop")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RouteStop implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "distance_from_prev", precision = 21, scale = 2)
    private BigDecimal distanceFromPrev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "trips", "routeStops" }, allowSetters = true)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "routeStops" }, allowSetters = true)
    private Stop stop;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RouteStop id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStopOrder() {
        return this.stopOrder;
    }

    public RouteStop stopOrder(Integer stopOrder) {
        this.setStopOrder(stopOrder);
        return this;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }

    public BigDecimal getDistanceFromPrev() {
        return this.distanceFromPrev;
    }

    public RouteStop distanceFromPrev(BigDecimal distanceFromPrev) {
        this.setDistanceFromPrev(distanceFromPrev);
        return this;
    }

    public void setDistanceFromPrev(BigDecimal distanceFromPrev) {
        this.distanceFromPrev = distanceFromPrev;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public RouteStop route(Route route) {
        this.setRoute(route);
        return this;
    }

    public Stop getStop() {
        return this.stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public RouteStop stop(Stop stop) {
        this.setStop(stop);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouteStop)) {
            return false;
        }
        return getId() != null && getId().equals(((RouteStop) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RouteStop{" +
            "id=" + getId() +
            ", stopOrder=" + getStopOrder() +
            ", distanceFromPrev=" + getDistanceFromPrev() +
            "}";
    }
}
