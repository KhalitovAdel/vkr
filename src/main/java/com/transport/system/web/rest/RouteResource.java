package com.transport.system.web.rest;

import com.transport.system.domain.Route;
import com.transport.system.repository.RouteRepository;
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
 * REST controller for managing {@link com.transport.system.domain.Route}.
 */
@RestController
@RequestMapping("/api/routes")
@Transactional
public class RouteResource {

    private static final Logger LOG = LoggerFactory.getLogger(RouteResource.class);

    private static final String ENTITY_NAME = "route";

    @Value("${jhipster.clientApp.name:transportSystem}")
    private String applicationName;

    private final RouteRepository routeRepository;

    public RouteResource(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    /**
     * {@code POST  /routes} : Create a new route.
     *
     * @param route the route to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new route, or with status {@code 400 (Bad Request)} if the route has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Route> createRoute(@Valid @RequestBody Route route) throws URISyntaxException {
        LOG.debug("REST request to save Route : {}", route);
        if (route.getId() != null) {
            throw new BadRequestAlertException("A new route cannot already have an ID", ENTITY_NAME, "idexists");
        }
        route = routeRepository.save(route);
        return ResponseEntity.created(new URI("/api/routes/" + route.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, route.getId().toString()))
            .body(route);
    }

    /**
     * {@code PUT  /routes/:id} : Updates an existing route.
     *
     * @param id the id of the route to save.
     * @param route the route to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated route,
     * or with status {@code 400 (Bad Request)} if the route is not valid,
     * or with status {@code 500 (Internal Server Error)} if the route couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Route route)
        throws URISyntaxException {
        LOG.debug("REST request to update Route : {}, {}", id, route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, route.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        route = routeRepository.save(route);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, route.getId().toString()))
            .body(route);
    }

    /**
     * {@code PATCH  /routes/:id} : Partial updates given fields of an existing route, field will ignore if it is null
     *
     * @param id the id of the route to save.
     * @param route the route to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated route,
     * or with status {@code 400 (Bad Request)} if the route is not valid,
     * or with status {@code 404 (Not Found)} if the route is not found,
     * or with status {@code 500 (Internal Server Error)} if the route couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Route> partialUpdateRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Route route
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Route partially : {}, {}", id, route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, route.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Route> result = routeRepository
            .findById(route.getId())
            .map(existingRoute -> {
                updateIfPresent(existingRoute::setRouteNumber, route.getRouteNumber());
                updateIfPresent(existingRoute::setRouteName, route.getRouteName());
                updateIfPresent(existingRoute::setLength, route.getLength());
                updateIfPresent(existingRoute::setRouteType, route.getRouteType());

                return existingRoute;
            })
            .map(routeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, route.getId().toString())
        );
    }

    /**
     * {@code GET  /routes} : get all the Routes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Routes in body.
     */
    @GetMapping("")
    public List<Route> getAllRoutes() {
        LOG.debug("REST request to get all Routes");
        return routeRepository.findAll();
    }

    /**
     * {@code GET  /routes/:id} : get the "id" route.
     *
     * @param id the id of the route to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the route, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Route : {}", id);
        Optional<Route> route = routeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(route);
    }

    /**
     * {@code DELETE  /routes/:id} : delete the "id" route.
     *
     * @param id the id of the route to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Route : {}", id);
        routeRepository.deleteById(id);
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
