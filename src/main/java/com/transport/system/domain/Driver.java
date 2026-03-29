package com.transport.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Driver.
 */
@Entity
@Table(name = "driver")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Driver implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "employee_number", length = 20, nullable = false, unique = true)
    private String employeeNumber;

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @NotNull
    @Size(max = 10)
    @Column(name = "license_category", length = 10, nullable = false)
    private String licenseCategory;

    @NotNull
    @Min(0)
    @Column(name = "experience", nullable = false)
    private Integer experience;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "driver")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "waybill", "events", "vehicle", "driver", "route" }, allowSetters = true)
    private Set<Trip> trips = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Driver id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeNumber() {
        return this.employeeNumber;
    }

    public Driver employeeNumber(String employeeNumber) {
        this.setEmployeeNumber(employeeNumber);
        return this;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Driver fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLicenseCategory() {
        return this.licenseCategory;
    }

    public Driver licenseCategory(String licenseCategory) {
        this.setLicenseCategory(licenseCategory);
        return this;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public Integer getExperience() {
        return this.experience;
    }

    public Driver experience(Integer experience) {
        this.setExperience(experience);
        return this;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public LocalDate getHireDate() {
        return this.hireDate;
    }

    public Driver hireDate(LocalDate hireDate) {
        this.setHireDate(hireDate);
        return this;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setDriver(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setDriver(this));
        }
        this.trips = trips;
    }

    public Driver trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Driver addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setDriver(this);
        return this;
    }

    public Driver removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setDriver(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Driver)) {
            return false;
        }
        return getId() != null && getId().equals(((Driver) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Driver{" +
            "id=" + getId() +
            ", employeeNumber='" + getEmployeeNumber() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", licenseCategory='" + getLicenseCategory() + "'" +
            ", experience=" + getExperience() +
            ", hireDate='" + getHireDate() + "'" +
            "}";
    }
}
