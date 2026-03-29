package com.transport.system.web.rest;

import static com.transport.system.domain.WaybillAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.transport.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.Waybill;
import com.transport.system.repository.WaybillRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link WaybillResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WaybillResourceIT {

    private static final String DEFAULT_DOCUMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_ACTUAL_DEPARTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_DEPARTURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_RETURN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_RETURN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_MILEAGE_START = new BigDecimal(1);
    private static final BigDecimal UPDATED_MILEAGE_START = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MILEAGE_END = new BigDecimal(1);
    private static final BigDecimal UPDATED_MILEAGE_END = new BigDecimal(2);

    private static final BigDecimal DEFAULT_FUEL_CONSUMPTION_PLAN = new BigDecimal(1);
    private static final BigDecimal UPDATED_FUEL_CONSUMPTION_PLAN = new BigDecimal(2);

    private static final BigDecimal DEFAULT_FUEL_CONSUMPTION_FACT = new BigDecimal(1);
    private static final BigDecimal UPDATED_FUEL_CONSUMPTION_FACT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/waybills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WaybillRepository waybillRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWaybillMockMvc;

    private Waybill waybill;

    private Waybill insertedWaybill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Waybill createEntity() {
        return new Waybill()
            .documentNumber(DEFAULT_DOCUMENT_NUMBER)
            .actualDeparture(DEFAULT_ACTUAL_DEPARTURE)
            .actualReturn(DEFAULT_ACTUAL_RETURN)
            .mileageStart(DEFAULT_MILEAGE_START)
            .mileageEnd(DEFAULT_MILEAGE_END)
            .fuelConsumptionPlan(DEFAULT_FUEL_CONSUMPTION_PLAN)
            .fuelConsumptionFact(DEFAULT_FUEL_CONSUMPTION_FACT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Waybill createUpdatedEntity() {
        return new Waybill()
            .documentNumber(UPDATED_DOCUMENT_NUMBER)
            .actualDeparture(UPDATED_ACTUAL_DEPARTURE)
            .actualReturn(UPDATED_ACTUAL_RETURN)
            .mileageStart(UPDATED_MILEAGE_START)
            .mileageEnd(UPDATED_MILEAGE_END)
            .fuelConsumptionPlan(UPDATED_FUEL_CONSUMPTION_PLAN)
            .fuelConsumptionFact(UPDATED_FUEL_CONSUMPTION_FACT);
    }

    @BeforeEach
    void initTest() {
        waybill = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWaybill != null) {
            waybillRepository.delete(insertedWaybill);
            insertedWaybill = null;
        }
    }

    @Test
    @Transactional
    void createWaybill() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Waybill
        var returnedWaybill = om.readValue(
            restWaybillMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waybill)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Waybill.class
        );

        // Validate the Waybill in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWaybillUpdatableFieldsEquals(returnedWaybill, getPersistedWaybill(returnedWaybill));

        insertedWaybill = returnedWaybill;
    }

    @Test
    @Transactional
    void createWaybillWithExistingId() throws Exception {
        // Create the Waybill with an existing ID
        waybill.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWaybillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waybill)))
            .andExpect(status().isBadRequest());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDocumentNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        waybill.setDocumentNumber(null);

        // Create the Waybill, which fails.

        restWaybillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waybill)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWaybills() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        // Get all the waybillList
        restWaybillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waybill.getId().intValue())))
            .andExpect(jsonPath("$.[*].documentNumber").value(hasItem(DEFAULT_DOCUMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].actualDeparture").value(hasItem(DEFAULT_ACTUAL_DEPARTURE.toString())))
            .andExpect(jsonPath("$.[*].actualReturn").value(hasItem(DEFAULT_ACTUAL_RETURN.toString())))
            .andExpect(jsonPath("$.[*].mileageStart").value(hasItem(sameNumber(DEFAULT_MILEAGE_START))))
            .andExpect(jsonPath("$.[*].mileageEnd").value(hasItem(sameNumber(DEFAULT_MILEAGE_END))))
            .andExpect(jsonPath("$.[*].fuelConsumptionPlan").value(hasItem(sameNumber(DEFAULT_FUEL_CONSUMPTION_PLAN))))
            .andExpect(jsonPath("$.[*].fuelConsumptionFact").value(hasItem(sameNumber(DEFAULT_FUEL_CONSUMPTION_FACT))));
    }

    @Test
    @Transactional
    void getWaybill() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        // Get the waybill
        restWaybillMockMvc
            .perform(get(ENTITY_API_URL_ID, waybill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(waybill.getId().intValue()))
            .andExpect(jsonPath("$.documentNumber").value(DEFAULT_DOCUMENT_NUMBER))
            .andExpect(jsonPath("$.actualDeparture").value(DEFAULT_ACTUAL_DEPARTURE.toString()))
            .andExpect(jsonPath("$.actualReturn").value(DEFAULT_ACTUAL_RETURN.toString()))
            .andExpect(jsonPath("$.mileageStart").value(sameNumber(DEFAULT_MILEAGE_START)))
            .andExpect(jsonPath("$.mileageEnd").value(sameNumber(DEFAULT_MILEAGE_END)))
            .andExpect(jsonPath("$.fuelConsumptionPlan").value(sameNumber(DEFAULT_FUEL_CONSUMPTION_PLAN)))
            .andExpect(jsonPath("$.fuelConsumptionFact").value(sameNumber(DEFAULT_FUEL_CONSUMPTION_FACT)));
    }

    @Test
    @Transactional
    void getNonExistingWaybill() throws Exception {
        // Get the waybill
        restWaybillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWaybill() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waybill
        Waybill updatedWaybill = waybillRepository.findById(waybill.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWaybill are not directly saved in db
        em.detach(updatedWaybill);
        updatedWaybill
            .documentNumber(UPDATED_DOCUMENT_NUMBER)
            .actualDeparture(UPDATED_ACTUAL_DEPARTURE)
            .actualReturn(UPDATED_ACTUAL_RETURN)
            .mileageStart(UPDATED_MILEAGE_START)
            .mileageEnd(UPDATED_MILEAGE_END)
            .fuelConsumptionPlan(UPDATED_FUEL_CONSUMPTION_PLAN)
            .fuelConsumptionFact(UPDATED_FUEL_CONSUMPTION_FACT);

        restWaybillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWaybill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWaybill))
            )
            .andExpect(status().isOk());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWaybillToMatchAllProperties(updatedWaybill);
    }

    @Test
    @Transactional
    void putNonExistingWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(put(ENTITY_API_URL_ID, waybill.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waybill)))
            .andExpect(status().isBadRequest());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(waybill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(waybill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWaybillWithPatch() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waybill using partial update
        Waybill partialUpdatedWaybill = new Waybill();
        partialUpdatedWaybill.setId(waybill.getId());

        partialUpdatedWaybill
            .actualDeparture(UPDATED_ACTUAL_DEPARTURE)
            .actualReturn(UPDATED_ACTUAL_RETURN)
            .mileageStart(UPDATED_MILEAGE_START)
            .fuelConsumptionPlan(UPDATED_FUEL_CONSUMPTION_PLAN);

        restWaybillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaybill.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaybill))
            )
            .andExpect(status().isOk());

        // Validate the Waybill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaybillUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWaybill, waybill), getPersistedWaybill(waybill));
    }

    @Test
    @Transactional
    void fullUpdateWaybillWithPatch() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the waybill using partial update
        Waybill partialUpdatedWaybill = new Waybill();
        partialUpdatedWaybill.setId(waybill.getId());

        partialUpdatedWaybill
            .documentNumber(UPDATED_DOCUMENT_NUMBER)
            .actualDeparture(UPDATED_ACTUAL_DEPARTURE)
            .actualReturn(UPDATED_ACTUAL_RETURN)
            .mileageStart(UPDATED_MILEAGE_START)
            .mileageEnd(UPDATED_MILEAGE_END)
            .fuelConsumptionPlan(UPDATED_FUEL_CONSUMPTION_PLAN)
            .fuelConsumptionFact(UPDATED_FUEL_CONSUMPTION_FACT);

        restWaybillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWaybill.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWaybill))
            )
            .andExpect(status().isOk());

        // Validate the Waybill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWaybillUpdatableFieldsEquals(partialUpdatedWaybill, getPersistedWaybill(partialUpdatedWaybill));
    }

    @Test
    @Transactional
    void patchNonExistingWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, waybill.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(waybill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(waybill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWaybill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        waybill.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWaybillMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(waybill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Waybill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWaybill() throws Exception {
        // Initialize the database
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the waybill
        restWaybillMockMvc
            .perform(delete(ENTITY_API_URL_ID, waybill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void registerDepartureAndReturnShouldWork() throws Exception {
        insertedWaybill = waybillRepository.saveAndFlush(waybill);

        var departure = Map.of("eventTime", Instant.now().truncatedTo(ChronoUnit.MILLIS).toString(), "mileage", 1000);
        restWaybillMockMvc
            .perform(
                post("/api/waybills/{id}/departure", insertedWaybill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(departure))
            )
            .andExpect(status().isOk());

        var ret = Map.of("eventTime", Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS).toString(), "mileage", 1100);
        restWaybillMockMvc
            .perform(
                post("/api/waybills/{id}/return", insertedWaybill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ret))
            )
            .andExpect(status().isOk());
    }

    protected long getRepositoryCount() {
        return waybillRepository.count();
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

    protected Waybill getPersistedWaybill(Waybill waybill) {
        return waybillRepository.findById(waybill.getId()).orElseThrow();
    }

    protected void assertPersistedWaybillToMatchAllProperties(Waybill expectedWaybill) {
        assertWaybillAllPropertiesEquals(expectedWaybill, getPersistedWaybill(expectedWaybill));
    }

    protected void assertPersistedWaybillToMatchUpdatableProperties(Waybill expectedWaybill) {
        assertWaybillAllUpdatablePropertiesEquals(expectedWaybill, getPersistedWaybill(expectedWaybill));
    }
}
