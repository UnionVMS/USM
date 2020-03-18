package eu.europa.ec.mare.usm.administration.service.userPreference;

import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.Preference;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class UserPreferenceServiceTest extends DeploymentFactory {

    private static final String USM_ADMIN = "usm_admin";

    @EJB
    private UserPreferenceService testSubject;

    /**
     * Tests the get user preferences method providing only user name in order to get
     * all user's preferences
     */
    @Test
    public void testGetUserPreferencesProvidingName() {
        // Setup
        FindUserPreferenceQuery query = new FindUserPreferenceQuery();
        query.setUserName("vms_super_fra");
        ServiceRequest<FindUserPreferenceQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester(USM_ADMIN);
        sRequest.setBody(query);
        String expected = "fr_FR";

        // Execute
        UserPreferenceResponse response = testSubject.getUserPrefernces(sRequest);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'option' value", expected, getOptionValue(response.getResults(), expected));
    }

    /**
     * Tests the get user preferences method providing user and group name in order to get
     * group filtered user's preferences
     */
    @Test
    public void testGetUserPreferencesProvidingNameAndGroup() {
        // Setup
        FindUserPreferenceQuery query = new FindUserPreferenceQuery();
        query.setUserName("vms_super_fra");
        query.setGroupName("Union-VMS");
        ServiceRequest<FindUserPreferenceQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester(USM_ADMIN);
        sRequest.setBody(query);
        String expected = "fr_FR";
        String expectedGroup = "Union-VMS";

        // Execute
        UserPreferenceResponse response = testSubject.getUserPrefernces(sRequest);

        List<Preference> results = response.getResults();

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'option' value", expected, getOptionValue(results, expected));
        assertEquals("Unexpected 'group name' value", expectedGroup, getGroupName(results, expectedGroup));
    }

    private String getOptionValue(List<Preference> userPreferences, String expected) {
        for (Preference userPreference : userPreferences) {
            if ((new String(userPreference.getOptionValue())).equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private String getGroupName(List<Preference> userPreferences, String expected) {
        if (userPreferences.size() > 0) {
            // check only the first value as all elements should have the same group name value
            if (userPreferences.get(0).getGroupName().equals(expected)) {
                return expected;
            } else {
                return null;
            }
        }
        return null;
    }

}
