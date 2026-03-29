package com.transport.system.web.rest;

import static com.transport.system.domain.DriverAsserts.*;
import static com.transport.system.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.system.IntegrationTest;
import com.transport.system.domain.Driver;
import com.transport.system.repository.DriverRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link DriverResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DriverResourceIT {

    private static final String DEFAULT_EMPLOYEE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_CATEGORY = "BBBBBBBBBB";

    private static final Integer DEFAULT_EXPERIENCE = 1;
    private static final Integer UPDATED_EXPERIENCE = 2;

    private static final LocalDate DEFAULT_HIRE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HIRE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/drivers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDriverMockMvc;

    private Driver driver;

    private Driver insertedDriver;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createEntity() {
        return new Driver()
            .employeeNumber(DEFAULT_EMPLOYEE_NUMBER)
            .fullName(DEFAULT_FULL_NAME)
            .licenseCategory(DEFAULT_LICENSE_CATEGORY)
            .experience(DEFAULT_EXPERIENCE)
            .hireDate(DEFAULT_HIRE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createUpdatedEntity() {
        return new Driver()
            .employeeNumber(UPDATED_EMPLOYEE_NUMBER)
            .fullName(UPDATED_FULL_NAME)
            .licenseCategory(UPDATED_LICENSE_CATEGORY)
            .experience(UPDATED_EXPERIENCE)
            .hireDate(UPDATED_HIRE_DATE);
    }

    @BeforeEach
    void initTest() {
        driver = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDriver != null) {
            driverRepository.delete(insertedDriver);
            insertedDriver = null;
        }
    }

    @Test
    @Transactional
    void createDriver() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Driver
        var returnedDriver = om.readValue(
            restDriverMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Driver.class
        );

        // Validate the Driver in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDriverUpdatableFieldsEquals(returnedDriver, getPersistedDriver(returnedDriver));

        insertedDriver = returnedDriver;
    }

    @Test
    @Transactional
    void createDriverWithExistingId() throws Exception {
        // Create the Driver with an existing ID
        driver.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmployeeNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setEmployeeNumber(null);

        // Create the Driver, which fails.

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setFullName(null);

        // Create the Driver, which fails.

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLicenseCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setLicenseCategory(null);

        // Create the Driver, which fails.

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExperienceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setExperience(null);

        // Create the Driver, which fails.

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHireDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setHireDate(null);

        // Create the Driver, which fails.

        restDriverMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDrivers() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        // Get all the driverList
        restDriverMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
            .andExpect(jsonPath("$.[*].employeeNumber").value(hasItem(DEFAULT_EMPLOYEE_NUMBER)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].licenseCategory").value(hasItem(DEFAULT_LICENSE_CATEGORY)))
            .andExpect(jsonPath("$.[*].experience").value(hasItem(DEFAULT_EXPERIENCE)))
            .andExpect(jsonPath("$.[*].hireDate").value(hasItem(DEFAULT_HIRE_DATE.toString())));
    }

    @Test
    @Transactional
    void getDriver() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        // Get the driver
        restDriverMockMvc
            .perform(get(ENTITY_API_URL_ID, driver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(driver.getId().intValue()))
            .andExpect(jsonPath("$.employeeNumber").value(DEFAULT_EMPLOYEE_NUMBER))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.licenseCategory").value(DEFAULT_LICENSE_CATEGORY))
            .andExpect(jsonPath("$.experience").value(DEFAULT_EXPERIENCE))
            .andExpect(jsonPath("$.hireDate").value(DEFAULT_HIRE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDriver() throws Exception {
        // Get the driver
        restDriverMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDriver() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver
        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDriver are not directly saved in db
        em.detach(updatedDriver);
        updatedDriver
            .employeeNumber(UPDATED_EMPLOYEE_NUMBER)
            .fullName(UPDATED_FULL_NAME)
            .licenseCategory(UPDATED_LICENSE_CATEGORY)
            .experience(UPDATED_EXPERIENCE)
            .hireDate(UPDATED_HIRE_DATE);

        restDriverMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDriver.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDriver))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDriverToMatchAllProperties(updatedDriver);
    }

    @Test
    @Transactional
    void putNonExistingDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(put(ENTITY_API_URL_ID, driver.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(driver))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(driver)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        partialUpdatedDriver.fullName(UPDATED_FULL_NAME);

        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDriver))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDriverUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDriver, driver), getPersistedDriver(driver));
    }

    @Test
    @Transactional
    void fullUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        partialUpdatedDriver
            .employeeNumber(UPDATED_EMPLOYEE_NUMBER)
            .fullName(UPDATED_FULL_NAME)
            .licenseCategory(UPDATED_LICENSE_CATEGORY)
            .experience(UPDATED_EXPERIENCE)
            .hireDate(UPDATED_HIRE_DATE);

        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDriver))
            )
            .andExpect(status().isOk());

        // Validate the Driver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDriverUpdatableFieldsEquals(partialUpdatedDriver, getPersistedDriver(partialUpdatedDriver));
    }

    @Test
    @Transactional
    void patchNonExistingDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, driver.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(driver))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(driver))
            )
            .andExpect(status().isBadRequest());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDriverMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(driver)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDriver() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.saveAndFlush(driver);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the driver
        restDriverMockMvc
            .perform(delete(ENTITY_API_URL_ID, driver.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return driverRepository.count();
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

    protected Driver getPersistedDriver(Driver driver) {
        return driverRepository.findById(driver.getId()).orElseThrow();
    }

    protected void assertPersistedDriverToMatchAllProperties(Driver expectedDriver) {
        assertDriverAllPropertiesEquals(expectedDriver, getPersistedDriver(expectedDriver));
    }

    protected void assertPersistedDriverToMatchUpdatableProperties(Driver expectedDriver) {
        assertDriverAllUpdatablePropertiesEquals(expectedDriver, getPersistedDriver(expectedDriver));
    }
}
