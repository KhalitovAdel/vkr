package com.transport.system.web.rest;

import static com.transport.system.domain.StopAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transport.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.Stop;
import com.transport.system.repository.StopRepository;
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
 * Integration tests for the {@link StopResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StopResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/stops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStopMockMvc;

    private Stop stop;

    private Stop insertedStop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stop createEntity() {
        return new Stop().name(DEFAULT_NAME).latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stop createUpdatedEntity() {
        return new Stop().name(UPDATED_NAME).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
    }

    @BeforeEach
    void initTest() {
        stop = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStop != null) {
            stopRepository.delete(insertedStop);
            insertedStop = null;
        }
    }

    @Test
    @Transactional
    void createStop() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Stop
        var returnedStop = om.readValue(
            restStopMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stop)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Stop.class
        );

        // Validate the Stop in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStopUpdatableFieldsEquals(returnedStop, getPersistedStop(returnedStop));

        insertedStop = returnedStop;
    }

    @Test
    @Transactional
    void createStopWithExistingId() throws Exception {
        // Create the Stop with an existing ID
        stop.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stop)))
            .andExpect(status().isBadRequest());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stop.setName(null);

        // Create the Stop, which fails.

        restStopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stop)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStops() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        // Get all the stopList
        restStopMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stop.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))));
    }

    @Test
    @Transactional
    void getStop() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        // Get the stop
        restStopMockMvc
            .perform(get(ENTITY_API_URL_ID, stop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stop.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)));
    }

    @Test
    @Transactional
    void getNonExistingStop() throws Exception {
        // Get the stop
        restStopMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStop() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stop
        Stop updatedStop = stopRepository.findById(stop.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStop are not directly saved in db
        em.detach(updatedStop);
        updatedStop.name(UPDATED_NAME).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStop))
            )
            .andExpect(status().isOk());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStopToMatchAllProperties(updatedStop);
    }

    @Test
    @Transactional
    void putNonExistingStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(put(ENTITY_API_URL_ID, stop.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stop)))
            .andExpect(status().isBadRequest());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStopWithPatch() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stop using partial update
        Stop partialUpdatedStop = new Stop();
        partialUpdatedStop.setId(stop.getId());

        restStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStop))
            )
            .andExpect(status().isOk());

        // Validate the Stop in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStopUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStop, stop), getPersistedStop(stop));
    }

    @Test
    @Transactional
    void fullUpdateStopWithPatch() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stop using partial update
        Stop partialUpdatedStop = new Stop();
        partialUpdatedStop.setId(stop.getId());

        partialUpdatedStop.name(UPDATED_NAME).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStop.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStop))
            )
            .andExpect(status().isOk());

        // Validate the Stop in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStopUpdatableFieldsEquals(partialUpdatedStop, getPersistedStop(partialUpdatedStop));
    }

    @Test
    @Transactional
    void patchNonExistingStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(patch(ENTITY_API_URL_ID, stop.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stop)))
            .andExpect(status().isBadRequest());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStop() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stop.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStopMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stop in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStop() throws Exception {
        // Initialize the database
        insertedStop = stopRepository.saveAndFlush(stop);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stop
        restStopMockMvc
            .perform(delete(ENTITY_API_URL_ID, stop.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stopRepository.count();
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

    protected Stop getPersistedStop(Stop stop) {
        return stopRepository.findById(stop.getId()).orElseThrow();
    }

    protected void assertPersistedStopToMatchAllProperties(Stop expectedStop) {
        assertStopAllPropertiesEquals(expectedStop, getPersistedStop(expectedStop));
    }

    protected void assertPersistedStopToMatchUpdatableProperties(Stop expectedStop) {
        assertStopAllUpdatablePropertiesEquals(expectedStop, getPersistedStop(expectedStop));
    }
}
