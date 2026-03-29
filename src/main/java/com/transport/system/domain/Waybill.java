package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Waybill.
 */
@Entity
@Table(name = "waybill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Waybill implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "document_number", length = 20, nullable = false, unique = true)
    private String documentNumber;

    @Column(name = "actual_departure")
    private Instant actualDeparture;

    @Column(name = "actual_return")
    private Instant actualReturn;

    @Column(name = "mileage_start", precision = 21, scale = 2)
    private BigDecimal mileageStart;

    @Column(name = "mileage_end", precision = 21, scale = 2)
    private BigDecimal mileageEnd;

    @Column(name = "fuel_consumption_plan", precision = 21, scale = 2)
    private BigDecimal fuelConsumptionPlan;

    @Column(name = "fuel_consumption_fact", precision = 21, scale = 2)
    private BigDecimal fuelConsumptionFact;

    @JsonIgnoreProperties(value = { "waybill", "events", "vehicle", "driver", "route" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "waybill")
    private Trip trip;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Waybill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public Waybill documentNumber(String documentNumber) {
        this.setDocumentNumber(documentNumber);
        return this;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Instant getActualDeparture() {
        return this.actualDeparture;
    }

    public Waybill actualDeparture(Instant actualDeparture) {
        this.setActualDeparture(actualDeparture);
        return this;
    }

    public void setActualDeparture(Instant actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public Instant getActualReturn() {
        return this.actualReturn;
    }

    public Waybill actualReturn(Instant actualReturn) {
        this.setActualReturn(actualReturn);
        return this;
    }

    public void setActualReturn(Instant actualReturn) {
        this.actualReturn = actualReturn;
    }

    public BigDecimal getMileageStart() {
        return this.mileageStart;
    }

    public Waybill mileageStart(BigDecimal mileageStart) {
        this.setMileageStart(mileageStart);
        return this;
    }

    public void setMileageStart(BigDecimal mileageStart) {
        this.mileageStart = mileageStart;
    }

    public BigDecimal getMileageEnd() {
        return this.mileageEnd;
    }

    public Waybill mileageEnd(BigDecimal mileageEnd) {
        this.setMileageEnd(mileageEnd);
        return this;
    }

    public void setMileageEnd(BigDecimal mileageEnd) {
        this.mileageEnd = mileageEnd;
    }

    public BigDecimal getFuelConsumptionPlan() {
        return this.fuelConsumptionPlan;
    }

    public Waybill fuelConsumptionPlan(BigDecimal fuelConsumptionPlan) {
        this.setFuelConsumptionPlan(fuelConsumptionPlan);
        return this;
    }

    public void setFuelConsumptionPlan(BigDecimal fuelConsumptionPlan) {
        this.fuelConsumptionPlan = fuelConsumptionPlan;
    }

    public BigDecimal getFuelConsumptionFact() {
        return this.fuelConsumptionFact;
    }

    public Waybill fuelConsumptionFact(BigDecimal fuelConsumptionFact) {
        this.setFuelConsumptionFact(fuelConsumptionFact);
        return this;
    }

    public void setFuelConsumptionFact(BigDecimal fuelConsumptionFact) {
        this.fuelConsumptionFact = fuelConsumptionFact;
    }

    public Trip getTrip() {
        return this.trip;
    }

    public void setTrip(Trip trip) {
        if (this.trip != null) {
            this.trip.setWaybill(null);
        }
        if (trip != null) {
            trip.setWaybill(this);
        }
        this.trip = trip;
    }

    public Waybill trip(Trip trip) {
        this.setTrip(trip);
        return this;
    }

    @AssertTrue(message = "Фактическое возвращение не может быть раньше выезда")
    public boolean isActualReturnAfterDeparture() {
        return actualDeparture == null || actualReturn == null || !actualReturn.isBefore(actualDeparture);
    }

    @AssertTrue(message = "Конечный пробег не может быть меньше начального")
    public boolean isMileageEndAfterStart() {
        return mileageStart == null || mileageEnd == null || mileageEnd.compareTo(mileageStart) >= 0;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Waybill)) {
            return false;
        }
        return getId() != null && getId().equals(((Waybill) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Waybill{" +
            "id=" + getId() +
            ", documentNumber='" + getDocumentNumber() + "'" +
            ", actualDeparture='" + getActualDeparture() + "'" +
            ", actualReturn='" + getActualReturn() + "'" +
            ", mileageStart=" + getMileageStart() +
            ", mileageEnd=" + getMileageEnd() +
            ", fuelConsumptionPlan=" + getFuelConsumptionPlan() +
            ", fuelConsumptionFact=" + getFuelConsumptionFact() +
            "}";
    }
}
