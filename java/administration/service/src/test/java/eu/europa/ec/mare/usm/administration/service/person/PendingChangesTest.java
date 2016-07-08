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
package eu.europa.ec.mare.usm.administration.service.person;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import org.junit.Before;

/**
 * Unit test for the PersonService features related to Pending Contact Details Changes. 
 */
@RunWith(Arquillian.class)
public class PendingChangesTest {
  private static final String POLICY_SUBJECT = "Feature";
  private static final String USM_ADMIN = "usm_admin";
  private static final String QUOTA_USR_GRC = "quota_usr_grc";
  private static final String QUOTA_USR_FRA = "quota_usr_fra";
  private static final String UPDATE_CONTACT_DETAILS = "update.contact.details.enabled";
  private static final String REVIEW_CONTACT_DETAILS = "review.contact.details.enabled";

  @EJB
  private PersonService testSubject;
  @EJB
  private DefinitionService policyService;
  
  private PolicyDefinition savedDefinition;
  private String pendingGRCPhoneNumber;
  private String pendingGRCEmail;

  @Before
  public void setUp() 
  throws IOException 
  {
    // Save the contact details policy definition
    savedDefinition = policyService.getDefinition(POLICY_SUBJECT);

    // Ensure contact details update and review are enabled
    Properties props = new Properties();
    props.setProperty(UPDATE_CONTACT_DETAILS, "true");
    props.setProperty(REVIEW_CONTACT_DETAILS, "true");
    ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(new PolicyDefinition());
    request.getBody().setSubject(POLICY_SUBJECT);
    request.getBody().setProperties(props);
    policyService.setDefinition(request);
    
    createPendingChange(QUOTA_USR_GRC);
    createPendingChange(QUOTA_USR_FRA);
  }

  private void createPendingChange(String userName) 
  {
    ServiceRequest<String> check = new ServiceRequest<>();
    check.setRequester(USM_ADMIN);
    check.setBody(userName);

    PendingContactDetails result = testSubject.getPendingContactDetails(check);
    if (result == null) {
    	String phoneNumber = "Tel" + System.currentTimeMillis();
    	String email = "Mailto:" + userName + "@mail.world";
    	if(userName.equals(QUOTA_USR_GRC)){
        	pendingGRCPhoneNumber = phoneNumber;
        	pendingGRCEmail = email;
        }
      ServiceRequest<ContactDetails> request = new ServiceRequest<>();
      request.setRequester(userName);
      request.setPassword("password");
      request.setBody(new ContactDetails());
      request.getBody().setFaxNumber("Fax" + System.currentTimeMillis());
      request.getBody().setMobileNumber("Mob" + System.currentTimeMillis());
      request.getBody().setPhoneNumber(phoneNumber);
      request.getBody().setEmail(email);
      
      testSubject.updateContactDetails(request);
    } else{
    	if(userName.equals(QUOTA_USR_GRC)){
        	pendingGRCPhoneNumber = result.getPhoneNumber();
        	pendingGRCEmail = result.getEmail();
        }
    }
    
  }
  
  @After
  public void tearDown() 
  {
    createPendingChange(QUOTA_USR_GRC);
    createPendingChange(QUOTA_USR_FRA);

    // Restore the initial policy definition
    ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(savedDefinition);

    policyService.setDefinition(request);
  }
  

  
  /**
   * Tests the findPendingContactDetails operation
   */
  @Test
  public void testFindPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<NoBody> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);

    // Execute
    List<PendingContactDetails> result = testSubject.findPendingContactDetails(request);

    // Verify
    assertNotNull("Unexpected null response", result);
    assertFalse("Unexpected empty result", result.isEmpty());
  }

  /**
   * Tests the getPendingContactDetails operation
   */
  @Test
  public void testGetPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(QUOTA_USR_GRC);

    // Execute
    PendingContactDetails result = testSubject.getPendingContactDetails(request);

    // Verify
    assertNotNull("Unexpected null response", result);
    assertEquals("Unexpected usrName value", request.getBody(), result.getUserName());
    assertEquals("Unexpected phoneNumber value", pendingGRCPhoneNumber, result.getPhoneNumber());
    assertEquals("Unexpected email value", pendingGRCEmail, result.getEmail());
  }
  
  /**
   * Tests the rejectPendingContactDetails operation
   */
  @Test
  public void testRejectPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(QUOTA_USR_FRA);

    ContactDetails verify = testSubject.getContactDetails(request); 
    // Execute
    ContactDetails result = testSubject.rejectPendingContactDetails(request);

    // Verify
    assertNotNull("Unexpected null response", verify);
    assertNotNull("Unexpected null response", result);
    assertEquals("Unexpected phoneNumber value", verify.getPhoneNumber(), result.getPhoneNumber());
    assertEquals("Unexpected email value", verify.getEmail(), result.getEmail());
    
  }
  
  /**
   * Tests the acceptPendingContactDetails operation
   */
  @Test
  public void testAcceptPendingContactDetails() 
  {
    // Set-up
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(QUOTA_USR_GRC);
    PendingContactDetails expected = testSubject.getPendingContactDetails(request);
    assertNotNull("Unexpected null response", expected);

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
  }
  

}