package com.transport.system.web.rest;

import com.transport.system.domain.Stop;
import com.transport.system.repository.StopRepository;
import com.transport.system.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.transport.system.domain.Stop}.
 */
@RestController
@RequestMapping("/api/stops")
@Transactional
public class StopResource {

    private static final Logger LOG = LoggerFactory.getLogger(StopResource.class);

    private static final String ENTITY_NAME = "stop";

    @Value("${jhipster.clientApp.name:transportSystem}")
    private String applicationName;

    private final StopRepository stopRepository;

    public StopResource(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    /**
     * {@code POST  /stops} : Create a new stop.
     *
     * @param stop the stop to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stop, or with status {@code 400 (Bad Request)} if the stop has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Stop> createStop(@Valid @RequestBody Stop stop) throws URISyntaxException {
        LOG.debug("REST request to save Stop : {}", stop);
        if (stop.getId() != null) {
            throw new BadRequestAlertException("A new stop cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stop = stopRepository.save(stop);
        return ResponseEntity.created(new URI("/api/stops/" + stop.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, stop.getId().toString()))
            .body(stop);
    }

    /**
     * {@code PUT  /stops/:id} : Updates an existing stop.
     *
     * @param id the id of the stop to save.
     * @param stop the stop to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stop,
     * or with status {@code 400 (Bad Request)} if the stop is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stop couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Stop> updateStop(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Stop stop)
        throws URISyntaxException {
        LOG.debug("REST request to update Stop : {}, {}", id, stop);
        if (stop.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stop.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stop = stopRepository.save(stop);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stop.getId().toString()))
            .body(stop);
    }

    /**
     * {@code PATCH  /stops/:id} : Partial updates given fields of an existing stop, field will ignore if it is null
     *
     * @param id the id of the stop to save.
     * @param stop the stop to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stop,
     * or with status {@code 400 (Bad Request)} if the stop is not valid,
     * or with status {@code 404 (Not Found)} if the stop is not found,
     * or with status {@code 500 (Internal Server Error)} if the stop couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Stop> partialUpdateStop(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Stop stop
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Stop partially : {}, {}", id, stop);
        if (stop.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stop.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stopRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stop> result = stopRepository
            .findById(stop.getId())
            .map(existingStop -> {
                updateIfPresent(existingStop::setName, stop.getName());
                updateIfPresent(existingStop::setLatitude, stop.getLatitude());
                updateIfPresent(existingStop::setLongitude, stop.getLongitude());

                return existingStop;
            })
            .map(stopRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stop.getId().toString())
        );
    }

    /**
     * {@code GET  /stops} : get all the Stops.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Stops in body.
     */
    @GetMapping("")
    public List<Stop> getAllStops() {
        LOG.debug("REST request to get all Stops");
        return stopRepository.findAll();
    }

    /**
     * {@code GET  /stops/:id} : get the "id" stop.
     *
     * @param id the id of the stop to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stop, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStop(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Stop : {}", id);
        Optional<Stop> stop = stopRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stop);
    }

    /**
     * {@code DELETE  /stops/:id} : delete the "id" stop.
     *
     * @param id the id of the stop to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Stop : {}", id);
        stopRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
