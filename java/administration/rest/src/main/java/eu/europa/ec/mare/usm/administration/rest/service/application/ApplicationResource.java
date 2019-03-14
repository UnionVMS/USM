package eu.europa.ec.mare.usm.administration.rest.service.application;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.ApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.GetParentApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.application.ApplicationService;
import eu.europa.ec.mare.usm.information.service.DeploymentService;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("applications")
public class ApplicationResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);

  @EJB
  ApplicationService service;

  @EJB
  DeploymentService deploymentService;

  @Context
  private HttpServletRequest servletRequest;

  /**
   * Retrieves a list of applications that meet the provided criteria
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param offset the pagination offset
   * @param limit the pagination limit
   * @param sortColumn the sort Column
   * @param sortDirection the sort Direction
   * @param name the requested name of the application
   * @param parent the name of the parent application
   *
   * @return an OK status and the list of applications that meet the criteria,
   * or a BAD_REQUEST error code in case the provided input incomplete, with an
   * INTERNAL_SERVER_ERROR error code in case an internal error prevented
   * fulfilling the request or UnauthorisedException with an FORBIDDEN error
   * code in case the end user is not authorised to perform the operation
   */
  @GET
  @Produces("application/json")
  public Response findApplications(@HeaderParam("authorization") String jwtToken,
          @HeaderParam("roleName") String roleName,
          @HeaderParam("scopeName") String scopeName,
          @DefaultValue("0") @QueryParam("offset") int offset,
          @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
          @DefaultValue("name") @QueryParam("sortColumn") String sortColumn,
          @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
          @QueryParam("name") String name,
          @QueryParam("parent") String parent) 
  {
    LOGGER.info("findApplications() - (ENTER)");

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

    LOGGER.info("findApplications() - (LEAVE)");
    return ret;
  }

  @GET
  @Path("{applicationName}")
  @Produces("application/json")
  public Response getApplicationDetails(@HeaderParam("authorization") String jwtToken,
                                           @HeaderParam("roleName") String roleName,
                                           @HeaderParam("scopeName") String scopeName,
                                           @PathParam("applicationName") String applicationName) 
  {
    LOGGER.info("getApplicationDetails(" + applicationName + ") - (ENTER)");

    Response ret;

    try {
      ServiceRequest<String> request = new ServiceRequest<>();
      request.setRequester(servletRequest.getRemoteUser());
      request.setRoleName(roleName);
      request.setScopeName(scopeName);
      request.setBody(applicationName);

      eu.europa.ec.mare.usm.information.domain.deployment.Application dd = deploymentService.getDeploymentDescriptor(applicationName);

      if (dd != null) {
        ret = Response.status(Response.Status.OK).
                entity(dd).
                build();
      } else {
        ret = Response.status(Response.Status.NO_CONTENT).build();
      }

    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getApplicationDetails() - (LEAVE)");
    return ret;
  }

  @GET
  @Path("parent/names")
  @Produces("application/json")
  public Response getParentApplicationNames(@HeaderParam("authorization") String jwtToken,
                                                @HeaderParam("roleName") String roleName,
                                                @HeaderParam("scopeName") String scopeName) 
  {
    LOGGER.info("getParentApplicationNames() - (ENTER)");

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

    LOGGER.info("getParentApplicationNames() - (LEAVE)");
    return ret;
  }

  @GET
  @Path("names")
  @Produces("application/json")
  public Response getApplicationNames(@HeaderParam("authorization") String jwtToken,
                                         @HeaderParam("roleName") String roleName,
                                         @HeaderParam("scopeName") String scopeName) 
  {
    LOGGER.info("getApplicationNames() - (ENTER)");

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

    LOGGER.info("getApplicationNames() - (LEAVE)");
    return ret;
  }

  @GET
  @Path("{applicationName}/features")
  @Produces("application/json")
  public Response getApplicationFeatures(@HeaderParam("authorization") String jwtToken,
                                            @HeaderParam("roleName") String roleName,
                                            @HeaderParam("scopeName") String scopeName,
                                            @PathParam("applicationName") String applicationName) 
  {
    LOGGER.info("getApplicationFeatures() - (ENTER)");

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

    LOGGER.info("getApplicationFeatures() - (LEAVE)");
    return ret;
  }

  @GET
  @Path("features")
  @Produces("application/json")
  public Response getAllfeatures(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName) 
  {
    LOGGER.info("getAllfeatures() - (ENTER)");

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

    LOGGER.info("getAllfeatures() - (LEAVE)");
    return ret;
  }

}
