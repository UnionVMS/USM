package eu.europa.ec.mare.usm.information.rest.service;

import org.junit.Test;

/**
 * Load-test for the InformationService
 */
public class InformationServiceLoadIT extends InformationRestServiceIT {

    private final int iterations = 32;

    /**
     * Tests the getUserContext method.
     */
    @Test
    public void loadTestGetUserContextWithPreferences() {
        for (int i = 0; i < iterations; i++) {
            getUserContextWithPreferencesTest();
        }
    }

    /**
     * Tests the getUserContext method.
     */
    @Test
    public void loadTestGetUserContext() {
        for (int i = 0; i < iterations; i++) {
            getUserContextTest();
        }
    }

    /**
     * Tests the updateUserContext method.
     */
    @Test
    public void loadTestUpdateUserContextWithPreferences() {
        for (int i = 0; i < iterations; i++) {
            updateUserContextWithPreferencesTest();
        }
    }

    /**
     * Tests the updateUserContext method.
     */
    @Test
    public void loadTestUpdateUserContextWithoutPreferences() {
        for (int i = 0; i < iterations; i++) {
            updateUserContextWithoutPreferencesTest();
        }
    }

    /**
     * Tests the getContactDetails method.
     */
    @Test
    public void loadTestGetContactDetails() {
        for (int i = 0; i < iterations; i++) {
            getContactDetailsTest();
        }
    }

    /**
     * Tests the findOrganisations method.
     */
    @Test
    public void loadTestFindOrganisations() {
        for (int i = 0; i < iterations; i++) {
            getOrganisationsTest();
        }
    }

    /**
     * Tests the getOrganisation method.
     */
    @Test
    public void loadTestGetOrganisationWithEndPoints() {
        for (int i = 0; i < iterations; i++) {
            getOrganisationWithEndPointsTest();
        }
    }

    /**
     * Tests the getOrganisation method.
     */
    @Test
    public void loadTestGetOrganisationWithEndPointChannel() {
        for (int i = 0; i < iterations; i++) {
            getOrganisationWithEndPointChannelTest();
        }
    }

    /**
     * Tests the getOrganisation method.
     */
    @Test
    public void loadTestGetOrganisationWithoutEndPoints() {
        for (int i = 0; i < iterations; i++) {
            getOrganisationWithoutEndPointsTest();
        }
    }
}

