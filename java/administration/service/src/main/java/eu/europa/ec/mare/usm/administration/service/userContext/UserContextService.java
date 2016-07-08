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