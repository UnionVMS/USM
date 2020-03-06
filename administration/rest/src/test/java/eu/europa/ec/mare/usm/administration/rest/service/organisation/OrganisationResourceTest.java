package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import eu.europa.ec.mare.usm.administration.rest.service.TestHelper;
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
public class OrganisationResourceTest extends BuildAdministrationDeployment {

    private static final String NATION_EEC = "EEC";
    private static final String ORGANISATION_FRA = "FRA";
    private static final String ORGANISATION_EC = "EC";
    private static final String ORGANISATION_GRC = "GRC";
    private static final String ENDPOINT_NAME_GRC = "FLUX.GRC";
    private static final String ENDPOINT_NAME_GRC_BACK = "FLUX.GRC_backup";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @EJB
    private TestHelper testHelper;

    @Test
    @OperateOnDeployment("normal")
    public void testOrganisationNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getOrganisationNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<OrganisationNameResponse> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        List<OrganisationNameResponse> orgNames = sar.getResults();

        assertEquals(ORGANISATION_FRA, getNameFromList(orgNames, ORGANISATION_FRA));
        assertEquals(ORGANISATION_EC, getNameFromList(orgNames, ORGANISATION_EC));
        assertEquals(ORGANISATION_GRC, getNameFromList(orgNames, ORGANISATION_GRC));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetOrganisationParentNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getOrganisationParentNames(String.valueOf(21), auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        List<String> orgNames = sar.getResults();
        assertTrue(orgNames.stream().anyMatch("DG-MARE"::equals));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testNationNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.getNationNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<String> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        List<String> orgNames = sar.getResults();
        assertTrue(orgNames.stream().anyMatch(NATION_EEC::equals));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindOrganisations() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Response response = restClient.findOrganisations(auth.getJwtoken(), null);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<Organisation> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        List<Organisation> organisations = sar.getResults();
        Organisation organisation = organisations.stream()
                .filter(or -> or.getName().equals(ORGANISATION_GRC)).findAny().orElse(null);
        assertNotNull(organisation);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetOrganisation() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Organisation organisation = getOrganisationByName(auth.getJwtoken(), ORGANISATION_GRC);
        assertEquals(ORGANISATION_GRC, organisation.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateOrganisation() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Organisation organisation = createOrganisation();
        Response response = restClient.createOrganisation(auth.getJwtoken(), organisation);
        assertEquals(OK.getStatusCode(), response.getStatus());

        Organisation created = response.readEntity(Organisation.class);
        assertNotNull(created);
        assertEquals(organisation.getName(), created.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdateOrganisation() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Organisation organisation = createOrganisation();
        Response response = restClient.createOrganisation(auth.getJwtoken(), organisation);
        Organisation created = response.readEntity(Organisation.class);
        String status = "D";
        created.setStatus(status);

        response = restClient.updateOrganisation(auth.getJwtoken(), created);

        assertEquals(OK.getStatusCode(), response.getStatus());
        Organisation updated = response.readEntity(Organisation.class);
        assertNotNull(updated);
        assertEquals(status, updated.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testDeleteOrganisation() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Organisation organisation = createOrganisation();
        Response response = restClient.createOrganisation(auth.getJwtoken(), organisation);
        Organisation created = response.readEntity(Organisation.class);

        response = restClient.deleteOrganisation(auth.getJwtoken(), String.valueOf(created.getOrganisationId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetOrganisationEndPoint() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        long organisationId = testHelper.getOrganisationIdByName(auth.getJwtoken(), ORGANISATION_GRC);
        Response response = restClient.getOrganisationById(auth.getJwtoken(), String.valueOf(organisationId));
        assertEquals(OK.getStatusCode(), response.getStatus());

        Organisation organisation = response.readEntity(Organisation.class);
        List<EndPoint> endpoints = organisation.getEndpoints();

        String endPointId = String.valueOf(endpoints.get(0).getEndpointId());
        response = restClient.getEndPoint(auth.getJwtoken(), endPointId);
        EndPoint ep = response.readEntity(EndPoint.class);

        assertEquals(ENDPOINT_NAME_GRC, ep.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetEndPointContact() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        long organisationId = testHelper.getOrganisationIdByName(auth.getJwtoken(), ORGANISATION_GRC);
        Response response = restClient.getOrganisationById(auth.getJwtoken(), String.valueOf(organisationId));
        Organisation organisation = response.readEntity(Organisation.class);

        List<EndPoint> endpoints = organisation.getEndpoints();
        EndPoint endPoint = endpoints.get(0);
        assertEquals(ENDPOINT_NAME_GRC, endPoint.getName());

        Long endPointContactId = endPoint.getPersons().get(0).getEndPointContactId();
        Response endPointContactResponse = restClient.getEndPointContact(auth.getJwtoken(), String.valueOf(endPointContactId));
        EndPointContact endPointContact = endPointContactResponse.readEntity(EndPointContact.class);

        assertNotNull(endPointContactResponse);
        assertEquals(endPointContact.getEndPointContactId(), endPointContactId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAssignAndRemoveContact() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        EndPointContact epc = assignContactRequest(auth.getJwtoken());
        Response response = restClient.assignContact(auth.getJwtoken(), epc);
        assertEquals(OK.getStatusCode(), response.getStatus());

        EndPointContact endPointContact = response.readEntity(EndPointContact.class);
        assertNotNull(endPointContact);

        Response deleteResponse = restClient.removeContact(auth.getJwtoken(), String.valueOf(endPointContact.getEndPointId()));
        assertEquals(OK.getStatusCode(), deleteResponse.getStatus());
    }

    /** Helper Methods */
    private String getNameFromList(List<OrganisationNameResponse> oNames, String expected) {
        return oNames.stream()
                .filter(org -> org.getParentOrgName().equals(expected))
                .findAny()
                .map(OrganisationNameResponse::getParentOrgName)
                .orElse(null);
    }

    private EndPointContact assignContactRequest(String jwtToken) {
        EndPoint endPoint = testHelper.findOrganisationEndPoint(ORGANISATION_GRC, ENDPOINT_NAME_GRC_BACK, jwtToken);
        EndPointContact contact = new EndPointContact();
        contact.setPersonId(4L);
        contact.setEndPointId(endPoint.getEndpointId());
        return contact;
    }

    private Organisation getOrganisationByName(String jwtToken, String orgName) {
        Response response = restClient.findOrganisations(jwtToken, orgName);
        PaginationResponse<Organisation> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {});
        List<Organisation> organisations = sar.getResults();
        return organisations.stream()
                .filter(org -> orgName.equals(org.getName()))
                .findAny()
                .orElse(null);
    }

    private Organisation createOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setName("testOrg" + System.currentTimeMillis());
        organisation.setStatus("E");
        organisation.setNation(NATION_EEC);
        organisation.setDescription("Test organisation");
        return organisation;
    }
}
