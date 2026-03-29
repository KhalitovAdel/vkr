package com.transport.system.web.rest;

import com.transport.system.domain.Waybill;
import com.transport.system.repository.WaybillRepository;
import com.transport.system.service.WaybillService;
import com.transport.system.service.dto.WaybillActionRequest;
import com.transport.system.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.transport.system.domain.Waybill}.
 */
@RestController
@RequestMapping("/api/waybills")
@Transactional
public class WaybillResource {

    private static final Logger LOG = LoggerFactory.getLogger(WaybillResource.class);

    private static final String ENTITY_NAME = "waybill";

    @Value("${jhipster.clientApp.name:transportSystem}")
    private String applicationName;

    private final WaybillRepository waybillRepository;
    private final WaybillService waybillService;

    public WaybillResource(WaybillRepository waybillRepository, WaybillService waybillService) {
        this.waybillRepository = waybillRepository;
        this.waybillService = waybillService;
    }

    /**
     * {@code POST  /waybills} : Create a new waybill.
     *
     * @param waybill the waybill to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new waybill, or with status {@code 400 (Bad Request)} if the waybill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Waybill> createWaybill(@Valid @RequestBody Waybill waybill) throws URISyntaxException {
        LOG.debug("REST request to save Waybill : {}", waybill);
        if (waybill.getId() != null) {
            throw new BadRequestAlertException("A new waybill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        waybill = waybillRepository.save(waybill);
        return ResponseEntity.created(new URI("/api/waybills/" + waybill.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, waybill.getId().toString()))
            .body(waybill);
    }

    /**
     * {@code PUT  /waybills/:id} : Updates an existing waybill.
     *
     * @param id the id of the waybill to save.
     * @param waybill the waybill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waybill,
     * or with status {@code 400 (Bad Request)} if the waybill is not valid,
     * or with status {@code 500 (Internal Server Error)} if the waybill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Waybill> updateWaybill(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Waybill waybill
    ) throws URISyntaxException {
        LOG.debug("REST request to update Waybill : {}, {}", id, waybill);
        if (waybill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waybill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waybillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        waybill = waybillRepository.save(waybill);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, waybill.getId().toString()))
            .body(waybill);
    }

    /**
     * {@code PATCH  /waybills/:id} : Partial updates given fields of an existing waybill, field will ignore if it is null
     *
     * @param id the id of the waybill to save.
     * @param waybill the waybill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated waybill,
     * or with status {@code 400 (Bad Request)} if the waybill is not valid,
     * or with status {@code 404 (Not Found)} if the waybill is not found,
     * or with status {@code 500 (Internal Server Error)} if the waybill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Waybill> partialUpdateWaybill(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Waybill waybill
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Waybill partially : {}, {}", id, waybill);
        if (waybill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, waybill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!waybillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Waybill> result = waybillRepository
            .findById(waybill.getId())
            .map(existingWaybill -> {
                updateIfPresent(existingWaybill::setDocumentNumber, waybill.getDocumentNumber());
                updateIfPresent(existingWaybill::setActualDeparture, waybill.getActualDeparture());
                updateIfPresent(existingWaybill::setActualReturn, waybill.getActualReturn());
                updateIfPresent(existingWaybill::setMileageStart, waybill.getMileageStart());
                updateIfPresent(existingWaybill::setMileageEnd, waybill.getMileageEnd());
                updateIfPresent(existingWaybill::setFuelConsumptionPlan, waybill.getFuelConsumptionPlan());
                updateIfPresent(existingWaybill::setFuelConsumptionFact, waybill.getFuelConsumptionFact());

                return existingWaybill;
            })
            .map(waybillRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, waybill.getId().toString())
        );
    }

    /**
     * {@code GET  /waybills} : get all the Waybills.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Waybills in body.
     */
    @GetMapping("")
    public List<Waybill> getAllWaybills(@RequestParam(name = "filter", required = false) String filter) {
        if ("trip-is-null".equals(filter)) {
            LOG.debug("REST request to get all Waybills where trip is null");
            return StreamSupport.stream(waybillRepository.findAll().spliterator(), false)
                .filter(waybill -> waybill.getTrip() == null)
                .toList();
        }
        LOG.debug("REST request to get all Waybills");
        return waybillRepository.findAll();
    }

    /**
     * {@code GET  /waybills/:id} : get the "id" waybill.
     *
     * @param id the id of the waybill to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the waybill, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Waybill> getWaybill(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Waybill : {}", id);
        Optional<Waybill> waybill = waybillRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(waybill);
    }

    /**
     * {@code DELETE  /waybills/:id} : delete the "id" waybill.
     *
     * @param id the id of the waybill to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaybill(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Waybill : {}", id);
        waybillRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/{id}/departure")
    public ResponseEntity<Waybill> registerDeparture(@PathVariable("id") Long id, @Valid @RequestBody WaybillActionRequest request) {
        return ResponseEntity.ok(waybillService.registerDeparture(id, request.getEventTime(), request.getMileage()));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Waybill> registerReturn(@PathVariable("id") Long id, @Valid @RequestBody WaybillActionRequest request) {
        return ResponseEntity.ok(waybillService.registerReturn(id, request.getEventTime(), request.getMileage()));
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
