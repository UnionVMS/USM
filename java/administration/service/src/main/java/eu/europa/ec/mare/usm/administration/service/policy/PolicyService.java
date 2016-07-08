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
package eu.europa.ec.mare.usm.administration.service.policy;

import java.util.List;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for the management of policy properties and for view policy properties.
 */
public interface PolicyService {
	  
  /**
   * Updates an existing policy property value. 
   *
   * @param request holds the details of the policy property to be updated
   * 
   * @return the updated policy property
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
  public Policy updatePolicy(ServiceRequest<Policy> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;	  

  /**
   * Retrieves a list of policy properties according to the request
   * 
   * @param request a FindPolicyQuery request.
   * 
   * @return List of policy properties
   *
   * 
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requestor is not 
   * authorised to view user information
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
 public List<Policy> findPolicies(ServiceRequest<FindPoliciesQuery> request)
 throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Gets the list of all subjects.
   *
   * @param request holds the identity of the service requester
   * 
   * @return the possibly empty list of all subjects
   * Retrieves the list of existing subjects 
   *  
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requestor is not 
   * authorised to view user information
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public List<String> getSubjects(ServiceRequest<NoBody> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;		  
}