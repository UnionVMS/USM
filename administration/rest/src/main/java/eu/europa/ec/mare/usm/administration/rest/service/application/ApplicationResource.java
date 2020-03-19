package eu.europa.ec.mare.usm.administration.rest.service.application;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.application.ApplicationService;
import eu.europa.ec.mare.usm.information.service.DeploymentService;
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
@Path("applications")
@Produces(MediaType.APPLICATION_JSON)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApplicationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);

    @EJB
    private ApplicationService service;

    @EJB
    private DeploymentService deploymentService;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Retrieves a list of applications that meet the provided criteria
     *
     * @param jwtToken      the JWT token identifying the service requester, optional
     *                      if the service requester is authenticated by the J2EE container
     * @param roleName      the name of the Role of the selected UserContext (optional)
     * @param scopeName     the name of the Scope of the selected UserContext (optional)
     * @param offset        the pagination offset
     * @param limit         the pagination limit
     * @param sortColumn    the sort Column
     * @param sortDirection the sort Direction
     * @param name          the requested name of the application
     * @param parent        the name of the parent application
     * @return an OK status and the list of applications that meet the criteria,
     * or a BAD_REQUEST error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    public Response findApplications(@HeaderParam("authorization") String jwtToken,
                                     @HeaderParam("roleName") String roleName,
                                     @HeaderParam("scopeName") String scopeName,
                                     @DefaultValue("0") @QueryParam("offset") int offset,
                                     @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
                                     @DefaultValue("name") @QueryParam("sortColumn") String sortColumn,
                                     @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
                                     @QueryParam("name") String name,
                                     @QueryParam("parent") String parent) {
        LOGGER.debug("findApplications() - (ENTER)");

        // Setup paginator
        Paginator paginator = new Paginator();
        paginator.setOffset(offset);
        paginator.setLimit(limit);
        paginator.setSortColumn(sortColumn);
        paginator.setSortDirection(sortDirection);

        ServiceRequest<FindApplicationQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindApplicationQuery applicationQuery = new FindApplicationQuery();
        applicationQuery.setName((!"".equals(name)) ? name : null);
        applicationQuery.setParentName((!"".equals(parent)) ? parent : null);
        applicationQuery.setPaginator(paginator);
        request.setBody(applicationQuery);

        Response ret;
        try {
            PaginationResponse<Application> pResponse = service.findApplications(request);

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

        LOGGER.debug("findApplications() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("{applicationName}")
    public Response getApplicationDetails(@HeaderParam("authorization") String jwtToken,
                                          @HeaderParam("roleName") String roleName,
                                          @HeaderParam("scopeName") String scopeName,
                                          @PathParam("applicationName") String applicationName) {
        LOGGER.debug("getApplicationDetails(" + applicationName + ") - (ENTER)");

        Response ret;

        try {
            ServiceRequest<String> request = new ServiceRequest<>();
            request.setRequester(servletRequest.getRemoteUser());
            request.setRoleName(roleName);
            request.setScopeName(scopeName);
            request.setBody(applicationName);

            eu.europa.ec.mare.usm.information.domain.deployment.Application dd = deploymentService.getDeploymentDescriptor(applicationName);

            if (dd != null) {
                ret = Response.ok(dd).build();
            } else {
                ret = Response.noContent().build();
            }

        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getApplicationDetails() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("parent/names")
    public Response getParentApplicationNames(@HeaderParam("authorization") String jwtToken,
                                              @HeaderParam("roleName") String roleName,
                                              @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getParentApplicationNames() - (ENTER)");

        ServiceRequest<GetParentApplicationQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(new GetParentApplicationQuery());

        Response ret;
        try {
            List<String> lst = service.getParentApplicationNames(request);
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

        LOGGER.debug("getParentApplicationNames() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("names")
    public Response getApplicationNames(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getApplicationNames() - (ENTER)");

        Response ret;
        ServiceRequest<ApplicationQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(new ApplicationQuery());

        try {
            List<String> lst = service.getApplicationNames(request);
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

        LOGGER.debug("getApplicationNames() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("{applicationName}/features")
    public Response getApplicationFeatures(@HeaderParam("authorization") String jwtToken,
                                           @HeaderParam("roleName") String roleName,
                                           @HeaderParam("scopeName") String scopeName,
                                           @PathParam("applicationName") String applicationName) {
        LOGGER.debug("getApplicationFeatures() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(applicationName);

        Response ret;
        try {
            List<Feature> lst = service.getFeatureApplicationNames(request);
            Status status;
            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<Feature> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getApplicationFeatures() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("features")
    public Response getAllFeatures(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getAllFeatures() - (ENTER)");

        Response ret;
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody("");

        try {
            List<Feature> lst = service.getAllFeatures(request);
            Status status;
            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<Feature> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getAllFeatures() - (LEAVE)");
        return ret;
    }

}
