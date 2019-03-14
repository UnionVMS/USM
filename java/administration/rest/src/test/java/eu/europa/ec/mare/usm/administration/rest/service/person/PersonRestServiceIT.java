package eu.europa.ec.mare.usm.administration.rest.service.person;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

/**
 * Integration test for the REST implementation of the PersonService
 */
public class PersonRestServiceIT extends AuthWrapper {
  private static final String USER_VMS_FRA = "vms_admin_fra";
  private static final String USM_ADMIN = "usm_admin";
  private PersonRestClient testSubject;
  private final String usm_admin;
  private final String vms_admin_fra;

  /**
   * Creates a new instance.
   * 
   * @throws IOException in case the "/test.properties" class-path resource
   * cannot be accessed
   */
  public PersonRestServiceIT() 
  throws IOException 
  {    
    super(USM_ADMIN);
  usm_admin = getAuthToken();
    vms_admin_fra = authenticate(USER_VMS_FRA);
  }

  @Before
  public void setUp() 
  {
    testSubject = new PersonRestClient(endPoint);
  }

  
  /**
   * Tests the getPersons operation
   */
  @Test
  public void testGetPersons() 
  {
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(usm_admin);

    List<Person> response = testSubject.getPersons(request);

    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected empty response", response.isEmpty());
  }

  /**
   * Tests the getPerson operation
   */
  @Test
  public void testGetPerson() 
  {
    Long personId = 4L;
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(personId);

    Person response = testSubject.getPerson(request);

    assertNotNull("Unexpected null response", response);
    assertEquals("Unexpected firstName value", "guest", response.getFirstName());
    assertEquals("Unexpected personId value", personId, response.getPersonId());
  }

  /**
   * Tests the isUpdateContactDetailsEnabled operation
   */
  @Test
  public void testIsUpdateContactDetailsEnabled() 
  {
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(usm_admin);

    Boolean response = testSubject.isUpdateContactDetailsEnabled(request);

    assertNotNull("Unexpected null response", response);
  }

  /**
   * Tests the updateContactDetails operation
   */
  @Test
  public void testUpdateContactDetails() 
  {
    ServiceRequest<ContactDetailsRequest> request = createRequest("vms_admin_fra@gouv.fre");

    ContactDetails response = testSubject.updateContactDetails(request);

    assertNotNull("Unexpected null response", response);
    assertEquals("Unexpected email value", request.getBody().getEmail(), 
                                           response.getEmail());

    
    ServiceRequest<ContactDetailsRequest> revert = createRequest("vms_admin_fra@gouv.fr");

    ContactDetails reverted = testSubject.updateContactDetails(revert);
    assertNotNull("Unexpected null response", reverted);
    assertEquals("Unexpected email value", revert.getBody().getEmail(), 
                                           reverted.getEmail());
  }
  
  private ServiceRequest<ContactDetailsRequest> createRequest(String email) 
  {
    ContactDetailsRequest requestBody = new ContactDetailsRequest();
    requestBody.setEmail(email);
    requestBody.setUserName("vms_user_fra");
    requestBody.setPassword("password");

    ServiceRequest<ContactDetailsRequest> ret = new ServiceRequest<>();
    ret.setRequester(vms_admin_fra);
    ret.setPassword("password");
    ret.setBody(requestBody);

    return ret;
  }
}
