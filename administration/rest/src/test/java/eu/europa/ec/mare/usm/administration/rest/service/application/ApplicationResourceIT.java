package eu.europa.ec.mare.usm.administration.rest.service.application;

import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
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
public class ApplicationResourceIT extends BuildAdministrationDeployment {

    private static final String APPLICATION_USM = "USM";
    private static final String APPLICATION_QUOTA = "Quota";
    private static final String APPLICATION_UVMS = "Union-VMS";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testFindApplications() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.findApplications(auth.getJwtoken(), null);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<Application> paginationResponse = response.readEntity(new GenericType<>() {});
        List<Application> applications = paginationResponse.getResults();
        assertFalse(applications.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindApplicationsWithNameParam() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.findApplications(auth.getJwtoken(), APPLICATION_USM);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<Application> paginationResponse = response.readEntity(new GenericType<>() {});
        List<Application> applications = paginationResponse.getResults();
        assertEquals(1, applications.size());
        assertEquals(APPLICATION_USM, applications.get(0).getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetApplicationDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getApplicationDetails(auth.getJwtoken(), APPLICATION_USM);
        assertEquals(OK.getStatusCode(), response.getStatus());

        eu.europa.ec.mare.usm.information.domain.deployment.Application application =
                response.readEntity(eu.europa.ec.mare.usm.information.domain.deployment.Application.class);

        assertNotNull(application);
        assertEquals(APPLICATION_USM, application.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetParentApplicationNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getParentApplicationNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new GenericType<>() {});
        List<String> appNames = sar.getResults();
        assertFalse(appNames.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetApplicationNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getApplicationNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new GenericType<>() {});
        List<String> appNames = sar.getResults();
        assertEquals(APPLICATION_USM, getAppName(appNames, APPLICATION_USM));
        assertEquals(APPLICATION_QUOTA, getAppName(appNames, APPLICATION_QUOTA));
        assertEquals(APPLICATION_UVMS, getAppName(appNames, APPLICATION_UVMS));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetApplicationFeatures() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getApplicationFeatures(auth.getJwtoken(), APPLICATION_USM);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<Feature> sar = response.readEntity(new GenericType<>() {});
        List<Feature> featureNames = sar.getResults();

        assertFalse(featureNames.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetAllFeatures() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getAllFeatures(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<Feature> sar = response.readEntity(new GenericType<>() {});
        List<Feature> featureNames = sar.getResults();

        assertFalse(featureNames.isEmpty());
    }


    /* HELPER METHODS */
    private String getAppName(List<String> aNames, String expected) {
        return aNames.stream()
                .filter(n -> n.equals(expected))
                .findAny()
                .orElse(null);
    }
}
