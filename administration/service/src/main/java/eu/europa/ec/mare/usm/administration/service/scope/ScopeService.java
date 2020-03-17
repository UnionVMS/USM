package eu.europa.ec.mare.usm.administration.service.scope;

import eu.europa.ec.mare.usm.administration.domain.*;

import java.util.List;

/**
 * Provides operation for the administration of Scopes
 */
public interface ScopeService {

    /**
     * Retrieves a specific scope.
     *
     * @param request the service request holding the get scope query
     * @return a specific scope
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to view scope information
     * @throws RuntimeException         in case an internal error prevented fulfilling the request
     */
    public Scope getScope(ServiceRequest<GetScopeQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of scopes according to the request criteria
     *
     * @param request the service request holding the find scopes query
     * @return PaginationResponse response which includes a list of Scopes and the
     * total value
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to view scope information
     * @throws RuntimeException         in case an internal error prevented fulfilling the
     *                                  request
     */
    public PaginationResponse<Scope> findScopes(ServiceRequest<FindScopesQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Creates a new Scope.
     *
     * @param request contains the details for the new scope
     * @return the scope was successfully created, <i>null</i>
     * otherwise
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete or if the provided application, name, status are requested and
     *                                  the pair (name, application) must be unique
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public Scope createScope(ServiceRequest<Scope> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Updates an existing Scope.
     *
     * @param request contains the details for the updated scope
     * @return the scope was successfully updated, <i>null</i>
     * otherwise
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete or if the provided application, name, status are requested and
     *                                  the pair (name, application) must be unique
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public Scope updateScope(ServiceRequest<Scope> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Deletes an existing scope.
     *
     * @param request contains the details for the deleted scope
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public void deleteScope(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves all datasets filtered by application and category.
     *
     * @param request contains the application and category used to filter the
     *                datasets
     * @return the list of the found dataset, <i>null</i> otherwise
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public List<DataSet> findDataSet(ServiceRequest<FindDataSetQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Gets the category names.
     *
     * @param request the request
     * @return the category names
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public List<String> getCategoryNames(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of DataSets
     *
     * @param request the request
     * @return the matching datasets
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public List<DataSet> findDataSets(ServiceRequest<FindDataSetQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of comprehensive scopes
     *
     * @param request the service request holding the Scope query
     * @return the possibly-empty list of comprehensive scopes
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public List<ComprehensiveScope> getScopes(ServiceRequest<ScopeQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
