package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.*;

import java.util.List;

/**
 * Provides operations for the management of policy properties and for view policy properties.
 */
public interface PolicyService {

    /**
     * Updates an existing policy property value.
     *
     * @param request holds the details of the policy property to be updated
     * @return the updated policy property
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public Policy updatePolicy(ServiceRequest<Policy> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of policy properties according to the request
     *
     * @param request a FindPolicyQuery request.
     * @return List of policy properties
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requestor is not
     *                                  authorised to view user information
     * @throws RuntimeException         in case an internal error prevented fulfilling
     *                                  the request
     */
    public List<Policy> findPolicies(ServiceRequest<FindPoliciesQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Gets the list of all subjects.
     *
     * @param request holds the identity of the service requester
     * @return the possibly empty list of all subjects
     * Retrieves the list of existing subjects
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requestor is not
     *                                  authorised to view user information
     * @throws RuntimeException         in case an internal error prevented fulfilling
     *                                  the request
     */
    public List<String> getSubjects(ServiceRequest<NoBody> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
