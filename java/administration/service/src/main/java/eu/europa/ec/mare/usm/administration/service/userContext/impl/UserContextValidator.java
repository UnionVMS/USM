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
package eu.europa.ec.mare.usm.administration.service.userContext.impl;

import eu.europa.ec.mare.usm.administration.domain.ComprehensiveUserContext;
import eu.europa.ec.mare.usm.administration.domain.Permission;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UserContext;
import eu.europa.ec.mare.usm.administration.domain.UserContextResponse;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of UserContext 
 * related service requests
 */
public class UserContextValidator extends RequestValidator {

	/**
	 * Creates a new instance.
	 */
	public UserContextValidator() {
	}

	public void assertValid(ServiceRequest<UserContext> request,
                           USMFeature feature, boolean isCreate) 
  {
		assertValid(request, feature, "user role");
		assertNotEmpty("userName", request.getBody().getUserName());
		assertNotNull("roleId", request.getBody().getRoleId());
		
		if (!isCreate){
			assertNotNull("userContextId", request.getBody().getRoleId());
		}
  }
	
	public void assertValidPermission(ServiceRequest<Permission> request,
                                       USMFeature feature)
	{
		assertValid(request, feature, "persmission");
		assertNotNull("roleId",request.getBody().getRoleId());
		assertNotNull("featureId",request.getBody().getFeatureId());
	}
	
	public void assertValidCopy(ServiceRequest<UserContextResponse> request, 
                                USMFeature feature, String toUserName)
  {
		assertValid(request, feature, "origin");
    if (request.getBody().getResults() != null) {
      for (ComprehensiveUserContext item : request.getBody().getResults()) {
        assertNotNull("userContextId", item.getUserContextId());
      }
    }

    assertNotEmpty("userName", toUserName);
	}
}