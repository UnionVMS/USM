package eu.europa.ec.mare.usm.administration.service.userPreference;

import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;

/**
 * Provides operations for user preferences
 */
public interface UserPreferenceService {
	/**
	   * Retrieves a list of all or part of user preferences according to the request.
	   * If group name is provided then the service should return only the user's preferences
	   * that correspond to the provided group name otherwise should return all user's
	   * preferences.
	   * 
	   * @param request a FindUserPreferenceQuery request.
	   * 
	   * @return UserPreferenceResponse
	   * 			a list of all or filtered user preferences
	   * 
	   * @throws IllegalArgumentException in case the provided input is null, empty
	   * or otherwise incomplete
	   * @throws UnauthorisedException in case the service requester is not 
	   * authorised to view user preferences
	   * @throws RuntimeException in case an internal error prevented fulfilling 
	   * the request
	   */
  public UserPreferenceResponse getUserPrefernces(ServiceRequest<FindUserPreferenceQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
}