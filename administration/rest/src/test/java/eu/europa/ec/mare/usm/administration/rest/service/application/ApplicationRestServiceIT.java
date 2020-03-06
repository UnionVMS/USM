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

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ApplicationRestServiceIT extends BuildAdministrationDeployment {

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
        Response response = restClient.findApplications(auth.getJwtoken(), APPLICATION_USM);
        PaginationResponse<Application> paginationResponse = response.readEntity(new GenericType<>() {});

        assertNotNull("Unexpected null result", response);
        List<Application> applications = paginationResponse.getResults();
        assertNotNull("Expected Scope " + APPLICATION_USM + " not found", getApplication(applications, APPLICATION_USM));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testApplicationNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getApplicationNames(auth.getJwtoken());
        ServiceArrayResponse<String> sar = response.readEntity(new GenericType<>() {});

        assertNotNull("Unexpected null result", response);
        List<String> appNames = sar.getResults();
        assertEquals("Unexpected 'orgName' value", APPLICATION_USM, getAppName(appNames, APPLICATION_USM));
        assertEquals("Unexpected 'orgName' value", APPLICATION_QUOTA, getAppName(appNames, APPLICATION_QUOTA));
        assertEquals("Unexpected 'orgName' value", APPLICATION_UVMS, getAppName(appNames, APPLICATION_UVMS));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetApplicationFeatures() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getApplicationFeatures(auth.getJwtoken(), APPLICATION_USM);
        ServiceArrayResponse<Feature> sar = response.readEntity(new GenericType<>() {});

        assertNotNull("Unexpected null result", response);
        List<Feature> featureNames = sar.getResults();

        assertFalse("List of application features is empty", featureNames.isEmpty());
    }

    /* HELPER METHODS */

    private String getAppName(List<String> aNames, String expected) {
        return aNames.stream()
                .filter(n -> n.equals(expected))
                .findAny()
                .orElse(null);
    }

    private String getApplication(List<Application> applications, String expected) {
        return applications.stream()
                .filter(app -> app.getName().equals(expected))
                .findAny()
                .map(Application::getName)
                .orElse(null);
    }
}
