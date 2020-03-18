package eu.europa.ec.mare.usm.administration.service.person;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PersonServiceTest extends DeploymentFactory {

    @EJB
    private PersonService testSubject;

    @Test
    public void testGetPerson() {
        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        //find guest person
        request.setBody(4L);

        Person contact = testSubject.getPerson(request);

        assertNotNull("Person was retrieved", contact);
        assertTrue("The person is guest", contact.getFirstName().equalsIgnoreCase("guest"));
    }

    @Test
    public void testUpdateContactDetails() {
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
        assertEquals("Contact details did not update", "vms_admin_fra@gouv.fre", response.getEmail());

        // reverse changes
        request.getBody().setEmail("vms_admin_fra@gouv.fr");
        testSubject.updateContactDetails(request);
    }

    @Test
    public void testUpdateContactDetailsWithInvalidPassword() {
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

    @Test
    public void testUpdateContactDetailsWithEmptyPassword() {
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

    @Test
    public void testUpdateNonExistingContactDetails() {
        // Set-up
        ContactDetails personDetails = new ContactDetails();
        personDetails.setEmail("usm_user@mail.org");

        ServiceRequest<ContactDetails> request = new ServiceRequest<>();
        request.setRequester("usm_user");
        request.setPassword("password");
        request.setBody(personDetails);

        testSubject.updateContactDetails(request);
    }

    @Test
    public void testGetContactDetails() {
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester("usm_user");
        request.setBody("usm_user");

        ContactDetails check = testSubject.getContactDetails(request);
        assertNotNull("Unexpected null result", check);
    }

    @Test
    public void testGetContactDetailsPersonExists() {
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester("vms_user_fra");
        request.setBody("vms_user_fra");

        ContactDetails check = testSubject.getContactDetails(request);
        assertNotNull("Unexpected null result", check);
    }

}
