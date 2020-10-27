package eu.europa.ec.mare.usm.administration.service.scope.impl;

import eu.europa.ec.mare.usm.administration.domain.Scope;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Scope related
 * service requests
 */
@Stateless
public class ScopeValidator extends RequestValidator {

    public ScopeValidator() {
    }

    public void assertValid(ServiceRequest<Scope> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "scope");

        Scope scope = request.getBody();
        assertNotEmpty("scopeName", scope.getName());
        assertNotEmpty("status", scope.getStatus());
        assertNotNull("activeTo", scope.getActiveTo());
        assertNotNull("activeFrom", scope.getActiveFrom());
        assertValidPeriod("active", scope.getActiveFrom(), scope.getActiveTo());
        assertValidPeriod("data", scope.getDataFrom(), scope.getDataTo());
        if (!isCreate) {
            assertNotNull("scopeId", scope.getScopeId());
        }
    }
}
