package com.transport.system.web.rest;

import static com.transport.system.domain.RouteAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transport.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.Route;
import com.transport.system.domain.enumeration.RouteType;
import com.transport.system.repository.RouteRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RouteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RouteResourceIT {

    private static final String DEFAULT_ROUTE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ROUTE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LENGTH = new BigDecimal(1);
    private static final BigDecimal UPDATED_LENGTH = new BigDecimal(2);

    private static final RouteType DEFAULT_ROUTE_TYPE = RouteType.CITY;
    private static final RouteType UPDATED_ROUTE_TYPE = RouteType.SUBURB;

    private static final String ENTITY_API_URL = "/api/routes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRouteMockMvc;

    private Route route;

    private Route insertedRoute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Route createEntity() {
        return new Route()
            .routeNumber(DEFAULT_ROUTE_NUMBER)
            .routeName(DEFAULT_ROUTE_NAME)
            .length(DEFAULT_LENGTH)
            .routeType(DEFAULT_ROUTE_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Route createUpdatedEntity() {
        return new Route()
            .routeNumber(UPDATED_ROUTE_NUMBER)
            .routeName(UPDATED_ROUTE_NAME)
            .length(UPDATED_LENGTH)
            .routeType(UPDATED_ROUTE_TYPE);
    }

    @BeforeEach
    void initTest() {
        route = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRoute != null) {
            routeRepository.delete(insertedRoute);
            insertedRoute = null;
        }
    }

    @Test
    @Transactional
    void createRoute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Route
        var returnedRoute = om.readValue(
            restRouteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Route.class
        );

        // Validate the Route in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRouteUpdatableFieldsEquals(returnedRoute, getPersistedRoute(returnedRoute));

        insertedRoute = returnedRoute;
    }

    @Test
    @Transactional
    void createRouteWithExistingId() throws Exception {
        // Create the Route with an existing ID
        route.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRouteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRouteNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setRouteNumber(null);

        // Create the Route, which fails.

        restRouteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRouteNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setRouteName(null);

        // Create the Route, which fails.

        restRouteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLengthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setLength(null);

        // Create the Route, which fails.

        restRouteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRouteTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        route.setRouteType(null);

        // Create the Route, which fails.

        restRouteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoutes() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        // Get all the routeList
        restRouteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(route.getId().intValue())))
            .andExpect(jsonPath("$.[*].routeNumber").value(hasItem(DEFAULT_ROUTE_NUMBER)))
            .andExpect(jsonPath("$.[*].routeName").value(hasItem(DEFAULT_ROUTE_NAME)))
            .andExpect(jsonPath("$.[*].length").value(hasItem(sameNumber(DEFAULT_LENGTH))))
            .andExpect(jsonPath("$.[*].routeType").value(hasItem(DEFAULT_ROUTE_TYPE.toString())));
    }

    @Test
    @Transactional
    void getRoute() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        // Get the route
        restRouteMockMvc
            .perform(get(ENTITY_API_URL_ID, route.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(route.getId().intValue()))
            .andExpect(jsonPath("$.routeNumber").value(DEFAULT_ROUTE_NUMBER))
            .andExpect(jsonPath("$.routeName").value(DEFAULT_ROUTE_NAME))
            .andExpect(jsonPath("$.length").value(sameNumber(DEFAULT_LENGTH)))
            .andExpect(jsonPath("$.routeType").value(DEFAULT_ROUTE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRoute() throws Exception {
        // Get the route
        restRouteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoute() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route
        Route updatedRoute = routeRepository.findById(route.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRoute are not directly saved in db
        em.detach(updatedRoute);
        updatedRoute.routeNumber(UPDATED_ROUTE_NUMBER).routeName(UPDATED_ROUTE_NAME).length(UPDATED_LENGTH).routeType(UPDATED_ROUTE_TYPE);

        restRouteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoute.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRoute))
            )
            .andExpect(status().isOk());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRouteToMatchAllProperties(updatedRoute);
    }

    @Test
    @Transactional
    void putNonExistingRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(put(ENTITY_API_URL_ID, route.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(route))
            )
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(route)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRouteWithPatch() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route using partial update
        Route partialUpdatedRoute = new Route();
        partialUpdatedRoute.setId(route.getId());

        partialUpdatedRoute.routeName(UPDATED_ROUTE_NAME).routeType(UPDATED_ROUTE_TYPE);

        restRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoute))
            )
            .andExpect(status().isOk());

        // Validate the Route in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRoute, route), getPersistedRoute(route));
    }

    @Test
    @Transactional
    void fullUpdateRouteWithPatch() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the route using partial update
        Route partialUpdatedRoute = new Route();
        partialUpdatedRoute.setId(route.getId());

        partialUpdatedRoute
            .routeNumber(UPDATED_ROUTE_NUMBER)
            .routeName(UPDATED_ROUTE_NAME)
            .length(UPDATED_LENGTH)
            .routeType(UPDATED_ROUTE_TYPE);

        restRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoute))
            )
            .andExpect(status().isOk());

        // Validate the Route in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteUpdatableFieldsEquals(partialUpdatedRoute, getPersistedRoute(partialUpdatedRoute));
    }

    @Test
    @Transactional
    void patchNonExistingRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, route.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(route))
            )
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(route))
            )
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        route.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(route)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Route in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoute() throws Exception {
        // Initialize the database
        insertedRoute = routeRepository.saveAndFlush(route);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the route
        restRouteMockMvc
            .perform(delete(ENTITY_API_URL_ID, route.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return routeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Route getPersistedRoute(Route route) {
        return routeRepository.findById(route.getId()).orElseThrow();
    }

    protected void assertPersistedRouteToMatchAllProperties(Route expectedRoute) {
        assertRouteAllPropertiesEquals(expectedRoute, getPersistedRoute(expectedRoute));
    }

    protected void assertPersistedRouteToMatchUpdatableProperties(Route expectedRoute) {
        assertRouteAllUpdatablePropertiesEquals(expectedRoute, getPersistedRoute(expectedRoute));
    }
}
