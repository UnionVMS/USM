package eu.europa.ec.mare.usm.administration.service;

import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMApplication;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.Feature;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Provides operations for the validation and authorisation of service requests
 */
@Stateless
public class RequestValidator {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);

    @EJB
    private InformationService infoService;

    /**
     * Creates a new instance.
     */
    public RequestValidator() {
    }

    /**
     * Asserts that the provided service request is valid and optionally,
     * that the service requester is allowed to use the specified feature.
     *
     * @param input   the service request to be validated
     * @param feature the optional feature to which the service requester must
     *                have been granted a right to use
     * @throws IllegalArgumentException if the service request is null, empty or
     *                                  incomplete
     * @throws UnauthorisedException    if the service requester is not allowed
     *                                  to use the specified feature
     */
    public void assertValid(ServiceRequest input, USMFeature feature)
            throws IllegalArgumentException, UnauthorisedException {
        assertNotNull("request", input);
        assertNotEmpty("requester", input.getRequester());

        if (feature != null) {
            UserContextQuery query = new UserContextQuery();
            query.setApplicationName(USMApplication.USM.name());
            query.setUserName(input.getRequester());
            UserContext ctx = infoService.getUserContext(query);
            boolean isAuthorised = false;
            if (ctx != null && ctx.getContextSet() != null) {
                for (Context c : ctx.getContextSet().getContexts()) {
                    if (c.getRole() != null &&
                            (input.getRoleName() == null || input.getRoleName().equals(c.getRole().getRoleName())) &&
                            (c.getScope() == null || c.getScope().getScopeName().equals(input.getScopeName()))) {
                        for (Feature f : c.getRole().getFeatures()) {
                            if (USMApplication.USM.name().equals(f.getApplicationName()) &&
                                    feature.name().equals(f.getFeatureName())) {
                                isAuthorised = true;
                                break;
                            }
                        }
                    }
                    if (isAuthorised) {
                        break;
                    }
                }
            }

            if (!isAuthorised) {
                LOGGER.info("User " + input.getRequester() +
                        " is not authorised for " + feature +
                        " using context with role " + input.getRoleName() +
                        " and scope " + input.getScopeName());
                throw new UnauthorisedException("Not authorised");
            }
        }
    }

    /**
     * Asserts that the provided service request is valid and optionally,
     * that the service requester is allowed to use the specified feature.
     *
     * @param input    the service request to be validated
     * @param feature  the optional feature to which the service requester must
     *                 have been granted a right to use
     * @param bodyName the logical name of the service-request body
     * @throws IllegalArgumentException if the service request is null, empty or
     *                                  incomplete
     * @throws UnauthorisedException    if the service requester is not allowed
     *                                  to use the specified feature
     */
    public void assertValid(ServiceRequest input, USMFeature feature, String bodyName)
            throws IllegalArgumentException, UnauthorisedException {
        assertValid(input, feature);
        assertNotNull(bodyName, input.getBody());
    }

    public void assertValid(ServiceRequest input, String bodyName, Set<USMFeature> features)
            throws IllegalArgumentException, UnauthorisedException {
        UnauthorisedException authException = null;
        for (USMFeature usmFeature : features) {
            try {
                assertValid(input, usmFeature);
                return;
            } catch (UnauthorisedException e) {
                // TODO: handle exception
                authException = e;
            }
        }
        if (authException != null) {
            throw authException;
        }
        assertNotNull(bodyName, input.getBody());
    }

    public void assertValidPeriod(String name, Date from, Date to) {
        if (from != null && to != null && to.before(from)) {
            throw new IllegalArgumentException(name + " dates are not in sequence");
        }
    }

    public void assertNotTooLong(String name, int maxLen, String value) {
        if (value != null && value.length() > maxLen) {
            throw new IllegalArgumentException(name + " is too long (max " + maxLen + ")");
        }
    }

    public void assertNotTooShort(String name, int minLen, String value) {
        if (value != null && value.length() < minLen) {
            throw new IllegalArgumentException(name + " is too short (min " + minLen + ")");
        }
    }

    public void assertNotEmpty(String name, String value) {
        assertNotNull(name, value);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    public void assertNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    protected void assertInList(String name, String[] listOfValues, String value) {
      List<String> asList = Arrays.asList(listOfValues);
      assertInList(name, asList, value);
    }

    protected void assertInList(String name, List<String> listOfValues, String value) {
        if (value != null) {
            boolean inList = false;

            for (String v : listOfValues) {
                if (v.equals(value)) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                throw new IllegalArgumentException(name + " (" + value + ") is not supported");
            }
        }
    }

}
