package eu.europa.ec.mare.usm.administration.service.person;

import java.util.List;

import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Service-level interface for the management of persons and of the 
 * user's contact details.
 */
public interface PersonService {

  /**
   * Retrieves the persons with the provided unique identifier.
   *
   * @param request the service request holding the unique identifier
   * 
   * @return the requested person if it exists, null otherwise
   * 
   * @throws IllegalArgumentException in case the service request is null, empty
   * or incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Person getPerson(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Retrieves the entire list of persons (dead or alive) from the database
   *
   * @return the possibly-empty list of persons
   * 
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<Person> getPersons() 
  throws RuntimeException;

  /**
   * Checks whether the <i>Update Contact Details</i> feature is enabled.
   * 
   * @return <i>true</i> if the feature is enabled, <i>false</i> otherwise.
   */
  public boolean isUpdateContactDetailsEnabled();
  
  /**
   * Updates user's contact details
   *
   * @param request holds the contact details of the user to be updated
   *
   * @return the update contact details
   *
   * @throws IllegalArgumentException in case the service request is null, 
   * empty or incomplete, or if the target user does not exist, or in case the 
   * service requester provided an incorrect password
   *
   * @throws IllegalStateException in case the Update Contact Details feature 
   * is disabled
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public ContactDetails updateContactDetails(ServiceRequest<ContactDetails> request)
  throws IllegalArgumentException, RuntimeException, IllegalStateException;
  
  /**
   * Retrieves the contact details of the user with the provided username.
   * 
   * @param request the service request holding the userName 
   * 
   * @return the user contact details
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * empty or if the target user does not exist
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public ContactDetails getContactDetails(ServiceRequest<String> request)
  throws IllegalArgumentException, RuntimeException;
  
  /**
   * Checks whether the <i>Review Contact Details</i> (by an 
   * administrator before becoming effective) feature is enabled.
   * 
   * @return <i>true</i> if the feature is enabled, <i>false</i> otherwise.
   * 
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public boolean isReviewContactDetailsEnabled()
  throws RuntimeException;
  
  /**
   * Retrieves the list of pending contact details.
   * 
   * @param request the service request holding details of the service requester
   * 
   * @return the possibly-empty list of pending contact details
   * 
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<PendingContactDetails> findPendingContactDetails(ServiceRequest<NoBody> request)
  throws UnauthorisedException, RuntimeException;
  
  /**
   * Retrieves the pending contact details of the user with the 
   * provided name.
   * 
   * @param request the service request holding the userName 
   * 
   * @return the pending contact details if they exist, null otherwise
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * empty or if the target user does not exist
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public PendingContactDetails getPendingContactDetails(ServiceRequest<String> request)
  throws IllegalArgumentException, RuntimeException;
  
  /**
   * Accepts the pending contact details of the user with the 
   * provided name.
   * 
   * @param request the service request holding the userName 
   * 
   * @return the accepted contact details
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * empty, or if the target user does not exist, or if the pending contact 
   * details do not exist
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public ContactDetails acceptPendingContactDetails(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Rejects the pending contact details of the user with the 
   * provided name.
   * 
   * @param request the service request holding the userName 
   * 
   * @return the accepted contact details if they exist, null otherwise
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * empty, or if the target user does not exist, or if the pending contact 
   * details do not exist
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public ContactDetails rejectPendingContactDetails(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
