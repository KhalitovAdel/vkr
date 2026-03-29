package com.transport.system.web.rest;

import com.transport.system.domain.Trip;
import com.transport.system.domain.Vehicle;
import com.transport.system.service.TripService;
import com.transport.system.service.dto.TripSuggestionRequest;
import com.transport.system.web.rest.errors.BadRequestAlertException;
import com.transport.system.web.rest.errors.ErrorConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/trips")
@Transactional
public class TripResource {

    private static final Logger LOG = LoggerFactory.getLogger(TripResource.class);

    private static final String ENTITY_NAME = "trip";

    @Value("${jhipster.clientApp.name:transportSystem}")
    private String applicationName;

    private final TripService tripService;

    public TripResource(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/suggest-vehicle")
    public ResponseEntity<Vehicle> suggestVehicle(@Valid @RequestBody TripSuggestionRequest request) {
        Optional<Vehicle> result = tripService.suggestVehicleForTrip(request);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Trip>> getTripsByDate(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(tripService.getTripsByDate(date));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Trip>> getTripsByVehicleAndDate(
        @PathVariable Long vehicleId,
        @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(tripService.getTripsByVehicleAndDate(vehicleId, date));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Trip>> getTripsByDriverAndDate(@PathVariable Long driverId, @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(tripService.getTripsByDriverAndDate(driverId, date));
    }

    /**
     * Список рейсов (JHipster UI: {@code GET /api/trips?sort=id,asc}).
     */
    @GetMapping("")
    public List<Trip> getAllTrips(@RequestParam(name = "sort", required = false) String sort) {
        LOG.debug("REST request to get all Trips, sort={}", sort);
        return tripService.findAllTrips(sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable Long id) {
        LOG.debug("REST request to get Trip : {}", id);
        return ResponseUtil.wrapOrNotFound(tripService.findTrip(id));
    }

    @PostMapping("")
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip trip) throws URISyntaxException {
        LOG.debug("REST request to save Trip : {}", trip);
        if (trip.getId() != null) {
            throw new BadRequestAlertException("Новый рейс не должен содержать ID", ENTITY_NAME, ErrorConstants.ERR_VALIDATION);
        }
        Trip result = tripService.createTrip(trip);
        return ResponseEntity.created(new URI("/api/trips/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Trip trip
    ) {
        LOG.debug("REST request to update Trip : {}, {}", id, trip);
        if (trip.getId() == null) {
            throw new BadRequestAlertException("Некорректный id", ENTITY_NAME, ErrorConstants.ERR_VALIDATION);
        }
        if (!Objects.equals(id, trip.getId())) {
            throw new BadRequestAlertException("ID в URL и в теле не совпадают", ENTITY_NAME, ErrorConstants.ERR_VALIDATION);
        }
        Trip result = tripService.updateTrip(id, trip);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Trip> partialUpdateTrip(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Trip trip
    ) {
        LOG.debug("REST request to partial update Trip : {}, {}", id, trip);
        if (trip.getId() == null) {
            throw new BadRequestAlertException("Некорректный id", ENTITY_NAME, ErrorConstants.ERR_VALIDATION);
        }
        if (!Objects.equals(id, trip.getId())) {
            throw new BadRequestAlertException("ID в URL и в теле не совпадают", ENTITY_NAME, ErrorConstants.ERR_VALIDATION);
        }
        Optional<Trip> result = tripService.partialUpdateTrip(id, trip);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, trip.getId().toString())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        LOG.debug("REST request to delete Trip : {}", id);
        tripService.deleteTrip(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
