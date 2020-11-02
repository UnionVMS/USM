package eu.europa.ec.mare.usm.administration.service.policy.impl;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Properties;

/**
 * Validates Policy related service requests.
 */
@Stateless
public class PolicyValidator extends RequestValidator {
    private static final int MAX_VALUE_LEN = 128;

    @Inject
    private PolicyJdbcDao policyJdbcDao;

    /**
     * Asserts that the provided service request is valid and that the service
     * requester is allowed to use the 'configurePolicies' feature.
     *
     * @param request the service request to be validated
     * @throws IllegalArgumentException if the service request is null, empty,
     *                                  incomplete or otherwise invalid
     * @throws UnauthorisedException    if the service requester is not allowed to
     *                                  use the 'configurePolicies' feature
     */
    public void assertValid(ServiceRequest<PolicyDefinition> request)
            throws IllegalArgumentException, UnauthorisedException {
        assertValid(request, USMFeature.configurePolicies, "policy");
        assertNotEmpty("subject", request.getBody().getSubject());
        Properties props = request.getBody().getProperties();

        assertNotNull("properties", props);
        for (String key : props.stringPropertyNames()) {
            String loc = "property[" + key + "]";
            assertNotNull(loc, props.getProperty(key));
            assertNotTooLong(loc, MAX_VALUE_LEN, props.getProperty(key));
        }
    }

    public void assertValidPolicyProperty(ServiceRequest<Policy> request)
            throws IllegalArgumentException, UnauthorisedException {
        assertValid(request, USMFeature.configurePolicies, "policy");
        assertNotEmpty("name", request.getBody().getName());
        String name = request.getBody().getName();
        String value = request.getBody().getValue();
        String loc = "property[" + name + "]";
        assertNotNull(loc, value);
        assertNotTooLong(loc, MAX_VALUE_LEN, value);
        assertInList("subject", policyJdbcDao.getSubjects(), request.getBody().getSubject());
    }
}
