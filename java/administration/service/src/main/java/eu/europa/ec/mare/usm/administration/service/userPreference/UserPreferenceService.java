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