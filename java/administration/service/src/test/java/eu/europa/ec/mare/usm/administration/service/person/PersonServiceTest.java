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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;

@RunWith(Arquillian.class)
public class PersonServiceTest {

  @EJB
  private PersonService testSubject;

  @Test
  public void testGetPerson() 
  {
    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester("vms_admin_com");
    //find guest person
    request.setBody(4L);

    Person contact = testSubject.getPerson(request);

    assertNotNull("Person was retrieved", contact);
    assertTrue("The person is guest", contact.getFirstName().equalsIgnoreCase("guest"));
  }

  /**
   * Tests the updateContactDetails method
   */
  @Test
  public void testUpdateContactDetails() 
  {
    // Set-up
    ContactDetails personDetails = new ContactDetails();
    personDetails.setEmail("vms_admin_fra@gouv.fre");

    ServiceRequest<ContactDetails> request = new ServiceRequest<>();
    request.setRequester("vms_user_fra");
    request.setPassword("password");
    request.setBody(personDetails);

    // Execute
    ContactDetails response = testSubject.updateContactDetails(request);

    // Verify
    assertNotNull("Unexpected null result", response);
    assertEquals("Contact details did not update", "vms_admin_fra@gouv.fre",
                 response.getEmail());

    // reverse changes
    request.getBody().setEmail("vms_admin_fra@gouv.fr");
    testSubject.updateContactDetails(request);

  }

  /**
   * Tests the updateContactDetails method
   */
  @Test
  public void testUpdateContactDetailsWithInvalidPassword() 
  {
    // Set-up
    ContactDetails personDetails = new ContactDetails();
    personDetails.setEmail("vms_admin_fra@gouv.fre");

    ServiceRequest<ContactDetails> request = new ServiceRequest<>();
    request.setRequester("vms_user_fra");
    request.setPassword("drowssap");
    request.setBody(personDetails);

    try {
      testSubject.updateContactDetails(request);
      fail("Failed to trigger IllegalArgumentException");
    } catch (EJBException | IllegalArgumentException exc) {
      System.out.println("Triggered expected exception: " + exc.getMessage());
    }
  }

  /**
   * Tests the updateContactDetails method
   */
  @Test
  public void testUpdateContactDetailsWithEmptyPassword() 
  {
    // Set-up
    ContactDetails personDetails = new ContactDetails();
    personDetails.setEmail("vms_admin_fra@gouv.fre");

    ServiceRequest<ContactDetails> request = new ServiceRequest<>();
    request.setRequester("vms_user_fra");
    request.setBody(personDetails);

    try {
      testSubject.updateContactDetails(request);
      fail("Failed to trigger IllegalArgumentException");
    } catch (EJBException | IllegalArgumentException exc) {
      System.out.println("Triggered expected exception: " + exc.getMessage());
    }
  }

  /**
   * Tests the updateContactDetails method
   */
  @Test
  public void testUpdateNonExistingContactDetails() 
  {
    // Set-up
    ContactDetails personDetails = new ContactDetails();
    personDetails.setEmail("usm_user@mail.org");

    ServiceRequest<ContactDetails> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setPassword("password");
    request.setBody(personDetails);

    testSubject.updateContactDetails(request);
  }
  
  /**
   * Tests the getContactDetails method
   */
  @Test
  public void testGetContactDetails() 
  {
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester("usm_user");
    request.setBody("usm_user");
    
    ContactDetails check = testSubject.getContactDetails(request);
    
    assertNotNull("Unexpected null result", check);
  }

  /**
   * Tests the getContactDetails method
   */
  @Test
  public void testGetContactDetailsPersonExists() 
  {
    ServiceRequest<String> request = new ServiceRequest<>();
    request.setRequester("vms_user_fra");
    request.setBody("vms_user_fra");
    
    ContactDetails check = testSubject.getContactDetails(request);
    
    assertNotNull("Unexpected null result", check);
  }

}