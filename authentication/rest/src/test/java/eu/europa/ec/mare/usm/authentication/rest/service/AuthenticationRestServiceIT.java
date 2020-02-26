package eu.europa.ec.mare.usm.authentication.rest.service;

import eu.europa.ec.mare.usm.BuildAuthenticationRestDeployment;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AuthenticationRestServiceIT extends BuildAuthenticationRestDeployment {

    private static final String PASSWORD = "password";
    private static final String RESPONSE = "Tintin";
    private static final String CHALLENGE = "Name of first pet";
    private static final String USER_VMS_ADMIN = "vms_admin_com";
    private static final String USER_VMS = "vms_user_com";

    @Test
    public void testAuthenticateUserREST() {
        // Execute
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName(USER_VMS_ADMIN);
        request.setPassword(PASSWORD);
        AuthenticationResponse result = authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertTrue(result.isAuthenticated());
        assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", result.getUserMap());
    }

    @Test
    public void testGetUserChallengeREST() {
        // Execute
        ChallengeResponse result = getUserChallenge(USER_VMS);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected 'challenge' value", CHALLENGE, result.getChallenge());
    }

    @Test
    public void testAuthenticateUserByChallenge() {
        // Execute
        ChallengeResponse request = new ChallengeResponse();
        request.setUserName(USER_VMS);
        request.setChallenge(CHALLENGE);
        request.setResponse(RESPONSE);
        AuthenticationResponse result = authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertTrue(result.isAuthenticated());
        assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", result.getUserMap());
    }

    @Test
    public void testAuthenticateUserSuccess() {
        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("quota_man_com");
        request.setPassword(PASSWORD);

        // Execute
        AuthenticationResponse response = authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertTrue(response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.SUCCESS, response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", response.getUserMap());
    }

    @Test
    public void testAuthenticateUserFailureAccountLocked() {
        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("lockout");
        request.setPassword(PASSWORD);

        // Execute
        AuthenticationResponse response = authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response", response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.ACCOUNT_LOCKED, response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", response.getUserMap());
    }

    @Test
    public void testAuthenticateUserFailureInvalidCredentials() {
        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("quota_usr_com");
        request.setPassword("wrong password");

        // Execute
        AuthenticationResponse response = authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response", response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.INVALID_CREDENTIALS,
                response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", response.getUserMap());

        // tear down to avoid user lock out
        request.setPassword(PASSWORD);
        response = authenticateUser(request);

        assertNotNull("Unexpected null response", response);
        assertTrue("Unexpected Authenticated response", response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.SUCCESS, response.getStatusCode());
    }

    private AuthenticationResponse authenticateUser(AuthenticationRequest request) {
        return getWebTargetInternal()
                .path("authenticate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), AuthenticationResponse.class);
    }

    private AuthenticationResponse authenticateUser(ChallengeResponse request) {
        return getWebTargetInternal()
                .path("challengeauth")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), AuthenticationResponse.class);
    }

    private ChallengeResponse getUserChallenge(String userNameToken) {
        return getWebTargetInternal()
                .path("challenge")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, userNameToken)
                .get(ChallengeResponse.class);
    }
}
