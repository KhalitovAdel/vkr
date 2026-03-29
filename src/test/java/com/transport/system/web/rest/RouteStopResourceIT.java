package com.transport.system.web.rest;

import static com.transport.system.domain.RouteStopAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transport.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.RouteStop;
import com.transport.system.repository.RouteStopRepository;
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
 * Integration tests for the {@link RouteStopResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RouteStopResourceIT {

    private static final Integer DEFAULT_STOP_ORDER = 1;
    private static final Integer UPDATED_STOP_ORDER = 2;

    private static final BigDecimal DEFAULT_DISTANCE_FROM_PREV = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISTANCE_FROM_PREV = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/route-stops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRouteStopMockMvc;

    private RouteStop routeStop;

    private RouteStop insertedRouteStop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteStop createEntity() {
        return new RouteStop().stopOrder(DEFAULT_STOP_ORDER).distanceFromPrev(DEFAULT_DISTANCE_FROM_PREV);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RouteStop createUpdatedEntity() {
        return new RouteStop().stopOrder(UPDATED_STOP_ORDER).distanceFromPrev(UPDATED_DISTANCE_FROM_PREV);
    }

    @BeforeEach
    void initTest() {
        routeStop = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRouteStop != null) {
            routeStopRepository.delete(insertedRouteStop);
            insertedRouteStop = null;
        }
    }

    @Test
    @Transactional
    void createRouteStop() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RouteStop
        var returnedRouteStop = om.readValue(
            restRouteStopMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routeStop)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RouteStop.class
        );

        // Validate the RouteStop in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRouteStopUpdatableFieldsEquals(returnedRouteStop, getPersistedRouteStop(returnedRouteStop));

        insertedRouteStop = returnedRouteStop;
    }

    @Test
    @Transactional
    void createRouteStopWithExistingId() throws Exception {
        // Create the RouteStop with an existing ID
        routeStop.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRouteStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routeStop)))
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStopOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        routeStop.setStopOrder(null);

        // Create the RouteStop, which fails.

        restRouteStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routeStop)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRouteStops() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        // Get all the routeStopList
        restRouteStopMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(routeStop.getId().intValue())))
            .andExpect(jsonPath("$.[*].stopOrder").value(hasItem(DEFAULT_STOP_ORDER)))
            .andExpect(jsonPath("$.[*].distanceFromPrev").value(hasItem(sameNumber(DEFAULT_DISTANCE_FROM_PREV))));
    }

    @Test
    @Transactional
    void getRouteStop() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        // Get the routeStop
        restRouteStopMockMvc
            .perform(get(ENTITY_API_URL_ID, routeStop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(routeStop.getId().intValue()))
            .andExpect(jsonPath("$.stopOrder").value(DEFAULT_STOP_ORDER))
            .andExpect(jsonPath("$.distanceFromPrev").value(sameNumber(DEFAULT_DISTANCE_FROM_PREV)));
    }

    @Test
    @Transactional
    void getNonExistingRouteStop() throws Exception {
        // Get the routeStop
        restRouteStopMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRouteStop() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeStop
        RouteStop updatedRouteStop = routeStopRepository.findById(routeStop.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRouteStop are not directly saved in db
        em.detach(updatedRouteStop);
        updatedRouteStop.stopOrder(UPDATED_STOP_ORDER).distanceFromPrev(UPDATED_DISTANCE_FROM_PREV);

        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRouteStop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRouteStopToMatchAllProperties(updatedRouteStop);
    }

    @Test
    @Transactional
    void putNonExistingRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, routeStop.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(routeStop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRouteStopWithPatch() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeStop using partial update
        RouteStop partialUpdatedRouteStop = new RouteStop();
        partialUpdatedRouteStop.setId(routeStop.getId());

        partialUpdatedRouteStop.stopOrder(UPDATED_STOP_ORDER);

        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRouteStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteStopUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRouteStop, routeStop),
            getPersistedRouteStop(routeStop)
        );
    }

    @Test
    @Transactional
    void fullUpdateRouteStopWithPatch() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the routeStop using partial update
        RouteStop partialUpdatedRouteStop = new RouteStop();
        partialUpdatedRouteStop.setId(routeStop.getId());

        partialUpdatedRouteStop.stopOrder(UPDATED_STOP_ORDER).distanceFromPrev(UPDATED_DISTANCE_FROM_PREV);

        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRouteStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRouteStop))
            )
            .andExpect(status().isOk());

        // Validate the RouteStop in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRouteStopUpdatableFieldsEquals(partialUpdatedRouteStop, getPersistedRouteStop(partialUpdatedRouteStop));
    }

    @Test
    @Transactional
    void patchNonExistingRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, routeStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(routeStop))
            )
            .andExpect(status().isBadRequest());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRouteStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        routeStop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRouteStopMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(routeStop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RouteStop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRouteStop() throws Exception {
        // Initialize the database
        insertedRouteStop = routeStopRepository.saveAndFlush(routeStop);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the routeStop
        restRouteStopMockMvc
            .perform(delete(ENTITY_API_URL_ID, routeStop.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return routeStopRepository.count();
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

    protected RouteStop getPersistedRouteStop(RouteStop routeStop) {
        return routeStopRepository.findById(routeStop.getId()).orElseThrow();
    }

    protected void assertPersistedRouteStopToMatchAllProperties(RouteStop expectedRouteStop) {
        assertRouteStopAllPropertiesEquals(expectedRouteStop, getPersistedRouteStop(expectedRouteStop));
    }

    protected void assertPersistedRouteStopToMatchUpdatableProperties(RouteStop expectedRouteStop) {
        assertRouteStopAllUpdatablePropertiesEquals(expectedRouteStop, getPersistedRouteStop(expectedRouteStop));
    }
}
