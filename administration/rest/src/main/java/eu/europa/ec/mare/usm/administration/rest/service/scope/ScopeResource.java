package eu.europa.ec.mare.usm.administration.rest.service.scope;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.scope.ScopeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("scopes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ScopeResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeResource.class);
    private static final String ALL = "all";

    @EJB
    private ScopeService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Creates a new scope
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param scope     the new scope to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response createScope(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                Scope scope) {
        LOGGER.info("createScope() - (ENTER)");

        ServiceRequest<Scope> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(scope);

        Response ret;
        try {
            Scope createdScope = service.createScope(request);

            Status status;
            if (createdScope.getScopeId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(createdScope).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("createScope() - (LEAVE)");
        return ret;
    }

    /**
     * Finds the data set category names
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the requested information with a status OK in case of success
     * otherwise status NOT_MODIFIED or a BAD_REQUEST error code in case the
     * provided input incomplete, with an INTERNAL_SERVER_ERROR error code in case
     * an internal error prevented fulfilling the request or a FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("datasets/category/names")
    public Response getDatasetCategoryNames(@HeaderParam("authorization") String jwtToken,
                                            @HeaderParam("roleName") String roleName,
                                            @HeaderParam("scopeName") String scopeName) {
        LOGGER.info("getDatasetCategoryNames() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody("");

        Response ret;
        try {
            List<String> lst = service.getCategoryNames(request);

            Status status;
            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getDatasetCategoryNames() - (LEAVE)");
        return ret;
    }

    /**
     * Finds the datasets matching the provided request.
     *
     * @param jwtToken    the JWT token identifying the service requester, optional
     *                    if the service requester is authenticated by the J2EE container
     * @param roleName    the name of the Role of the selected UserContext (optional)
     * @param scopeName   the name of the Scope of the selected UserContext
     *                    (optional)
     * @param application the application name
     * @param category    the category name
     * @return the requested information with a status OK in case of success
     * otherwise status NOT_MODIFIED or a BAD_REQUEST error code in case the
     * provided input incomplete, with an INTERNAL_SERVER_ERROR error code in case
     * an internal error prevented fulfilling the request or a FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("datasets")
    public Response findDatasets(@HeaderParam("authorization") String jwtToken,
                                 @HeaderParam("roleName") String roleName,
                                 @HeaderParam("scopeName") String scopeName,
                                 @QueryParam("application") String application,
                                 @QueryParam("category") String category) {
        LOGGER.info("findDatasets() - (ENTER)");

        ServiceRequest<FindDataSetQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindDataSetQuery findDataSetQuery = new FindDataSetQuery();
        findDataSetQuery.setApplicationName((application != null && !ALL.equals(application)) ? application : null);
        findDataSetQuery.setCategory((category != null && !ALL.equals(category)) ? category : null);
        request.setBody(findDataSetQuery);

        Response ret;
        try {
            List<DataSet> datasets = service.findDataSet(request);

            Status status;
            ServiceArrayResponse<DataSet> sar = null;
            if (datasets == null || datasets.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
                sar = new ServiceArrayResponse<>();
                sar.setResults(datasets);
            }

            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("findDatasets() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a list of scopes that meet the provided criteria
     *
     * @param jwtToken        the JWT token identifying the service requester, optional
     *                        if the service requester is authenticated by the J2EE container
     * @param roleName        the name of the Role of the selected UserContext (optional)
     * @param scopeName       the name of the Scope of the selected UserContext
     *                        (optional)
     * @param offset          the pagination offset
     * @param limit           the pagination limit
     * @param sortColumn      the sort Column
     * @param sortDirection   the sort Direction
     * @param nameOfScope     the requested scope
     * @param applicationName the requested application
     * @param status          the requested status of the scope
     * @return an OK status and the list of scopes that meet the criteria, or a
     * BAD_REQUEST error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    public Response findScopes(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               @DefaultValue("0") @QueryParam("offset") int offset,
                               @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
                               @DefaultValue("name") @QueryParam("sortColumn") String sortColumn,
                               @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
                               @QueryParam("name") String nameOfScope,
                               @QueryParam("application") String applicationName,
                               @QueryParam("status") String status) {
        LOGGER.info("findScopes() - (ENTER)");

        // Setup paginator
        Paginator paginator = new Paginator();
        paginator.setOffset(offset);
        paginator.setLimit(limit);
        paginator.setSortColumn(sortColumn);
        paginator.setSortDirection(sortDirection);

        ServiceRequest<FindScopesQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindScopesQuery findScopesQuery = new FindScopesQuery();
        findScopesQuery.setScopeName((!"".equals(nameOfScope)) ? nameOfScope : null);
        findScopesQuery.setApplicationName((!"".equals(applicationName)) ? applicationName : null);
        findScopesQuery.setStatus((status != null && !ALL.equals(status)) ? status : null);
        findScopesQuery.setPaginator(paginator);
        request.setBody(findScopesQuery);

        Response ret;
        try {
            PaginationResponse<Scope> pResponse = service.findScopes(request);

            Status statusCode;
            if (pResponse == null || pResponse.getResults().isEmpty()) {
                statusCode = Response.Status.NO_CONTENT;
            } else {
                statusCode = Response.Status.OK;
            }

            ret = Response.status(statusCode).entity(pResponse).build();

        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("findScopes() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a scope according to the provided scopeId
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param scopeId   the requested scope id
     * @return an OK status and the requested scope, or a BAD_REQUEST error code
     * in case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("{scopeId}")
    public Response getScope(@HeaderParam("authorization") String jwtToken,
                             @HeaderParam("roleName") String roleName,
                             @HeaderParam("scopeName") String scopeName,
                             @PathParam("scopeId") Long scopeId) {
        LOGGER.info("getScope() - (ENTER)");

        ServiceRequest<GetScopeQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        GetScopeQuery getScopeQuery = new GetScopeQuery();
        getScopeQuery.setScopeId(scopeId);
        request.setBody(getScopeQuery);

        Response ret;
        try {
            Scope scope = service.getScope(request);

            Status status;
            if (scope == null) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(scope).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getScope() - (LEAVE)");
        return ret;
    }

    /**
     * Updates an existing scope
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param scope     the scope to be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    public Response updateScope(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                Scope scope) {
        LOGGER.info("updateScope() - (ENTER)");

        ServiceRequest<Scope> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(scope);

        Response ret;
        try {
            Scope updatedScope = service.updateScope(request);

            Status status = Response.Status.OK;
            ret = Response.status(status).entity(updatedScope).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateScope() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing scope
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param scopeId   the identifier of the scope to be deleted
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @DELETE
    @Path("{scopeId}")
    public Response deleteScope(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                @PathParam("scopeId") Long scopeId) {
        LOGGER.info("deleteScope(" + scopeId + ") - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(scopeId);

        Response ret;
        try {
            service.deleteScope(request);
            Status status = Response.Status.OK;
            ret = Response.status(status).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.info("deleteScope() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the list of scopes (names along with the corresponding id and the
     * status)
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the list of scope names and the corresponding ids with an OK
     * status, or a BAD_REQUEST error code in case the provided input incomplete,
     * with an INTERNAL_SERVER_ERROR error code in case an internal error
     * prevented fulfilling the request or UnauthorisedException with an FORBIDDEN
     * error code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("names")
    public Response getScopes(@HeaderParam("authorization") String jwtToken,
                              @HeaderParam("roleName") String roleName,
                              @HeaderParam("scopeName") String scopeName) {
        LOGGER.info("getScopes() - (ENTER)");

        ServiceRequest<ScopeQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(new ScopeQuery());

        Response ret;
        try {
            List<ComprehensiveScope> lst = service.getScopes(request);
            Status status;

            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<ComprehensiveScope> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getScopes() - (LEAVE)");
        return ret;
    }
}
