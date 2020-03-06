package eu.europa.ec.mare.usm.administration.rest.service.authentication;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import eu.europa.ec.mare.usm.administration.rest.service.policy.PolicyRestClient;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AuthenticationRestServiceIT extends BuildAdministrationDeployment {
    private static final String PASSWORD = "password";
    private static final String RESPONSE = "Tintin";
    private static final String CHALLENGE = "Name of first pet";
    private static final String USER_VMS_ADMIN = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
    private static final String USER_VMS = "vms_user_com";
    private PolicyRestClient client = null;

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserREST() {
        AuthenticationJwtResponse result = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);

        assertNotNull("Unexpected null result", result);
        assertTrue(result.isAuthenticated());
        assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetUserChallengeREST() {
        AuthenticationJwtResponse response = restClient.authenticateUser(USER_VMS, PASSWORD);
        ChallengeResponse result = restClient.getUserChallenge(response.getJwtoken());

        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected 'challenge' value", CHALLENGE, result.getChallenge());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserByChallenge() {
        ChallengeResponse request = new ChallengeResponse();
        request.setUserName(USER_VMS);
        request.setChallenge(CHALLENGE);
        request.setResponse(RESPONSE);
        AuthenticationResponse result = restClient.authenticateByChallenge(request);

        assertNotNull("Unexpected null result", result);
        assertTrue(result.isAuthenticated());
        assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserSuccess() {
        AuthenticationResponse response = restClient.authenticateUser("quota_man_com", "password");

        assertNotNull("Unexpected null response", response);
        assertTrue(response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.SUCCESS, response.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserFailureAccountLocked() {
        AuthenticationResponse response = restClient.authenticateUser("lockout", "password");

        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response", response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.ACCOUNT_LOCKED, response.getStatusCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAuthenticateUserFailureInvalidCredentials() {
        AuthenticationResponse response = restClient.authenticateUser("quota_usr_com", "wrong password");

        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response", response.isAuthenticated());
        assertEquals("Unexpected response StatusCode", AuthenticationResponse.INVALID_CREDENTIALS, response.getStatusCode());

    }

    @Test
    @OperateOnDeployment("normal")
    @Ignore("Is this really belong here? Will come back to this later.")
    public void testUpdatePolicy() {
        int initialValue = getMaxSessionAnySite();
        setMaxSessionAnySite(20);
        assertEquals("account.maxSessionOneSite is 20", 20, getMaxSessionAnySite());
        setMaxSessionAnySite(initialValue);
    }

    /** Helper Methods */
    private String getRequester() {
        AuthenticationJwtResponse authenticate = restClient.authenticateUser(USER_VMS_ADMIN, PASSWORD);
        return authenticate.getJwtoken();
    }

    public int getMaxSessionAnySite() {
        ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
        findReq.setRequester(getRequester());
        FindPoliciesQuery q = new FindPoliciesQuery();
        q.setName("account.maxSessionAnySite");
        q.setSubject("Account");
        findReq.setBody(q);
        ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
        GenericType<List<Policy>> gtype = new GenericType<>() {
        };
        List<Policy> policiesFound = findResp.getEntity(gtype);

        return Integer.parseInt(policiesFound.get(0).getValue());
    }

    public int getMaxSessionOneSite() {
        ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
        findReq.setRequester(getRequester());
        FindPoliciesQuery q = new FindPoliciesQuery();
        q.setName("account.maxSessionOneSite");
        q.setSubject("Account");
        findReq.setBody(q);
        ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
        GenericType<List<Policy>> gtype = new GenericType<>() {};
        List<Policy> policiesFound = findResp.getEntity(gtype);

        return Integer.parseInt(policiesFound.get(0).getValue());
    }

    public void setMaxSessionAnySite(int newValue) {
        ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
        findReq.setRequester(getRequester());
        FindPoliciesQuery q = new FindPoliciesQuery();
        q.setName("account.maxSessionAnySite");
        q.setSubject("Account");
        findReq.setBody(q);
        ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
        GenericType<List<Policy>> gtype = new GenericType<>() {};
        List<Policy> policiesFound = findResp.getEntity(gtype);

        ServiceRequest<Policy> req = new ServiceRequest<>();
        req.setRequester(getRequester());

        Policy uPolicy = policiesFound.get(0);
        uPolicy.setValue("" + newValue);
        req.setBody(uPolicy);

        client.updatePolicy(ClientResponse.class, req);
    }

    public void setMaxSessionOneSite(int newValue) {
        ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
        findReq.setRequester(getRequester());
        FindPoliciesQuery q = new FindPoliciesQuery();
        q.setName("account.maxSessionOneSite");
        q.setSubject("Account");
        findReq.setBody(q);
        ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
        GenericType<List<Policy>> gtype = new GenericType<>() {};
        List<Policy> policiesFound = findResp.getEntity(gtype);

        ServiceRequest<Policy> req = new ServiceRequest<>();
        req.setRequester(getRequester());

        Policy uPolicy = policiesFound.get(0);
        uPolicy.setValue("" + newValue);
        req.setBody(uPolicy);

        client.updatePolicy(ClientResponse.class, req);
    }
}
