package eu.europa.ec.mare.usm.administration.service.application;

import eu.europa.ec.mare.usm.administration.domain.*;

import java.util.List;

/**
 * Provides operations for the administration of Applications.
 */
public interface ApplicationService {
    /**
     * Retrieves the name of the applications matching the provided query.
     *
     * @param request the service request holding the Application query
     * @return the possibly-empty list of application names
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public List<String> getApplicationNames(ServiceRequest<ApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves the features of a specific application.
     *
     * @param request the service request holding the name of the given application
     * @return the possibly-empty list of features
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public List<Feature> getFeatureApplicationNames(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Gets the all features.
     *
     * @param request the request
     * @return the all features
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnauthorisedException    the unauthorised exception
     * @throws RuntimeException         the runtime exception
     */
    public List<Feature> getAllFeatures(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves the applications matching the provided query
     *
     * @param request the service request holding the Application query
     * @return the possibly-empty list of applications
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public PaginationResponse<Application> findApplications(ServiceRequest<FindApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of application's parent names
     *
     * @param request the service request holding the Application's parent query
     * @return the possibly-empty list of application names
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public List<String> getParentApplicationNames(ServiceRequest<GetParentApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
