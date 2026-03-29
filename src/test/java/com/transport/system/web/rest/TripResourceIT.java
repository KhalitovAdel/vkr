package com.transport.system.web.rest;

import static com.transport.system.domain.TripAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.Trip;
import com.transport.system.domain.enumeration.TripStatus;
import com.transport.system.repository.TripRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
 * Integration tests for the {@link TripResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TripResourceIT {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final LocalTime DEFAULT_DEPARTURE_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_DEPARTURE_TIME = LocalTime.MAX.withNano(0);

    private static final LocalTime DEFAULT_ARRIVAL_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_ARRIVAL_TIME = LocalTime.MAX.withNano(0);

    private static final LocalDate DEFAULT_TRIP_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TRIP_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final TripStatus DEFAULT_TRIP_STATUS = TripStatus.SCHEDULED;
    private static final TripStatus UPDATED_TRIP_STATUS = TripStatus.ONGOING;

    private static final String ENTITY_API_URL = "/api/trips";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTripMockMvc;

    private Trip trip;

    private Trip insertedTrip;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trip createEntity() {
        return new Trip()
            .departureTime(DEFAULT_DEPARTURE_TIME)
            .arrivalTime(DEFAULT_ARRIVAL_TIME)
            .tripDate(DEFAULT_TRIP_DATE)
            .tripStatus(DEFAULT_TRIP_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trip createUpdatedEntity() {
        return new Trip()
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .tripDate(UPDATED_TRIP_DATE)
            .tripStatus(UPDATED_TRIP_STATUS);
    }

    @BeforeEach
    void initTest() {
        trip = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTrip != null) {
            tripRepository.delete(insertedTrip);
            insertedTrip = null;
        }
    }

    @Test
    @Transactional
    void createTrip() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Trip
        var returnedTrip = om.readValue(
            restTripMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Trip.class
        );

        // Validate the Trip in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTripUpdatableFieldsEquals(returnedTrip, getPersistedTrip(returnedTrip));

        insertedTrip = returnedTrip;
    }

    @Test
    @Transactional
    void createTripWithExistingId() throws Exception {
        // Create the Trip with an existing ID
        trip.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTripMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDepartureTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setDepartureTime(null);

        // Create the Trip, which fails.

        restTripMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArrivalTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setArrivalTime(null);

        // Create the Trip, which fails.

        restTripMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTripDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setTripDate(null);

        // Create the Trip, which fails.

        restTripMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTripStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trip.setTripStatus(null);

        // Create the Trip, which fails.

        restTripMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTrips() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get all the tripList
        restTripMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trip.getId().intValue())))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.format(LOCAL_DATE_TIME_FORMAT))))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.format(LOCAL_DATE_TIME_FORMAT))))
            .andExpect(jsonPath("$.[*].tripDate").value(hasItem(DEFAULT_TRIP_DATE.toString())))
            .andExpect(jsonPath("$.[*].tripStatus").value(hasItem(DEFAULT_TRIP_STATUS.toString())));
    }

    @Test
    @Transactional
    void getTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        // Get the trip
        restTripMockMvc
            .perform(get(ENTITY_API_URL_ID, trip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trip.getId().intValue()))
            .andExpect(jsonPath("$.departureTime").value(DEFAULT_DEPARTURE_TIME.format(LOCAL_DATE_TIME_FORMAT)))
            .andExpect(jsonPath("$.arrivalTime").value(DEFAULT_ARRIVAL_TIME.format(LOCAL_DATE_TIME_FORMAT)))
            .andExpect(jsonPath("$.tripDate").value(DEFAULT_TRIP_DATE.toString()))
            .andExpect(jsonPath("$.tripStatus").value(DEFAULT_TRIP_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTrip() throws Exception {
        // Get the trip
        restTripMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrip are not directly saved in db
        em.detach(updatedTrip);
        updatedTrip
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .tripDate(UPDATED_TRIP_DATE)
            .tripStatus(UPDATED_TRIP_STATUS);

        restTripMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTrip.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTrip))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTripToMatchAllProperties(updatedTrip);
    }

    @Test
    @Transactional
    void putNonExistingTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(put(ENTITY_API_URL_ID, trip.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trip)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTripWithPatch() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip using partial update
        Trip partialUpdatedTrip = new Trip();
        partialUpdatedTrip.setId(trip.getId());

        partialUpdatedTrip.departureTime(UPDATED_DEPARTURE_TIME).arrivalTime(UPDATED_ARRIVAL_TIME).tripDate(UPDATED_TRIP_DATE);

        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrip.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrip))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrip, trip), getPersistedTrip(trip));
    }

    @Test
    @Transactional
    void fullUpdateTripWithPatch() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trip using partial update
        Trip partialUpdatedTrip = new Trip();
        partialUpdatedTrip.setId(trip.getId());

        partialUpdatedTrip
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .tripDate(UPDATED_TRIP_DATE)
            .tripStatus(UPDATED_TRIP_STATUS);

        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrip.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrip))
            )
            .andExpect(status().isOk());

        // Validate the Trip in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTripUpdatableFieldsEquals(partialUpdatedTrip, getPersistedTrip(partialUpdatedTrip));
    }

    @Test
    @Transactional
    void patchNonExistingTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(patch(ENTITY_API_URL_ID, trip.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trip)))
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trip))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrip() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trip.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTripMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trip)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trip in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrip() throws Exception {
        // Initialize the database
        insertedTrip = tripRepository.saveAndFlush(trip);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trip
        restTripMockMvc
            .perform(delete(ENTITY_API_URL_ID, trip.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void suggestVehicleEndpointShouldBeReachable() throws Exception {
        var payload = Map.of("routeId", 999999L, "tripDate", "2026-03-26", "departureTime", "08:30:00");
        restTripMockMvc
            .perform(post("/api/trips/suggest-vehicle").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(payload)))
            .andExpect(status().isBadRequest());
    }

    protected long getRepositoryCount() {
        return tripRepository.count();
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

    protected Trip getPersistedTrip(Trip trip) {
        return tripRepository.findById(trip.getId()).orElseThrow();
    }

    protected void assertPersistedTripToMatchAllProperties(Trip expectedTrip) {
        assertTripAllPropertiesEquals(expectedTrip, getPersistedTrip(expectedTrip));
    }

    protected void assertPersistedTripToMatchUpdatableProperties(Trip expectedTrip) {
        assertTripAllUpdatablePropertiesEquals(expectedTrip, getPersistedTrip(expectedTrip));
    }
}
