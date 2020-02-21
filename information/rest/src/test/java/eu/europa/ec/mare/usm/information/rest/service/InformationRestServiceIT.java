package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.mare.usm.information.domain.*;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class InformationRestServiceIT extends BuildInformationRestDeployment {

    private static final String APPLICATION_NAME_QUOTA = "Quota";
    private static final String OPT_USER_LOCALE = "userLocale";
    private static final String OPT_ROWS_PER_PAGE = "rowsPerPage";
    private static final String OPT_LEVEL_CODE = "levelCode";
    private static final String QUOTA_USERNAME_GRC = "quota_usr_grc";
    private static final String QUOTA_USERNAME_FRA = "quota_usr_fra";

    @Test
    @OperateOnDeployment("normal")
    public void getUserContextTest() {
        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(APPLICATION_NAME_QUOTA);
        query.setUserName(QUOTA_USERNAME_FRA);

        Response response = getUserContext(query);
        UserContext userContext = response.readEntity(UserContext.class);

        assertEquals(query.getApplicationName(), userContext.getApplicationName());
        assertEquals(query.getUserName(), userContext.getUserName());
        assertNotNull(userContext.getContextSet());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getUserContextWithPreferencesTest() {
        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(APPLICATION_NAME_QUOTA);
        query.setUserName(QUOTA_USERNAME_FRA);

        Response response = getUserContext(query);
        UserContext userContext = response.readEntity(UserContext.class);

        String expectedLevelCodeValue = "FRA";
        String expectedRowPerPageValue = "10";
        String expectedRoleName = "MS Quota User";

        assertEquals(query.getApplicationName(), userContext.getApplicationName());
        assertEquals(query.getUserName(), userContext.getUserName());
        assertNotNull(userContext.getContextSet());
        assertNotNull(userContext.getContextSet().getContexts());

        boolean foundExpectedRole = false;

        for (Context context : userContext.getContextSet().getContexts()) {

            assertNotNull(context.getRole());
            assertNotNull(context.getRole().getRoleName());

            if (expectedRoleName.equals(context.getRole().getRoleName())) {
                foundExpectedRole = true;
                assertNotNull(context.getPreferences());
                assertNotNull(context.getPreferences().getPreferences());
                assertFalse(context.getPreferences().getPreferences().isEmpty());

                boolean foundLevelCode = false;
                boolean foundRowsPerPage = false;

                for (Preference preference : context.getPreferences().getPreferences()) {
                    if (OPT_LEVEL_CODE.equals(preference.getOptionName())) {
                        foundLevelCode = true;
                        assertEquals(expectedLevelCodeValue, preference.getOptionValue());
                    } else if (OPT_ROWS_PER_PAGE.equals(preference.getOptionName())) {
                        foundRowsPerPage = true;
                        assertEquals(expectedRowPerPageValue, preference.getOptionValue());
                    }
                }
                if (!foundLevelCode) {
                    fail("Expected 'levelCode' preference not found in UserContext");
                }
                if (!foundRowsPerPage) {
                    fail("Expected 'rowsPerPage' preference not found in UserContext");
                }
            }
        }
        if (!foundExpectedRole) {
            fail("Role " + expectedRoleName + " not found in UserContext");
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateUserContextWithPreferencesTest() {
        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(APPLICATION_NAME_QUOTA);
        query.setUserName(QUOTA_USERNAME_GRC);

        Response response = getUserContext(query);
        UserContext userContext = response.readEntity(UserContext.class);

        assertNotNull(userContext);
        assertNotNull(userContext.getContextSet());

        String expectedRoleName = "MS Quota User";
        boolean foundExpectedRole = false;

        for (Context context : userContext.getContextSet().getContexts()) {
            if (expectedRoleName.equals(context.getRole().getRoleName())) {
                foundExpectedRole = true;

                assertNotNull(context.getPreferences());
                assertNotNull(context.getPreferences().getPreferences());

                for (Preference preference : context.getPreferences().getPreferences()) {
                    if (OPT_LEVEL_CODE.equals(preference.getOptionName())) {
                        preference.setOptionValue("*");
                    } else if (OPT_ROWS_PER_PAGE.equals(preference.getOptionName())) {
                        preference.setOptionValue("7");
                    } else if (OPT_USER_LOCALE.equals(preference.getOptionName())) {
                        preference.setOptionValue("el_GR");
                    }
                }
            }
        }
        if (!foundExpectedRole) {
            fail("Role " + expectedRoleName + " not found in UserContext");
        }

        Response updated = updateUserContext(userContext);
        assertEquals(Response.Status.OK.getStatusCode(), updated.getStatus());

        response = getUserContext(query);
        UserContext verify = response.readEntity(UserContext.class);

        assertEquals(query.getApplicationName(), verify.getApplicationName());
        assertEquals(query.getUserName(), verify.getUserName());

        foundExpectedRole = false;
        for (Context context : verify.getContextSet().getContexts()) {
            assertNotNull(context.getRole());
            assertNotNull(context.getRole().getRoleName());

            if (expectedRoleName.equals(context.getRole().getRoleName())) {
                foundExpectedRole = true;

                assertNotNull(context.getPreferences());
                assertNotNull(context.getPreferences().getPreferences());

                for (Preference item : context.getPreferences().getPreferences()) {
                    if (OPT_LEVEL_CODE.equals(item.getOptionName())) {
                        assertEquals("*", item.getOptionValue());
                    } else if (OPT_ROWS_PER_PAGE.equals(item.getOptionName())) {
                        assertEquals("7", item.getOptionValue());
                    } else if (OPT_USER_LOCALE.equals(item.getOptionName())) {
                        assertEquals("el_GR", item.getOptionValue());
                    }
                }
            }
        }
        if (!foundExpectedRole) {
            fail("Role " + expectedRoleName + " not found in UserContext");
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateUserContextWithoutPreferencesTest() {
        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(APPLICATION_NAME_QUOTA);
        query.setUserName(QUOTA_USERNAME_GRC);

        Response response = getUserContext(query);
        UserContext userContext = response.readEntity(UserContext.class);

        assertNotNull(userContext);
        assertNotNull(userContext.getContextSet());

        for (Context ctx : userContext.getContextSet().getContexts()) {
            ctx.setPreferences(null);
        }

        Response updated = updateUserContext(userContext);
        assertEquals(Response.Status.OK.getStatusCode(), updated.getStatus());

        response = getUserContext(query);
        UserContext verify = response.readEntity(UserContext.class);

        assertNotNull(verify);
        assertNotNull(verify.getContextSet());
        assertEquals(query.getApplicationName(), verify.getApplicationName());
        assertEquals(query.getUserName(), verify.getUserName());

        String expectedRoleName = "MS Quota User";

        boolean foundExpectedRole = false;
        for (Context context : verify.getContextSet().getContexts()) {

            assertNotNull(context.getRole());
            assertNotNull(context.getRole().getRoleName());

            if (expectedRoleName.equals(context.getRole().getRoleName())) {
                foundExpectedRole = true;
                assertNotNull(context.getPreferences());
                assertNotNull(context.getPreferences().getPreferences());
                for (Preference preference : context.getPreferences().getPreferences()) {
                    if (OPT_ROWS_PER_PAGE.equals(preference.getOptionName())) {
                        assertEquals("10", preference.getOptionValue());
                    } else if (OPT_USER_LOCALE.equals(preference.getOptionName())) {
                        assertEquals("en_GB", preference.getOptionValue());
                    }
                }
            }
        }
        if (!foundExpectedRole) {
            fail("Role " + expectedRoleName + " not found in UserContext");
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getContactDetailsTest() {
        Response response = getContactDetails(QUOTA_USERNAME_FRA);
        ContactDetails contactDetails = response.readEntity(ContactDetails.class);

        assertNotNull(contactDetails);
        assertEquals("quota_usr_fra@gouv.fr", contactDetails.geteMail());
        assertEquals("FRA", contactDetails.getOrganisationName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getContactDetailsNotFoundTest() {
        Response response = getContactDetails("anonymous");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getContactDetailsNoOrganisationTest() {
        Response response = getContactDetails("orphan");
        ContactDetails contactDetails = response.readEntity(ContactDetails.class);

        assertNotNull(contactDetails);
        assertEquals("orphan@mail.org", contactDetails.geteMail());
        assertNull(contactDetails.getOrganisationName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationsTest() {
        Response response = getOrganisations("EEC");
        List<Organisation> result = response.readEntity(new GenericType<>() {
        });

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationsNotFoundTest() {
        Response response = getOrganisations("dummy");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationWithEndPointsTest() {
        String organisationName = "FRA";
        Response response = getOrganisation(organisationName);
        Organisation result = response.readEntity(Organisation.class);

        assertNotNull(result);
        assertEquals(organisationName, result.getNation());
        assertTrue(result.isEnabled());
        assertNotNull(result.getEndPoints());
        assertFalse(result.getEndPoints().isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationWithEndPointChannelTest() {
        String organisationName = "GRC";
        Response response = getOrganisation(organisationName);
        Organisation result = response.readEntity(Organisation.class);

        assertNotNull(result);
        assertEquals(organisationName, result.getNation());
        assertTrue(result.isEnabled());
        assertNotNull(result.getEndPoints());
        assertFalse(result.getEndPoints().isEmpty());
        assertNotNull(result.getEndPoints().get(0).getChannels());

        for (EndPoint endPoint : result.getEndPoints()) {
            if ("FLUX.GRC_backup".equals(endPoint.getName())) {
                assertNotNull(endPoint.getChannels());
                assertFalse(endPoint.getChannels().isEmpty());
            }
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationWithoutEndPointsTest() {
        String organisationName = "DG-MARE";
        Response response = getOrganisation(organisationName);
        Organisation result = response.readEntity(Organisation.class);

        assertNotNull(result);
        assertEquals("EEC", result.getNation());
        assertTrue(result.isEnabled());
        assertNull(result.getEndPoints());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationWithParentTest() {
        String organisationName = "DG-MARE";
        Response response = getOrganisation(organisationName);
        Organisation result = response.readEntity(Organisation.class);

        assertNotNull(result);
        assertEquals("EC", result.getParentOrganisation());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getOrganisationWithChildrenTest() {
        String organisationName = "EC";
        Response response = getOrganisation(organisationName);
        Organisation result = response.readEntity(Organisation.class);

        assertNotNull(result);
        assertNotNull(result.getChildOrganisations());
        assertFalse(result.getChildOrganisations().isEmpty());

        boolean found = false;
        for (String item : result.getChildOrganisations()) {
            if ("DG-MARE".equals(item)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }


    private Response getUserContext(UserContextQuery query) {
        return getWebTargetInternal()
                .path("userContext")
                .path(query.getApplicationName())
                .path(query.getUserName())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
    }

    private Response updateUserContext(UserContext userContext) {
        return getWebTargetInternal()
                .path("userContext")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .put(Entity.json(userContext));
    }

    private Response getContactDetails(String username) {
        return getWebTargetInternal()
                .path("contactDetails")
                .path(username)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
    }

    private Response getOrganisations(String nation) {
        return getWebTargetInternal()
                .path("organisation")
                .path("nation")
                .path(nation)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
    }

    private Response getOrganisation(String organisationName) {
        return getWebTargetInternal()
                .path("organisation")
                .path(organisationName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
    }


}
