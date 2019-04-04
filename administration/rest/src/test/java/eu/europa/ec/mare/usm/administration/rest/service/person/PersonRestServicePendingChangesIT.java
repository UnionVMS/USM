package eu.europa.ec.mare.usm.administration.rest.service.person;


import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Integration test for the features related to Pending Contact 
 * Details Changes of the REST implementation of the PersonService.
 */
public class PersonRestServicePendingChangesIT extends AuthWrapper {
  private static final String QUOTA_USR_GRC = "quota_usr_grc";
  private static final String QUOTA_USR_FRA = "quota_usr_fra";
  private static final String USM_ADMIN = "usm_admin";
  private PersonRestClient testSubject;
  private final String usm_admin;

  /**
   * Creates a new instance.
   * 
   * @throws IOException in case the "/test.properties" class-path resource
   * cannot be accessed
   */
  public PersonRestServicePendingChangesIT() 
  throws IOException 
  {

    super(USM_ADMIN);
    usm_admin = getAuthToken();
  }

  @Before
  public void setUp() 
  {
    testSubject = new PersonRestClient(endPoint);
    
    
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    
    if (testSubject.isReviewContactDetailsEnabled(request)) {
      createPendingChange(QUOTA_USR_GRC);
      createPendingChange(QUOTA_USR_FRA);
    }
  }

  
  private void createPendingChange(String userName) 
  {
    ServiceRequest<String> check = new ServiceRequest<>();
    check.setRequester(usm_admin);
    check.setBody(userName);

    PendingContactDetails result = testSubject.getPendingContactDetails(check);
    if (result == null) {
      ServiceRequest<ContactDetailsRequest> request = new ServiceRequest<>();
      request.setRequester(userName);
      request.setPassword("password");
      request.setBody(new ContactDetailsRequest());
      request.getBody().setUserName(userName);
      request.getBody().setPassword("password");
      request.getBody().setFaxNumber("Fax" + System.currentTimeMillis());
      request.getBody().setMobileNumber("Mob" + System.currentTimeMillis());
      request.getBody().setPhoneNumber("Tel" + System.currentTimeMillis());
      request.getBody().setEmail("Mailto:" + userName + "@mail.world");
      
      testSubject.updateContactDetails(request);
    }
  }
    
  
  /**
   * Tests the isReviewContactDetailsEnabled operation
   */
  @Test
  public void testIsReviewContactDetailsEnabled() 
  {
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(usm_admin);

    Boolean response = testSubject.isReviewContactDetailsEnabled(request);

    assertNotNull("Unexpected null response", response);
  }

  /**
   * Tests the findPendingContactDetails operation
   */
  @Test
  public void testFindPendingContactDetails() 
  {
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(usm_admin);

    List<PendingContactDetails> response = testSubject.findPendingContactDetails(request);

    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected empty response", response.isEmpty());
  }

  /**
   * Tests the getPendingContactDetails operation
   */
  @Test
  public void testGetPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody("quota_usr_com");

    // Execute
    PendingContactDetails result = testSubject.getPendingContactDetails(request);

    // Verify
    assertNotNull("Unexpected null response", result);
    assertEquals("Unexpected usrName value", request.getBody(), result.getUserName());
    assertEquals("Unexpected phoneNumber value", "+32287654321", result.getPhoneNumber());
    assertEquals("Unexpected email value", "quota_usr_com@mail.europa.ec", result.getEmail());
  }
  
  /**
   * Tests the rejectPendingContactDetails operation
   */
  @Test
  public void testRejectPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(QUOTA_USR_FRA);
    PendingContactDetails expected = testSubject.getPendingContactDetails(request);

    if (expected != null) {
      // Execute
      ContactDetails result = testSubject.rejectPendingContactDetails(request);

      // Verify
      assertNotNull("Unexpected null response", result);
      assertNull("Unexpected non-null phoneNumber value", result.getPhoneNumber());
      assertEquals("Unexpected email value", "quota_usr_fra@gouv.fr", result.getEmail());

      ContactDetails verify = testSubject.getContactDetails(request);
      assertNotNull("Unexpected null response", verify);
      assertNull("Unexpected non-null phoneNumber value", verify.getPhoneNumber());
      assertEquals("Unexpected email value", "quota_usr_fra@gouv.fr", verify.getEmail());
    } else {
      System.out.println("WARNING: No pending update found to test rejectPendingContactDetails");
    }
  }
  
  /**
   * Tests the accepPendingContactDetails operation
   */
  @Test
  public void testAcceptPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(usm_admin);
    request.setBody(QUOTA_USR_GRC);
    PendingContactDetails expected = testSubject.getPendingContactDetails(request);

    if (expected != null) {
      // Execute
      ContactDetails result = testSubject.acceptPendingContactDetails(request);

      // Verify
      assertNotNull("Unexpected null response", result);
      assertEquals("Unexpected email value", expected.getEmail(), result.getEmail());
      assertEquals("Unexpected phoneNumber value", expected.getPhoneNumber(), result.getPhoneNumber());

      ContactDetails verify = testSubject.getContactDetails(request);
      assertNotNull("Unexpected null response", verify);
      assertEquals("Unexpected email value", expected.getEmail(), verify.getEmail());
      assertEquals("Unexpected phoneNumber value", expected.getPhoneNumber(), verify.getPhoneNumber());
    } else {
      System.out.println("WARNING: No pending update to test acceptPendingContactDetails");
    }
  }
  


}
