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
package eu.europa.ec.mare.usm.administration.service.role.impl;

import eu.europa.ec.mare.usm.administration.domain.ComprehensiveRole;
import eu.europa.ec.mare.usm.administration.domain.Permission;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of Role related
 * service requests
 */
public class RoleValidator extends RequestValidator {

	/**
	 * Creates a new instance.
	 */
	public RoleValidator() {
	}

	public void assertValid(ServiceRequest<ComprehensiveRole> request,
                           USMFeature feature, boolean isCreate) 
  {
		assertValid(request, feature, "role");
		assertNotEmpty("roleName", request.getBody().getName());
		assertNotEmpty("status", request.getBody().getStatus());
		
		if (!isCreate){
			assertNotNull("roleId", request.getBody().getRoleId());
		}
  }

	public void assertValidPermission(ServiceRequest<Permission> request,
                                       USMFeature feature)
	{
		assertValid(request, feature, "persmission");
		assertNotNull("roleId",request.getBody().getRoleId());
		assertNotNull("featureId",request.getBody().getFeatureId());
	}
	
}