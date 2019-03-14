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
