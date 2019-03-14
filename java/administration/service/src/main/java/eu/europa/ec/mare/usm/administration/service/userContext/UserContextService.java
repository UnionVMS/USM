package eu.europa.ec.mare.usm.administration.service.userContext;

import eu.europa.ec.mare.usm.administration.domain.FindUserContextsQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserContext;
import eu.europa.ec.mare.usm.administration.domain.UserContextResponse;

/**
 * Provides operations for user roles
 */
public interface UserContextService {
	/**
	   * Retrieves a list of user roles according to the request
	   * 
	   * @param request a FindUserContextsQuery request.
	   * 
	   * @return UserContextResponse
	   * 
	   * @throws IllegalArgumentException in case the provided input is null, empty
	   * or otherwise incomplete
	   * @throws UnauthorisedException in case the service requestor is not 
	   * authorised to view user information
	   * @throws RuntimeException in case an internal error prevented fulfilling 
	   * the request
	   */
  public UserContextResponse getUserContexts(ServiceRequest<FindUserContextsQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Creates a new user role.
   *
   * @param request contains the details for the new user role
   *
   * @return the user role was successfully created, <i>null</i>
   * otherwise
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete or if the provided application, name, status are requested and
   * the pair (name, application) must be unique
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public UserContext createUserContext(ServiceRequest<UserContext> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Updates an existing user role.
   *
   * @param request holds the details of the user role to be updated
   * 
   * @return the update user role, what else?
   * 
   * @throws IllegalArgumentException in case the service request is null or 
   * incomplete
   * 
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * 
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public UserContext updateUserContext(ServiceRequest<UserContext> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Deletes a user role based on his identifier
   *
   * @param request the identifier of the user role which must be deleted
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void deleteUserContext(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  
  /**
   * Copy all profiles from one user to another
   * @param request 
   * @throws IllegalArgumentException
   * @throws UnauthorisedException
   * @throws RuntimeException
   */
  public void copyUserProfiles(ServiceRequest<UserContextResponse> request, String toUserName)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}