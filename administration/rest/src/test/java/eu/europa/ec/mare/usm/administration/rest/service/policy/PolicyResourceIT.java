package eu.europa.ec.mare.usm.administration.rest.service.policy;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PolicyResourceIT extends BuildAdministrationDeployment {

    private static final String POL_PASSWORD = "Password";
    private static final String POL_ACCOUNT = "Account";
    private static final String POL_FEATURE = "Feature";
    private static final String POL_ADMINISTRATION = "Administration";
    private static final String POL_AUTHENTICATION = "Authentication";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testFindPolicies() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.findPolicies(auth.getJwtoken(), "password.minLength", POL_PASSWORD);
        assertEquals(OK.getStatusCode(), response.getStatus());

        List<Policy> policies = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        assertNotNull(policies);
        assertFalse(policies.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePolicy() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.findPolicies(auth.getJwtoken(), "password.minLength", POL_PASSWORD);
        assertEquals(OK.getStatusCode(), response.getStatus());

        List<Policy> policies = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        assertNotNull(policies);
        assertEquals("Expected 1 result", 1, policies.size());
        assertEquals("password.minLength initial value should be 8", "8", policies.get(0).getValue());

        Policy uPolicy = policies.get(0);
        uPolicy.setValue("24");

        response = restClient.updatePolicy(auth.getJwtoken(), uPolicy);
        assertEquals(OK.getStatusCode(), response.getStatus());

        Policy updatedPolicy = response.readEntity(Policy.class);
        assertEquals("password.minLength updated to 24", "24", updatedPolicy.getValue());

        response = restClient.findPolicies(auth.getJwtoken(), "password.minLength", POL_PASSWORD);
        policies = response.readEntity(new javax.ws.rs.core.GenericType<>() {});

        assertEquals("Expected 1 result", 1, policies.size());
        assertEquals("password.minLength updated to 24", "24", policies.get(0).getValue());

        uPolicy = policies.get(0);
        uPolicy.setValue("8");

        response = restClient.updatePolicy(auth.getJwtoken(), uPolicy);
        assertEquals(OK.getStatusCode(), response.getStatus());

        updatedPolicy = response.readEntity(Policy.class);
        assertEquals("password.minLength updated to its original value of 8", "8", updatedPolicy.getValue());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testSubjects() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Response response = restClient.getSubjects(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new GenericType<>() {});

        assertNotNull("Unexpected null result", sar);
        List<String> subjects = sar.getResults();
        assertNotNull("Expected Subject " + POL_PASSWORD + " not found",
                getSubject(subjects, POL_PASSWORD));
        assertNotNull("Expected Subject " + POL_ACCOUNT + "not found",
                getSubject(subjects, POL_ACCOUNT));
        assertNotNull("Expected Subject " + POL_FEATURE + "not found",
                getSubject(subjects, POL_FEATURE));
        assertNotNull("Expected Subject " + POL_ADMINISTRATION + "not found",
                getSubject(subjects, POL_ADMINISTRATION));
        assertNotNull("Expected Subject " + POL_AUTHENTICATION + "not found",
                getSubject(subjects, POL_AUTHENTICATION));
    }

    private String getSubject(List<String> subjects, String expected) {
        return subjects.stream()
                .filter(expected::equals)
                .findAny()
                .orElse(null);
    }
}
