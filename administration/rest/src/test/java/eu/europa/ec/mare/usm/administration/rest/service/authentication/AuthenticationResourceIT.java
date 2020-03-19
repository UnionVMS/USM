package eu.europa.ec.mare.usm.administration.rest.service.authentication;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AuthenticationResourceIT extends BuildAdministrationDeployment {
    private static final String PASSWORD = "password";
    private static final String RESPONSE = "Tintin";
    private static final String CHALLENGE = "Name of first pet";
    private static final String USER_VMS_ADMIN = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
    private static final String USER_VMS = "vms_user_com";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserREST() {
        AuthenticationJwtResponse result = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(AuthenticationResponse.SUCCESS, result.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetUserChallengeREST() {
        AuthenticationJwtResponse response = restClient.authenticateUser(USER_VMS, PASSWORD);
        ChallengeResponse result = restClient.getUserChallenge(response.getJwtoken());

        assertNotNull(result);
        assertEquals(CHALLENGE, result.getChallenge());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserByChallenge() {
        ChallengeResponse request = new ChallengeResponse();
        request.setUserName(USER_VMS);
        request.setChallenge(CHALLENGE);
        request.setResponse(RESPONSE);
        AuthenticationResponse result = restClient.authenticateByChallenge(request);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(AuthenticationResponse.SUCCESS, result.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserSuccess() {
        AuthenticationResponse response = restClient.authenticateUser("quota_man_com", PASSWORD);

        assertNotNull(response);
        assertTrue(response.isAuthenticated());
        assertEquals(AuthenticationResponse.SUCCESS, response.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserFailureAccountLocked() {
        AuthenticationResponse response = restClient.authenticateUser("lockout", PASSWORD);

        assertNotNull(response);
        assertFalse(response.isAuthenticated());
        assertEquals(AuthenticationResponse.ACCOUNT_LOCKED, response.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserFailureInvalidCredentials() {
        AuthenticationResponse response = restClient.authenticateUser("quota_usr_com", "wrong password");

        assertNotNull(response);
        assertFalse(response.isAuthenticated());
        assertEquals(AuthenticationResponse.INVALID_CREDENTIALS, response.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetContexts() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS, PASSWORD);

        Response response = restClient.getContexts(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        UserContext userContext = response.readEntity(UserContext.class);
        assertNotNull(userContext);
        assertFalse(userContext.getContextSet().getContexts().isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPing() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS, PASSWORD);

        Response response = restClient.getPing(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        StatusResponse statusResponse = response.readEntity(StatusResponse.class);
        assertNotNull(statusResponse);
        assertEquals(OK.toString(), statusResponse.getMessage());
    }

    /**
     * Helper Methods
     */
    public int getMaxSessionAnySite() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);
        Response findResp = restClient.findPolicies(auth.getJwtoken(), "account.maxSessionAnySite", "Account");
        List<Policy> policiesFound = findResp.readEntity(new javax.ws.rs.core.GenericType<>() {
        });
        return Integer.parseInt(policiesFound.get(0).getValue());
    }

    public int getMaxSessionOneSite() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);
        Response findResp = restClient.findPolicies(auth.getJwtoken(), "account.maxSessionOneSite", "Account");
        List<Policy> policiesFound = findResp.readEntity(new javax.ws.rs.core.GenericType<>() {
        });
        return Integer.parseInt(policiesFound.get(0).getValue());
    }

    public void setMaxSessionAnySite(int newValue) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);
        Response findResp = restClient.findPolicies(auth.getJwtoken(), "account.maxSessionAnySite", "Account");
        List<Policy> policiesFound = findResp.readEntity(new javax.ws.rs.core.GenericType<>() {
        });

        Policy uPolicy = policiesFound.get(0);
        uPolicy.setValue("" + newValue);
        restClient.updatePolicy(auth.getJwtoken(), uPolicy);
    }

    public void setMaxSessionOneSite(int newValue) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);
        Response findResp = restClient.findPolicies(auth.getJwtoken(), "account.maxSessionOneSite", "Account");
        List<Policy> policiesFound = findResp.readEntity(new javax.ws.rs.core.GenericType<>() {
        });

        Policy uPolicy = policiesFound.get(0);
        uPolicy.setValue("" + newValue);
        restClient.updatePolicy(auth.getJwtoken(), uPolicy);
    }
}
