package eu.europa.ec.mare.usm.administration.service.organisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.Channel;
import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.OrganisationNameResponse;
import eu.europa.ec.mare.usm.administration.domain.OrganisationQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.administration.service.organisation.impl.OrganisationServiceBean;

/**
 * Unit-test for the OrganisationService
 */
@RunWith(Arquillian.class)
public class OrganisationServiceTest extends DeploymentFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationServiceBean.class);

  @EJB
  OrganisationService testSubject;

  /**
   * Creates a new instance
   */
  public OrganisationServiceTest() {
  }

  /**
   * Tests the getOrganisationNames method.
   */
  @Test
  public void testGetOrganisationNames()
  {
    // Setup
    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setBody(new OrganisationQuery());

    // Execute
    List<OrganisationNameResponse> response;
    response = testSubject.getOrganisationNames(request);

    // Verify
    String expected = "EC";
    assertNotNull("Unexpected null result", response);
    assertNotNull("Expected Organisation Name '" + expected +
                  "' not found in [" + response + "]",
                  getNameFromList(response, expected));
  }

  /**
   * Tests the getOrganisationParentNames method.
   */
  @Test
  public void testGetOrganisationParentNames()
  {
    // Setup
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setBody(2l);

    // Execute
    List<String> response;
    response = testSubject.getOrganisationParentNames(request);

    // Verify
    assertNotNull("Unexpected null result", response);
    String expected = "DG-MARE";
    assertNotNull("Expected potential Parent Organisation Name '" + expected +
                  "' not found in [" + response + "]",
                  getFromList(response, expected));
  }

  private String getFromList(List<String> names, String expected) {
    for (String orgName : names) {
      if (orgName.equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  /**
   * Tests the getNationNames method.
   */
  @Test
  public void testGetNationNames() {
    // Setup
    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setBody(new OrganisationQuery());

    // Execute
    List<String> response = testSubject.getNationNames(request);

    // Verify
    String expected = "EEC";
    assertNotNull("Unexpected null result", response);
    assertNotNull("Expected Nation '" + expected +" ' not found",
                  getNationNameFromList(response, expected));
  }

  private String getNameFromList(List<OrganisationNameResponse> names, String expected) {
    for (OrganisationNameResponse orgName : names) {
      if (orgName.getParentOrgName().equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  private String getNationNameFromList(List<String> names, String expected) {
    for (String element : names) {
      if (element.equals(expected)) {
        return expected;
      }
    }

    return null;
  }

  /**
   * Tests the getOrganisationByName method
   */
  @Test
  public void testGetOrganisationByName() {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setBody("EC");

    // Execute
    Organisation response = testSubject.getOrganisationByName(request);

    // Verify
    String expected = "EC";
    assertNotNull("Unexpected null result", response);
    assertNotNull("Unexpected null organisationId", response.getOrganisationId());
    assertEquals("Unexpected organisationName", expected, response.getName());
  }

  /**
   * Tests the getOrganisation method
   */
  @Test
  public void testGetOrganisation() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("DG-MARE");
    Organisation setup = testSubject.getOrganisationByName(r1);
    assertNotNull("Unexpected null result", setup);
    assertNotNull("Unexpected null organisationId", setup.getOrganisationId());

    // Execute
    ServiceRequest<Long> r2 = new ServiceRequest<Long>();
    r2.setRequester("usm_user");
    r2.setBody(setup.getOrganisationId());
    Organisation response = testSubject.getOrganisation(r2);

    // Verify
    String expected = "DG-MARE";
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected organisationName", expected, response.getName());
  }

  /**
   * Tests the createOrganisation method
   */
  @Test
  public void testCreateOrganisation() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("testCreateOrganisation");
    Organisation cleanup = testSubject.getOrganisationByName(r1);
    if (cleanup != null) {
      ServiceRequest<Long> r2 = new ServiceRequest<Long>();
      r2.setRequester("usm_admin");
      r2.setBody(cleanup.getOrganisationId());

      testSubject.deleteOrganisation(r2);
    }

    // Execute
    ServiceRequest<Organisation> request = new ServiceRequest<Organisation>();
    request.setRequester("usm_admin");
    request.setBody(new Organisation());
    request.getBody().setName("testCreateOrganisation " + System.currentTimeMillis());
    request.getBody().setDescription("A simple organisation");
    request.getBody().setNation("TST");
    request.getBody().setStatus("D");
    request.getBody().setParent("DG-MARE");
    Organisation organisation = testSubject.createOrganisation(request);

    // Verify
    assertNotNull("Unexpected null organisationId", organisation.getOrganisationId());
    ServiceRequest<Long> r3 = new ServiceRequest<Long>();
    r3.setRequester("usm_admin");
    r3.setBody(organisation.getOrganisationId());
    Organisation response = testSubject.getOrganisation(r3);
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected organisationName", request.getBody().getName(),
            response.getName());
  }

  /**
   * Tests the updateOrganisation method
   */
  @Test
  public void testUpdateOrganisation() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("testUpdateOrganisation");
    Organisation setup = testSubject.getOrganisationByName(r1);
    ServiceRequest<Organisation> r2 = new ServiceRequest<Organisation>();
    r2.setRequester("usm_admin");
    r2.setBody(setup);
    if (setup == null) {
      r2.setBody(new Organisation());
      r2.getBody().setName("testUpdateOrganisation");
      r2.getBody().setNation("TST");
      r2.getBody().setStatus("E");
      Organisation organisation = testSubject.createOrganisation(r2);
      r2.getBody().setOrganisationId(organisation.getOrganisationId());
    }

    // Execute
    testSubject.updateOrganisation(r2);

    // Verify
    ServiceRequest<String> r3 = new ServiceRequest<String>();
    r3.setRequester("usm_user");
    r3.setBody("testUpdateOrganisation");
    Organisation response = testSubject.getOrganisationByName(r3);
    assertNotNull("Unexpected null result", response);

  }

  /**
   * Tests the deleteOrganisation method
   */
  @Test
  public void testDeleteOrganisation() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("testDeleteOrganisation");
    Organisation setup = testSubject.getOrganisationByName(r1);
    Long organisationId = null;
    if (setup != null) {
      organisationId = setup.getOrganisationId();
    } else {
      ServiceRequest<Organisation> r2 = new ServiceRequest<Organisation>();
      r2.setRequester("usm_admin");
      r2.setBody(setup);
      r2.setBody(new Organisation());
      r2.getBody().setName("testDeleteOrganisation");
      r2.getBody().setNation("TST");
      r2.getBody().setStatus("E");
      organisationId = testSubject.createOrganisation(r2).getOrganisationId();
    }

    // Execute
    ServiceRequest<Long> request = new ServiceRequest<Long>();
    request.setRequester("usm_admin");
    request.setBody(organisationId);

    testSubject.deleteOrganisation(request);

    // Verify
    Organisation response = testSubject.getOrganisation(request);
    assertNull("Unexpected non-null result", response);
  }

  @Test
  public void testGetEndPoint() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("GRC");
    Organisation setup = testSubject.getOrganisationByName(r1);
    assertNotNull("Unexpected null result", setup);
    assertNotNull("Unexpected null organisationId", setup.getEndpoints());

    // Execute
    ServiceRequest<Long> r2 = new ServiceRequest<Long>();
    r2.setRequester("usm_user");
    r2.setBody(setup.getEndpoints().get(0).getEndpointId());
    EndPoint response = testSubject.getEndPoint(r2);

    // Verify
    String expected = "FLUX.GRC";
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected end-point name", expected, response.getName());
  }

  @Test
  public void testGetEndPointContact() {
    // Set-up
    ServiceRequest<String> r1 = new ServiceRequest<String>();
    r1.setRequester("usm_user");
    r1.setBody("GRC");
    Organisation setup = testSubject.getOrganisationByName(r1);
    assertNotNull("Unexpected null result", setup);
    assertNotNull("Unexpected null organisationId", setup.getEndpoints());

    // Execute
    ServiceRequest<Long> r2 = new ServiceRequest<Long>();
    r2.setRequester("usm_user");
    r2.setBody(setup.getEndpoints().get(0).getEndpointId());
    EndPoint responseEndPoint = testSubject.getEndPoint(r2);

    if (responseEndPoint.getPersons() != null && !responseEndPoint.getPersons().isEmpty()) {
      r2.setBody(responseEndPoint.getPersons().get(0).getEndPointContactId());
      EndPointContact responseContact = testSubject.getContact(r2);

      // Verify
      assertNotNull("Unexpected null result", responseContact);
    }
  }

  @Test
  public void testCreateUpdateDeleteEndpoints() {
    	//the new endpoint will be created assigned to the usm application and will be removed after this test

    LOGGER.info("testCreateUpdateDeleteEndpoints ----------------------------");

    ServiceRequest<EndPoint> newEndPoint = createEndpointRequest("GRC");
    EndPoint ep = newEndPoint.getBody();
    ep = testSubject.createEndPoint(newEndPoint);
    assertNotNull("The endpoint wasn't created", ep.getEndpointId());

    ep.setDescription("new description added");
    ep.setStatus("D");
    newEndPoint.setBody(ep);
    EndPoint updatedEp = testSubject.updateEndPoint(newEndPoint);

    assertTrue("Description wasn't updated", updatedEp.getDescription().equalsIgnoreCase("new description added"));
    assertTrue("Status wasn't updated", updatedEp.getStatus().equalsIgnoreCase("D"));

    ServiceRequest<Long> deleteep = new ServiceRequest<>();
    deleteep.setRequester("usm_admin");
    deleteep.setBody(ep.getEndpointId());

    testSubject.deleteEndPoint(deleteep);

    updatedEp = testSubject.getEndPoint(deleteep);
    assertNull("End point wasn't deleted ", updatedEp);
  }

  ;
    
    @Test
  public void testCreateUpdateDeleteChannel() {
    ServiceRequest<EndPoint> newEndPoint = createEndpointRequest("GRC");

    EndPoint ep = testSubject.createEndPoint(newEndPoint);
    assertNotNull("The endpoint wasn't created", ep.getEndpointId());

    ServiceRequest<Channel> newChannel = new ServiceRequest<Channel>();
    newChannel.setRequester("usm_admin");
    Channel channelBody = new Channel();
    channelBody.setDataflow("dataflow1");
    channelBody.setEndpointId(ep.getEndpointId());
    channelBody.setPriority(1);
    channelBody.setService("service1");
    newChannel.setBody(channelBody);

    channelBody = testSubject.createChannel(newChannel);

    assertNotNull("Channel wasn't created", channelBody.getChannelId());

    channelBody.setChannelId(channelBody.getChannelId());
    channelBody.setDataflow("dataflow2");
    channelBody.setPriority(2);
    newChannel.setBody(channelBody);

    Channel updatedChannel = testSubject.updateChannel(newChannel);

    assertTrue("DataFlow wasn't updated ", updatedChannel.getDataflow().equalsIgnoreCase("dataflow2"));
    assertTrue("Priority wasn't updated ", updatedChannel.getPriority().equals(2));

    ServiceRequest<Long> idRequest = new ServiceRequest<>();
    idRequest.setRequester("usm_admin");
    idRequest.setBody(channelBody.getChannelId());
    testSubject.deleteChannel(idRequest);

    idRequest.setBody(channelBody.getEndpointId());
    ep = testSubject.getEndPoint(idRequest);
    assertTrue("Channel wasn't deleted", ep.getChannelList() == null || ep.getChannelList().isEmpty() ? true : false);

    testSubject.deleteEndPoint(idRequest);
    ep = testSubject.getEndPoint(idRequest);
    assertNull("End point wasn't deleted", ep);
  }

  @Test
  public void testAddRemoveContact() {
    ServiceRequest<EndPoint> newEndPoint = createEndpointRequest("GRC");

    EndPoint ep = testSubject.createEndPoint(newEndPoint);
    assertNotNull("The endpoint wasn't created", ep.getEndpointId());

    EndPointContact epc = new EndPointContact();
    epc.setEndPointId(ep.getEndpointId());
    epc.setPersonId(4L);
    ServiceRequest<EndPointContact> contact = new ServiceRequest<EndPointContact>();
    contact.setBody(epc);
    contact.setRequester("usm_admin");
    epc = testSubject.assignContact(contact);

    assertNotNull("The contact wasn't assigned", epc);
    assertNotNull("The assigned contact has not a proper identifier", epc.getEndPointContactId());

    contact.setBody(epc);
    testSubject.removeContact(contact);

    ServiceRequest<Long> idRequest = new ServiceRequest<>();
    idRequest.setRequester("usm_admin");
    idRequest.setBody(ep.getEndpointId());

    ep = testSubject.getEndPoint(idRequest);
    assertTrue("contact wasn't deleted", ep.getPersons() == null || ep.getPersons().isEmpty() ? true : false);

    testSubject.deleteEndPoint(idRequest);
    ep = testSubject.getEndPoint(idRequest);
    assertNull("The end-point wasn't deleted", ep);
  }

  private ServiceRequest<EndPoint> createEndpointRequest(String organisation) {
    ServiceRequest<EndPoint> newEndPoint = new ServiceRequest<EndPoint>();
    newEndPoint.setRequester("usm_admin");
    EndPoint ep = new EndPoint();
    ep.setName("testEndPoint");
    ep.setOrganisationName(organisation);
    ep.setEmail("test@endpoint.com");
    ep.setStatus("E");
    ep.setUri("test");
    newEndPoint.setBody(ep);
    return newEndPoint;
  }
}
