package eu.europa.ec.mare.usm.administration.service.ldap.impl;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of LDAP
 * related service requests
 */
public class LDAPValidator extends RequestValidator {

    public LDAPValidator() {
    }

    public void assertValid(ServiceRequest<GetUserQuery> request) {
        assertValid(request, USMFeature.manageUsers, "query");
        String userName = request.getBody().getUserName();
        assertNotEmpty("userName", userName);
    }

}
