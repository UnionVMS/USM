package eu.europa.ec.mare.usm.administration.rest.service.user;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserRestServiceIT extends BuildAdministrationDeployment {

    private static final String USER_NAME = "vms_user_fra";
    private static final String ORGANISATION_NAME = "FRA";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";

    private UserRestClient manageUserClient = null;

    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    //    remove
    private static final String usmAdmin = "usm_admin";

    @EJB
    private AdministrationRestClient restClient;

//    private AuthenticationRestClient authenticationClient;
//    private final String vms_admin_com;
//    private String usmAdmin;

//    public UserRestServiceIT()
//            throws IOException {
//        super(VMS_ADMIN_COM_USER);
//        vms_admin_com = getAuthToken();
//    }
//
//    @Before
//    public void setUp() {
//        manageUserClient = new UserRestClient(endPoint);
//        authenticationClient = new AuthenticationRestClient(endPoint);
//        usmAdmin = login("usm_admin", "password");
//    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindUsers() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.findUsers(auth.getJwtoken(),
                "0", "4", "user_name", "ASC", USER_NAME, ORGANISATION_NAME, null);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<UserAccount> pr = response.readEntity(new javax.ws.rs.core.GenericType<>() {
        });

        assertNotNull(response);
        assertEquals(USER_NAME, getSearchName(pr.getResults(), USER_NAME));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindUsersByManager() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.findUsers(auth.getJwtoken(),
                "0", "4", "user_name", "ASC", USER_NAME, ORGANISATION_NAME, "USM-UserManager");
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<UserAccount> pr = response.readEntity(new javax.ws.rs.core.GenericType<>() {
        });

        assertNotNull(response);
        assertEquals(USER_NAME, getSearchName(pr.getResults(), USER_NAME));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetUser() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.getUser(auth.getJwtoken(), USER_NAME);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount userAccount = response.readEntity(UserAccount.class);
        assertEquals(USER_NAME, userAccount.getUserName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.getUserNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {
        });
        List<String> result = sar.getResults();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateUser() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        UserAccount user = createUser();

        Response response = restClient.createUser(auth.getJwtoken(), user);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount created = response.readEntity(UserAccount.class);
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdateUser() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        UserAccount user = createUser();

        Response response = restClient.createUser(auth.getJwtoken(), user);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount created = response.readEntity(UserAccount.class);
        assertNotNull(created);

        // Update
        created.getPerson().setFirstName("firstNameUpdated");
        created.getPerson().setLastName("lastNameUpdated");

        response = restClient.updateUser(auth.getJwtoken(), created);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount updated = response.readEntity(UserAccount.class);
        assertNotNull(updated);
        assertEquals("firstNameUpdated", updated.getPerson().getFirstName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testChangePasswordByAdministrator() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        UserAccount user = createUser();

        Response response = restClient.createUser(auth.getJwtoken(), user);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount created = response.readEntity(UserAccount.class);
        assertNotNull(created);

        ChangePassword request = new ChangePassword();
        request.setUserName(user.getUserName());
        request.setNewPassword("p@3$w0rdD");

        response = restClient.changePassword(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        // Verify: test user logs-in with changed password
        auth = restClient.authenticateUser(request.getUserName(), request.getNewPassword());
        assertNotNull(auth);
        assertNotNull(auth.getJwtoken());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testChangePasswordByEndUser() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        // Create User
        UserAccount user = createUser();
        Response response = restClient.createUser(auth.getJwtoken(), user);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserAccount created = response.readEntity(UserAccount.class);
        assertNotNull(created);

        // User updated by Admin
        String initialPassword = "p@3$w0rdD";
        ChangePassword request = new ChangePassword();
        request.setUserName(user.getUserName());
        request.setNewPassword(initialPassword);

        response = restClient.changePassword(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        // User updated by User itself
        String updatedPassword = "P@3$w0rdD";
        request = new ChangePassword();
        request.setUserName(user.getUserName());
        request.setCurrentPassword(initialPassword);
        request.setNewPassword(updatedPassword);

        auth = restClient.authenticateUser(request.getUserName(), initialPassword);
        response = restClient.changePassword(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        auth = restClient.authenticateUser(request.getUserName(), request.getNewPassword());
        assertNotNull(auth);
        assertNotNull(auth.getJwtoken());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetUserContexts() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.getUserContexts(auth.getJwtoken(), USER_NAME);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserContextResponse uc = response.readEntity(UserContextResponse.class);
        assertNotNull(uc);
        assertNotNull(getUserContext(uc.getResults(), "User"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateUpdateDeleteUserContext() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        String username = "guest";
        UserContext userContext = createUserContextRequest(username);

        Response response = restClient.getUserContexts(auth.getJwtoken(), username);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserContextResponse ucr = response.readEntity(UserContextResponse.class);

        String expected = "Administrator";
        Long userContextId;

        // Create
        if (getUserContext(ucr.getResults(), expected) == null) {
            response = restClient.createUserContext(auth.getJwtoken(), userContext);
            assertEquals(OK.getStatusCode(), response.getStatus());
            userContext = response.readEntity(UserContext.class);
            userContextId = userContext.getUserContextId();
        } else {
            userContextId = getUserContext(ucr.getResults(), expected).getUserContextId();
        }

        response = restClient.getUserContexts(auth.getJwtoken(), username);
        ucr = response.readEntity(UserContextResponse.class);

        // Update
        userContext.setRoleId(2L);
        userContext.setScopeId(2L);
        userContext.setUserContextId(userContextId);

        response = restClient.updateUserContext(auth.getJwtoken(), userContext);
        assertEquals(OK.getStatusCode(), response.getStatus());
        userContext = response.readEntity(UserContext.class);
        assertEquals(Long.valueOf(2), userContext.getScopeId());

        // Delete
        response = restClient.deleteUserContext(auth.getJwtoken(), userContext.getUserName(), userContextId.toString());
        assertEquals(OK.getStatusCode(), response.getStatus());

        // Validation
        response = restClient.getUserContexts(auth.getJwtoken(), userContext.getUserName());
        assertEquals(OK.getStatusCode(), response.getStatus());
        ucr = response.readEntity(UserContextResponse.class);
        assertNull(getUserContextId(ucr.getResults(), userContextId));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCopyUserContext() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        // From user contexts
        Response response = restClient.getUserContexts(auth.getJwtoken(), VMS_ADMIN_COM_USER);
        UserContextResponse responseUsmUser = response.readEntity(UserContextResponse.class);

        // To user contexts
        response = restClient.getUserContexts(auth.getJwtoken(), "vms_user_com");
        UserContextResponse responseGuestInitial = response.readEntity(UserContextResponse.class);

        response = restClient.copyUserProfiles(auth.getJwtoken(), "vms_user_com", responseUsmUser.getResults());
        assertEquals(OK.getStatusCode(), response.getStatus());

        response = restClient.getUserContexts(auth.getJwtoken(), VMS_ADMIN_COM_USER);
        UserContextResponse responseGuestFinal = response.readEntity(UserContextResponse.class);

        assertTrue(checkUserContexts(responseUsmUser.getResults(), responseGuestFinal.getResults()));

        // Delete copied contexts
        for (ComprehensiveUserContext element : responseGuestFinal.getResults()) {
            response = restClient.deleteUserContext(auth.getJwtoken(),
                    "vms_user_com", element.getUserContextId().toString());
            assertEquals(OK.getStatusCode(), response.getStatus());
        }

        responseGuestFinal = restClient.getUserContexts(auth.getJwtoken(), VMS_ADMIN_COM_USER)
                .readEntity(UserContextResponse.class);
        assertTrue(responseGuestFinal.getResults().isEmpty());

        // Add original contexts
        UserContext uc = new UserContext();
        uc.setUserName("vms_user_com");

        for (ComprehensiveUserContext element : responseGuestInitial.getResults()) {
            uc.setRoleId(element.getRoleId());
            uc.setScopeId(element.getScopeId());
            restClient.createUserContext(auth.getJwtoken(), uc);
        }

        responseGuestFinal = restClient.getUserContexts(auth.getJwtoken(), "vms_user_com")
                .readEntity(UserContextResponse.class);
        assertTrue(responseGuestFinal.getResults().size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetChallenges() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.getChallenges(auth.getJwtoken(), VMS_ADMIN_COM_USER);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ChallengeInformationResponse cir = response.readEntity(ChallengeInformationResponse.class);

        assertNotNull(cir);
        assertNotNull(cir.getResults());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testSetChallenges() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        ChallengeInformationResponse request = new ChallengeInformationResponse();
        request.setUserPassword("password");
        List<ChallengeInformation> results = new ArrayList<>();
        ChallengeInformation challengeInformation = new ChallengeInformation();
        challengeInformation.setChallenge("Name of first pet updated!");
        challengeInformation.setResponse("Tartampion updated!");
        results.add(challengeInformation);
        request.setResults(results);

        Response response = restClient.setChallenges(auth.getJwtoken(), VMS_ADMIN_COM_USER, request);
        assertEquals(OK.getStatusCode(), response.getStatus());
        ChallengeInformationResponse cir = response.readEntity(ChallengeInformationResponse.class);

        assertNotNull(cir);
        assertNotNull(cir.getResults());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetUserPreferences() {
//        ServiceRequest<FindUserPreferenceQuery> request = new ServiceRequest<>();
//        request.setRequester(usmAdmin);
//        FindUserPreferenceQuery query = new FindUserPreferenceQuery();
//        query.setUserName("vms_super_fra");
//        query.setGroupName("Union-VMS");
//        request.setBody(query);



        String username = "vms_super_fra";
        String groupName = "Union-VMS";


        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.getUserPreferences(auth.getJwtoken(), username, groupName);
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserPreferenceResponse upr = response.readEntity(UserPreferenceResponse.class);

        String expectedOptionValue = "fr_FR";
        assertNotNull(upr);
        assertNotNull(upr.getResults());
        assertNotNull(getUserPreference(upr.getResults(), expectedOptionValue));
    }

//        // Execute
//        ClientResponse response = manageUserClient.getUserPreferences(ClientResponse.class, request);
//
//        GenericType<UserPreferenceResponse> gType = new GenericType<UserPreferenceResponse>() {
//        };
//
//        // Verify
//        String expectedOptionValue = "fr_FR";
//        assertNotNull("Unexpected null response", response);
//        UserPreferenceResponse upr = response.getEntity(gType);
//        assertNotNull("Unexpected null UserPreferenceResponse", upr);
//        assertNotNull("Unexpected null UserPreferenceResponse.results", upr.getResults());
//        assertNotNull("Expected 'User' preference not found", getUserPreference(upr.getResults(), expectedOptionValue));
//    }

    private UserAccount createUser() {
        UserAccount ret = new UserAccount();
        ret.setUserName("testRestUser" + System.currentTimeMillis());
        ret.setStatus("E");
        Organisation org = new Organisation();
        ret.setOrganisation(org);
        org.setName("DG-MARE");

        Person person = new Person();
        ret.setPerson(person);
        person.setFirstName("Test-Rest");
        person.setLastName("User");
        person.setEmail(ret.getUserName() + "@email.com");

        ret.setActiveFrom(new Date(System.currentTimeMillis() - 3600000));
        ret.setActiveTo(new Date(System.currentTimeMillis() + 36000000));

        return ret;
    }

    private String getSearchName(List<UserAccount> userAccounts, String expected) {
        for (UserAccount userAccount : userAccounts) {
            if (userAccount.getUserName().equals(expected)) {
                return expected;
            } else if (userAccount.getPerson().getFirstName().equals(expected)) {
                return expected;
            } else if (userAccount.getPerson().getLastName().equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private ComprehensiveUserContext getUserContext(List<ComprehensiveUserContext> list, String expected) {
        if (list != null && !list.isEmpty()) {
            for (ComprehensiveUserContext item : list) {
                if (item != null && item.getRole() != null && item.getRole().equals(expected)) {
                    return item;
                }
            }
        }
        return null;
    }

    private UserContext createUserContextRequest(String username) {
        UserContext userContext = new UserContext();
        userContext.setUserName(username);
        userContext.setUserContextId(null);
        userContext.setRoleId(2L);
        userContext.setScopeId(1L);
        return userContext;
    }

    private Long getUserContextId(List<ComprehensiveUserContext> cUserContexts, Long expected) {
        for (ComprehensiveUserContext userContext : cUserContexts) {
            if (userContext != null && userContext.getUserContextId().equals(expected)) {
                return userContext.getUserContextId();
            }
        }
        return null;
    }

    private Preference getUserPreference(List<Preference> cUserPreferences, String expected) {
        if (cUserPreferences != null && !cUserPreferences.isEmpty()) {
            for (Preference cUserPreference : cUserPreferences) {
                if (cUserPreference != null && cUserPreference.getOptionValue() != null &&
                        expected.equals(new String(cUserPreference.getOptionValue()))) {
                    return cUserPreference;
                }
            }
        }
        return null;
    }

    private boolean checkUserContexts(List<ComprehensiveUserContext> fromList, List<ComprehensiveUserContext> toList) {
        for (ComprehensiveUserContext element : fromList) {
            if (!toList.contains(element)) {
                return false;
            }
        }
        return true;
    }
}
