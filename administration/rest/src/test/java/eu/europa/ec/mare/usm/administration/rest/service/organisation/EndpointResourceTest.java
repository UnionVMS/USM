package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class EndpointResourceTest extends BuildAdministrationDeployment {

    private static final String ORGANISATION_EC = "EC";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testCreateEndpoint() {
        EndPoint endpointRequest = createEndpointRequest();
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Response response = restClient.createEndPoint(auth.getJwtoken(), endpointRequest);

        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        EndPoint endPoint = response.readEntity(EndPoint.class);
        assertNotNull("Unexpected null endpoint", endPoint);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdateEndpoint() {
        EndPoint request = createEndpointRequest();
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.createEndPoint(auth.getJwtoken(), request);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        EndPoint endPoint = response.readEntity(EndPoint.class);
        endPoint.setEmail("updated@email.com");

        response = restClient.updateEndpoint(auth.getJwtoken(), endPoint);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        endPoint = response.readEntity(EndPoint.class);

        assertNotNull("Unexpected returned endpoint is null", endPoint);
        assertEquals("Unexpected Status", "updated@email.com", endPoint.getEmail());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testDeleteEndpoint() {
        EndPoint request = createEndpointRequest();
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.createEndPoint(auth.getJwtoken(), request);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        EndPoint endPoint = response.readEntity(EndPoint.class);

        response = restClient.deleteEndpoint(auth.getJwtoken(), String.valueOf(endPoint.getEndpointId()));
        assertEquals("Unexpected Response.Status", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private EndPoint createEndpointRequest() {
        EndPoint endPoint = new EndPoint();
        endPoint.setName("testEndpoint" + System.currentTimeMillis());
        endPoint.setStatus("E");
        endPoint.setEmail("test@email.com");
        endPoint.setOrganisationName(ORGANISATION_EC);
        endPoint.setDescription("EC test endpoint");
        endPoint.setUri("http://test.uri.eu");
        return endPoint;
    }
}
