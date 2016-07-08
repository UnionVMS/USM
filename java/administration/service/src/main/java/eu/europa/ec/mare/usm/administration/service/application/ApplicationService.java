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
package eu.europa.ec.mare.usm.administration.service.application;

import java.util.List;

import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.ApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.GetParentApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for the administration of Applications.
 */
public interface ApplicationService {
  /**
   * Retrieves the name of the applications matching the provided query.
   * 
   * @param request the service request holding the Application query
   * 
   * @return the possibly-empty list of application names
   * 
   * @throws IllegalArgumentException in case the service request is null or 
   * incomplete
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public List<String> getApplicationNames(ServiceRequest<ApplicationQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  
  /**
   * Retrieves the features of a specific application.
   * 
   * @param request the service request holding the name of the given application
   * 
   * @return the possibly-empty list of features
   * 
   * @throws IllegalArgumentException in case the service request is null or 
   * incomplete
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public List<Feature> getFeatureApplicationNames(ServiceRequest<String> request)
		  throws IllegalArgumentException, UnauthorisedException, RuntimeException;


	/**
	 * Gets the all features.
	 *
	 * @param request the request
	 * @return the all features
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnauthorisedException the unauthorised exception
	 * @throws RuntimeException the runtime exception
	 */
	public List<Feature> getAllFeatures(ServiceRequest<String> request)
			throws IllegalArgumentException, UnauthorisedException,
			RuntimeException;
		  
	/**
	   * Retrieves the applications matching the provided query
	   * 
	   * @param request the service request holding the Application query
	   * 
	   * @return the possibly-empty list of applications
	   * 
	   * @throws IllegalArgumentException in case the service request is null or 
	   * incomplete
	   * @throws UnauthorisedException in case the service requester is not 
	   * authorised to use the specific feature
	   * @throws RuntimeException in case an internal problem prevents processing 
	   * the request
	   */
	public PaginationResponse<Application> findApplications(ServiceRequest<FindApplicationQuery> request)
			throws IllegalArgumentException, UnauthorisedException, RuntimeException;

	/**
	   * Retrieves a list of application's parent names
	   * 
	   * @param request the service request holding the Application's parent query
	   * 
	   * @return the possibly-empty list of application names
	   * 
	   * @throws IllegalArgumentException in case the service request is null or 
	   * incomplete
	   * @throws UnauthorisedException in case the service requester is not 
	   * authorised to use the specific feature
	   * @throws RuntimeException in case an internal problem prevents processing 
	   * the request
	   */
	  public List<String> getParentApplicationNames(ServiceRequest<GetParentApplicationQuery> request)
	  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}