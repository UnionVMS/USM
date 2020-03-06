package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("endpoint")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EndpointResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointResource.class);

    @EJB
    private OrganisationService service;

    @Context
    private HttpServletRequest servletRequest;

    public EndpointResource() {
    }

    /**
     * Retrieves an end point according to the provided endPointId
     *
     * @param jwtToken   the JWT token identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @param endPointId the requested end point id
     * @return an OK status and the requested end point, or a BAD_REQUEST error
     * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR
     * error code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("{endPointId}")
    public Response getEndPoint(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                @PathParam("endPointId") Long endPointId) {
        LOGGER.info("getEndPoint() - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(endPointId);

        Response ret;
        try {
            EndPoint endPoint = service.getEndPoint(request);

            Response.Status status;
            if (endPoint == null) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(endPoint).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getEndPoint() - (LEAVE)");
        return ret;
    }

    /**
     * Creates a new endpoint
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param endpoint  the new endpoint to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response createEndpoint(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   EndPoint endpoint) {
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
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param endpoint  the endpoint to be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    public Response updateEndpoint(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   EndPoint endpoint) {
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
     * @param jwtToken   the JWT token identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
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
    public Response deleteEndpoint(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   @PathParam("endpointId") String endpointId) {
        LOGGER.info("deleteEndpoint(" + endpointId + ") - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(Long.valueOf(endpointId));

        Response ret;

        try {
            service.deleteEndPoint(request);
            ret = Response.noContent().build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.info("deleteEndpoint() - (ENTER)");

        return ret;
    }

    /**
     * Retrieves an end point contact according to the provided endPointContactId
     *
     * @param jwtToken          the JWT token identifying the service requester, optional
     *                          if the service requester is authenticated by the J2EE container
     * @param roleName          the name of the Role of the selected UserContext (optional)
     * @param scopeName         the name of the Scope of the selected UserContext
     *                          (optional)
     * @param endPointContactId the requested end point contact id
     * @return an OK status and the requested end point contact, or a BAD_REQUEST
     * error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an UNAUTHORIZED error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("contact/{endPointContactId}")
    public Response getEndPointContact(@HeaderParam("authorization") String jwtToken,
                                       @HeaderParam("roleName") String roleName,
                                       @HeaderParam("scopeName") String scopeName,
                                       @PathParam("endPointContactId") Long endPointContactId) {
        LOGGER.info("getEndPointContact() - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(endPointContactId);

        Response ret;
        try {
            EndPointContact endPointContact = service.getContact(request);

            Response.Status status;
            if (endPointContact == null) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(endPointContact).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getEndPointContact() - (LEAVE)");
        return ret;
    }
}
