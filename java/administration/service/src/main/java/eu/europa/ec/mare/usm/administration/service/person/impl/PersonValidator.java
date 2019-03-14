package eu.europa.ec.mare.usm.administration.service.person.impl;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of Person 
 * related service requests
 */
public class PersonValidator extends RequestValidator {
  
  
  /**
   * Creates a new instance.
   */
  public PersonValidator() 
  {
  }

  
  public void assertValid(ServiceRequest<Person> request, USMFeature feature, 
                          boolean isCreate) 
  {
    assertValid(request, feature, "person");

    if (!isCreate) {
      assertNotNull("personId", request.getBody().getPersonId());
    }
  }

  void assertValid(ServiceRequest<ContactDetails> request) 
  {
    assertNotNull("request", request);
    assertNotEmpty("userName", request.getRequester());
    assertNotEmpty("password", request.getPassword());
    assertNotNull("contactDetails", request.getBody());
    assertNotEmpty("email", request.getBody().getEmail());
    assertNotTooLong("email", 64, request.getBody().getEmail());
    assertNotTooLong("faxNumber", 32, request.getBody().getFaxNumber());
    assertNotTooLong("phoneNumber", 32, request.getBody().getPhoneNumber());
    assertNotTooLong("mobileNumber", 32, request.getBody().getMobileNumber());
  }

}
