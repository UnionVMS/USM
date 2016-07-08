/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import eu.europa.ec.mare.usm.administration.domain.*;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

import javax.ws.rs.core.Response;

/**
 * Integration test for the Organisation REST service.
 */
public class OrganisationRestServiceIT extends AuthWrapper {

  private static final String NATION_EEC = "EEC";
  private static final String ORGANISATION_FRA = "FRA";
  private static final String ORGANISATION_EC = "EC";
  private static final String ORGANISATION_GRC = "GRC";
  private static final String ENDPOINT_NAME_GRC = "FLUX.GRC";
  private static final String ENDPOINT_NAME_GRC_BACK = "FLUX.GRC_backup";
  private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
  private static final String USM_ADMIN = "usm_admin";
  private OrganisationRestClient client = null;
  private final String vms_admin_com;
  private final String usm_admin;

  /**
   * Creates a new instance.
   *
   * @throws IOException in case the '/test.properties' class-path resource
   * cannot be loaded
   */
  public OrganisationRestServiceIT()
          throws IOException {
    super(VMS_ADMIN_COM_USER);
    vms_admin_com = getAuthToken();
    usm_admin = authenticate(USM_ADMIN);
  }

  @Before
  public void setUp() {
    client = new OrganisationRestClient(endPoint);
  }

  /**
   * Tests the getOrganisationNames operation.
   */
  @Test
  public void testOrganisationNames() {
    // Execute
    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(new OrganisationQuery());

    ClientResponse response = client.getOrganisationNames(ClientResponse.class, request);
    GenericType<ServiceArrayResponse<OrganisationNameResponse>> gType = new GenericType<ServiceArrayResponse<OrganisationNameResponse>>() {
    };
    ServiceArrayResponse<OrganisationNameResponse> sar = response.getEntity(gType);

    // Verify
    assertNotNull("Unexpected null result", response);
    List<OrganisationNameResponse> orgNames = sar.getResults();
    assertEquals("Unexpected 'orgName' value", ORGANISATION_FRA, getNameFromList(orgNames, ORGANISATION_FRA));
    assertEquals("Unexpected 'orgName' value", ORGANISATION_EC, getNameFromList(orgNames, ORGANISATION_EC));
    assertEquals("Unexpected 'orgName' value", ORGANISATION_GRC, getNameFromList(orgNames, ORGANISATION_GRC));
  }

  /**
   * Tests the getOrganisationParentNames operation.
   */
  @Test
  public void testGetOrganisationParentNames() {
    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(2l);

    ClientResponse response = client.getOrganisationParentNames(ClientResponse.class, request);
    GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
    };
    ServiceArrayResponse<String> sar = response.getEntity(gType);

    // Verify
    assertNotNull("Unexpected null result", response);
    List<String> orgNames = sar.getResults();
    String expected = "DG-MARE";
    assertNotNull("Expected potential Parent Organisation Name '" + expected +
                  "' not found in [" + response + "]",
                  getFromList(orgNames, expected));
  }

  private String getFromList(List<String> oNames, String expected) {
    for (String org : oNames) {
      if (org.equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  /**
   * Tests the getNationNames operation.
   */
  @Test
  public void testNationNames() {
    // Execute
    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(new OrganisationQuery());

    ClientResponse response = client.getNationNames(ClientResponse.class, request);
    GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
    };
    ServiceArrayResponse<String> sar = response.getEntity(gType);

    // Verify
    assertNotNull("Unexpected null result", response);
    List<String> orgNames = sar.getResults();
    assertEquals("Unexpected 'nationName' value", NATION_EEC,
            getNationNameFromList(orgNames, NATION_EEC));
  }

  private String getNameFromList(List<OrganisationNameResponse> oNames, String expected) {
    for (OrganisationNameResponse org : oNames) {
      if (org.getParentOrgName().equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  private String getNationNameFromList(List<String> oNames, String expected) {
    for (String name : oNames) {
      if (name.equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  @Test
  public void testFindOrganisations() {
    // Execute
    ServiceRequest<FindOrganisationsQuery> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    FindOrganisationsQuery query = new FindOrganisationsQuery();
    query.setPaginator(getDefaultPaginator());
    request.setBody(query);
    ClientResponse response = client.findOrganisations(ClientResponse.class, request);
    GenericType<PaginationResponse<Organisation>> gType = new GenericType<PaginationResponse<Organisation>>() {
    };
    PaginationResponse<Organisation> sar = response.getEntity(gType);
    // Verify
    assertNotNull("Unexpected null result", response);
    List<Organisation> organisations = sar.getResults();
    Organisation org = getOrganisation(organisations, ORGANISATION_GRC);
    assertNotNull("Expected Organisation " + ORGANISATION_GRC + " not found",
            org);
  }

  @Test
  public void testGetOrganisation() {
    // Set-up
    Long organisationId = findOrganisation(ORGANISATION_GRC);

    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(organisationId);

    ClientResponse response = client.getOrganisation(ClientResponse.class, request);
    GenericType<Organisation> gType = new GenericType<Organisation>() {
    };
    Organisation organisation = response.getEntity(gType);
    // Verify
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'scopeName' value", ORGANISATION_GRC, organisation.getName());
  }

  @Test
  public void testCreateOrganisation() {
    ServiceRequest<Organisation> request = createOrgRequest();

    ClientResponse response = client.createOrganisation(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    Organisation org = response.getEntity(Organisation.class);
    assertNotNull("Unexpected null organisation", org);
  }

  @Test
  public void testUpdateOrganisation() {
    ServiceRequest<Organisation> request = createOrgRequest();

    ClientResponse response = client.createOrganisation(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    Organisation sar = response.getEntity(Organisation.class);
    sar.setStatus("D");
    request.setBody(sar);

    //responseUpdate
    response = client.updateOrganisation(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<Organisation> orgType = new GenericType<Organisation>() {
    };
    Organisation org = response.getEntity(orgType);

    assertNotNull("Unexpected returned organisation is null", org);
    assertTrue("Unexpected Status", org.getStatus().equals("D"));
  }

  @Test
  public void testDeleteOrganisation() {
    //prepare the role
    ServiceRequest<Organisation> request = createOrgRequest();

    ClientResponse response = client.createOrganisation(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<Organisation> gType = new GenericType<Organisation>() {
    };
    Organisation sar = response.getEntity(gType);

    //delete the role
    ServiceRequest<String> deleteRequest = new ServiceRequest<>();
    deleteRequest.setBody(sar.getOrganisationId().toString());
    deleteRequest.setRequester(request.getRequester());

    response = client.deleteOrganisation(ClientResponse.class, deleteRequest);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

  }

  private ServiceRequest<Organisation> createOrgRequest() {
    Organisation requestBody = new Organisation();
    requestBody.setName("testOrg" + System.currentTimeMillis());
    requestBody.setStatus("E");
    requestBody.setNation(NATION_EEC);
    requestBody.setDescription("Test organisation");

    ServiceRequest<Organisation> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(requestBody);

    return request;
  }

  @Test
  public void testGetEndPoint() {
    // Set-up
    List<EndPoint> endPointIds = findOrganisationEndPoints(ORGANISATION_GRC);

    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(endPointIds.get(0).getEndpointId());

    ClientResponse response = client.getEndPoint(ClientResponse.class, request);
    GenericType<EndPoint> gType = new GenericType<EndPoint>() {
    };
    EndPoint endPoint = response.getEntity(gType);
    // Verify
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'scopeName' value", ENDPOINT_NAME_GRC, endPoint.getName());
  }

  @Test
  public void testCreateEndpoint() {
    ServiceRequest<EndPoint> request = createEndpointRequest();

    ClientResponse response = client.createEndPoint(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    EndPoint endPoint = response.getEntity(EndPoint.class);
    assertNotNull("Unexpected null endpoint", endPoint);
  }

  @Test
  public void testUpdateEndpoint() {
    ServiceRequest<EndPoint> request = createEndpointRequest();

    ClientResponse response = client.createEndPoint(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    EndPoint sar = response.getEntity(EndPoint.class);
    sar.setStatus("D");
    sar.setOrganisationName(ORGANISATION_EC);
    request.setBody(sar);

    //responseUpdate
    response = client.updateEndPoint(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<EndPoint> orgType = new GenericType<EndPoint>() {
    };
    EndPoint endPoint = response.getEntity(orgType);

    assertNotNull("Unexpected returned endpoint is null", endPoint);
    assertTrue("Unexpected Status", endPoint.getStatus().equals("D"));
  }

  @Test
  public void testDeleteEndpoint() {
    //prepare the role
    ServiceRequest<EndPoint> request = createEndpointRequest();

    ClientResponse response = client.createEndPoint(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<EndPoint> gType = new GenericType<EndPoint>() {
    };
    EndPoint sar = response.getEntity(gType);

    //delete the role
    ServiceRequest<String> deleteRequest = new ServiceRequest<>();
    deleteRequest.setBody(sar.getEndpointId().toString());
    deleteRequest.setRequester(request.getRequester());

    response = client.deleteEndPoint(ClientResponse.class, deleteRequest);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

  }

  private ServiceRequest<EndPoint> createEndpointRequest() {
    EndPoint requestBody = new EndPoint();
    requestBody.setName("testEndpoint" + System.currentTimeMillis());
    requestBody.setStatus("E");
    requestBody.setOrganisationName(ORGANISATION_EC);
    requestBody.setDescription("EC test endpoint");
    requestBody.setURI("http://test.uri.eu");

    ServiceRequest<EndPoint> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(requestBody);

    return request;
  }

  @Test
  public void testGetEndPointContact() {
    // Set-up
    List<EndPoint> endPointIds = findOrganisationEndPoints(ORGANISATION_GRC);

    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(endPointIds.get(0).getEndpointId());

    ClientResponse response = client.getEndPoint(ClientResponse.class, request);
    GenericType<EndPoint> gType = new GenericType<EndPoint>() {
    };
    EndPoint endPoint = response.getEntity(gType);
    // Verify endpoint
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'scopeName' value", ENDPOINT_NAME_GRC, endPoint.getName());

    // get endpoint contact
    ServiceRequest<Long> endPointContactRequest = new ServiceRequest<>();
    endPointContactRequest.setRequester(vms_admin_com);
    Long endPointContactId = endPoint.getPersons().get(0).getEndPointContactId();
    endPointContactRequest.setBody(endPointContactId);

    ClientResponse endPointContactResponse = client.getEndpointContact(ClientResponse.class, endPointContactRequest);
    GenericType<EndPointContact> personType = new GenericType<EndPointContact>() {
    };
    EndPointContact endPointContact = endPointContactResponse.getEntity(personType);

    // Verify channel
    assertNotNull("Unexpected null result", endPointContactResponse);
    assertEquals("Unexpected 'channelId' value", endPointContact.getEndPointContactId(), endPointContactId);
  }

  @Test
  public void testAssignContact() {
    ServiceRequest<EndPointContact> request = assignContactRequest();

    ClientResponse response = client.assignContact(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    EndPointContact endPointContact = response.getEntity(EndPointContact.class);
    assertNotNull("Unexpected null endpoint contact ", endPointContact);
  }

  @Test
  public void testRemoveContact() {
    //prepare the role
    ServiceRequest<EndPointContact> request = assignContactRequest();
    EndPointContact sar = null;
    EndPointContact endPointContact = findEndPointContact(ORGANISATION_GRC, ENDPOINT_NAME_GRC_BACK, 4L);
    if (endPointContact == null) {
      ClientResponse response = client.assignContact(ClientResponse.class, request);
      assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

      GenericType<EndPointContact> gType = new GenericType<EndPointContact>() {
      };
      sar = response.getEntity(gType);
    } else {
      sar = endPointContact;
    }
    //delete the role
    ServiceRequest<String> deleteRequest = new ServiceRequest<>();
    deleteRequest.setBody(sar.getEndPointContactId().toString());
    deleteRequest.setRequester(request.getRequester());

    ClientResponse removeResponse = client.removeContact(ClientResponse.class, deleteRequest);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), removeResponse.getStatus());

  }

  private ServiceRequest<EndPointContact> assignContactRequest() {
    EndPointContact requestBody = new EndPointContact();
    requestBody.setPersonId(4L);
    EndPoint endPoint = findOrganisationEndPoint(ORGANISATION_GRC, ENDPOINT_NAME_GRC_BACK);
    requestBody.setEndPointId(endPoint.getEndpointId());

    ServiceRequest<EndPointContact> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(requestBody);

    return request;
  }

  @Test
  public void testGetChannel() {
    // Set-up
    List<EndPoint> endPointIds = findOrganisationEndPoints(ORGANISATION_GRC);

    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(endPointIds.get(1).getEndpointId());

    ClientResponse response = client.getEndPoint(ClientResponse.class, request);
    GenericType<EndPoint> gType = new GenericType<EndPoint>() {
    };
    EndPoint endPoint = response.getEntity(gType);
    // Verify endpoint
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'scopeName' value", ENDPOINT_NAME_GRC_BACK, endPoint.getName());

    // get channel
    ServiceRequest<Long> channelRequest = new ServiceRequest<>();
    channelRequest.setRequester(vms_admin_com);
    Long channelId = endPoint.getChannelList().get(0).getChannelId();
    channelRequest.setBody(channelId);

    ClientResponse channelResponse = client.getChannel(ClientResponse.class, channelRequest);
    GenericType<Channel> channelType = new GenericType<Channel>() {
    };
    Channel channel = channelResponse.getEntity(channelType);

    // Verify channel
    assertNotNull("Unexpected null result", channelResponse);
    assertEquals("Unexpected 'channelId' value", channel.getChannelId(), channelId);
  }

  @Test
  public void testCreateChannel() {
    ServiceRequest<Channel> request = createChannelRequest();

    ClientResponse response = client.createChannel(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    Channel channel = response.getEntity(Channel.class);
    assertNotNull("Unexpected null channel", channel);
  }

  @Test
  public void testUpdateChannel() {
    ServiceRequest<Channel> request = createChannelRequest();

    ClientResponse response = client.createChannel(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    Channel sar = response.getEntity(Channel.class);
    sar.setPriority(2);
    request.setBody(sar);

    //responseUpdate
    response = client.updateChannel(ClientResponse.class, request);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<Channel> orgType = new GenericType<Channel>() {
    };
    Channel channel = response.getEntity(orgType);

    assertNotNull("Unexpected returned endpoint is null", channel);
    assertTrue("Unexpected priority", channel.getPriority().equals(2));
  }

  @Test
  public void testDeleteChannel() {
    //prepare the role
    ServiceRequest<Channel> request = createChannelRequest();

    ClientResponse response = client.createChannel(ClientResponse.class, request);

    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

    GenericType<Channel> gType = new GenericType<Channel>() {
    };
    Channel sar = response.getEntity(gType);

    //delete the role
    ServiceRequest<String> deleteRequest = new ServiceRequest<>();
    deleteRequest.setBody(sar.getChannelId().toString());
    deleteRequest.setRequester(request.getRequester());

    response = client.deleteChannel(ClientResponse.class, deleteRequest);
    assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

  }

  private ServiceRequest<Channel> createChannelRequest() {
    Channel requestBody = new Channel();
    requestBody.setDataflow("testDataflow" + System.currentTimeMillis());
    requestBody.setService("service");
    requestBody.setPriority(1);
    EndPoint endPoint = findOrganisationEndPoint(ORGANISATION_GRC, ENDPOINT_NAME_GRC_BACK);
    requestBody.setEndpointId(endPoint.getEndpointId());

    ServiceRequest<Channel> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(requestBody);

    return request;
  }

  private EndPointContact findEndPointContact(String organisationName, String endpointName, Long contactId) {
    EndPointContact ret = null;
    Long organisationId = findOrganisation(organisationName);
    Organisation organisation = getOrganisationById(organisationId);
    assertNotNull("Unexpected null result", organisation);
    assertNotNull("Unexpected null result", organisation.getEndpoints());
    List<EndPoint> endPoints = findOrganisationEndPoints(organisationName);
    for (EndPoint endPoint : endPoints) {
      if (endpointName.equals(endPoint.getName())) {
        List<EndPointContact> contacts = endPoint.getPersons();
        for (EndPointContact contact : contacts) {
          if (contact.getPersonId().equals(contactId)) {
            ret = contact;
            break;
          }
        }
        break;
      }
    }
    return ret;
  }

  private EndPoint findOrganisationEndPoint(String organisationName, String endpointName) {
    EndPoint ret = new EndPoint();
    Long organisationId = findOrganisation(organisationName);
    Organisation organisation = getOrganisationById(organisationId);
    assertNotNull("Unexpected null result", organisation);
    assertNotNull("Unexpected null result", organisation.getEndpoints());
    List<EndPoint> endPoints = findOrganisationEndPoints(organisationName);
    for (EndPoint endPoint : endPoints) {
      if (endpointName.equals(endPoint.getName())) {
        ret = endPoint;
        break;
      }
    }
    return ret;
  }

  private List<EndPoint> findOrganisationEndPoints(String organisationName) {
    Long organisationId = findOrganisation(organisationName);
    Organisation organisation = getOrganisationById(organisationId);
    assertNotNull("Unexpected null result", organisation);
    assertNotNull("Unexpected null result", organisation.getEndpoints());
    return organisation.getEndpoints();
  }

  public Long findOrganisation(String organisationName) {
    ServiceRequest<FindOrganisationsQuery> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    FindOrganisationsQuery query = new FindOrganisationsQuery();
    query.setName(organisationName);
    query.setPaginator(getDefaultPaginator());
    request.setBody(query);

    ClientResponse response = client.findOrganisations(ClientResponse.class, request);
    GenericType<PaginationResponse<Organisation>> gType = new GenericType<PaginationResponse<Organisation>>() {
    };
    PaginationResponse<Organisation> sar = response.getEntity(gType);
    // Verify
    assertNotNull("Unexpected null result", response);
    List<Organisation> organisations = sar.getResults();
    assertEquals("Unexpected result size", 1, organisations.size());
    return organisations.get(0).getOrganisationId();
  }

  public Organisation getOrganisationById(Long organisationId) {
    // Execute
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(vms_admin_com);
    request.setBody(organisationId);

    ClientResponse response = client.getOrganisation(ClientResponse.class, request);
    GenericType<Organisation> gType = new GenericType<Organisation>() {
    };
    Organisation organisation = response.getEntity(gType);
    // Verify
    assertNotNull("Unexpected null result", response);
    return organisation;
  }

  private Organisation getOrganisation(List<Organisation> orgs, String expected) {
    for (Organisation org : orgs) {
      if (org.getName().equals(expected)) {
        return org;
      }
    }
    return null;
  }

  private Paginator getDefaultPaginator() {
    Paginator paginator = new Paginator();
    paginator.setLimit(8);
    paginator.setOffset(0);
    paginator.setSortColumn("name");
    paginator.setSortDirection("ASC");
    return paginator;
  }

}