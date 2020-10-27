package eu.europa.ec.mare.usm.administration.service.application.impl;

import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Application
 * related service requests
 */
@Stateless
public class ApplicationValidator extends RequestValidator {

    public ApplicationValidator() {
    }

    public void assertValid(ServiceRequest<Application> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "application");
    }

}
