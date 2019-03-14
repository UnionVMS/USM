package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.organisation.OrganisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("endpoint")
public class EndpointResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(EndpointResource.class);

  @EJB
  OrganisationService service;

  @Context
  private HttpServletRequest servletRequest;

  public EndpointResource() {
  }

  /**
   * Creates a new endpoint
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endpoint the new endpoint to be created
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @POST
  @Consumes("application/json")
  @Produces("application/json")
  public Response createEndpoint(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  EndPoint endpoint) 
  {
    LOGGER.info("createEndpoint() - (ENTER)");

    ServiceRequest<EndPoint> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(endpoint);
    Response ret;

    try {
      EndPoint createdEndpoint = service.createEndPoint(request);

      Response.Status status;
      if (createdEndpoint.getEndpointId() != null) {
        status = Response.Status.OK;
      } else {
        status = Response.Status.NOT_MODIFIED;
      }
      ret = Response.status(status).entity(createdEndpoint).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("createEndpoint() - (LEAVE)");
    return ret;
  }

  /**
   * Updates an existing endpoint
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endpoint the endpoint to be updated
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @PUT
  @Consumes("application/json")
  @Produces("application/json")
  public Response updateEndpoint(@HeaderParam("authorization") String jwtToken,
                                    @HeaderParam("roleName") String roleName,
                                    @HeaderParam("scopeName") String scopeName,
                                    EndPoint endpoint) 
  {
    LOGGER.info("updateEndpoint() - (ENTER)");

    ServiceRequest<EndPoint> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(endpoint);

    Response ret;

    try {
      EndPoint updatedEndpoint = service.updateEndPoint(request);

      Response.Status status = Response.Status.OK;
      ret = Response.status(status).entity(updatedEndpoint).build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("updateEndpoint() - (LEAVE)");
    return ret;
  }

  /**
   * Deletes an existing endpoint
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endpointId the identifier of the endpoint to be deleted
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @DELETE
  @Path("{endpointId}")
  @Produces("application/json")
  public Response deleteEndpoint(@HeaderParam("authorization") String jwtToken,
                                    @HeaderParam("roleName") String roleName,
                                    @HeaderParam("scopeName") String scopeName,
                                    @PathParam("endpointId") String endpointId) 
  {
    LOGGER.info("deleteEndpoint(" + endpointId + ") - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(Long.valueOf(endpointId));

    Response ret;

    try {
      service.deleteEndPoint(request);
      Response.Status status = Response.Status.OK;
      ret = Response.status(status).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.info("deleteEndpoint() - (ENTER)");

    return ret;
  }
}
