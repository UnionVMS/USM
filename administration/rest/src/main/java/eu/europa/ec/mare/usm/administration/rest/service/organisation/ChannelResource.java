package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.Channel;
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
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("channel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChannelResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelResource.class);

    @EJB
    private OrganisationService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Retrieves a communication channel according to the provided channelId
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param channelId the requested channel id
     * @return an OK status and the requested channel, or a BAD_REQUEST error code
     * in case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("{channelId}")
    public Response getChannel(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               @PathParam("channelId") Long channelId) {
        LOGGER.info("getChannel() - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(channelId);

        Response ret;
        try {
            Channel channel = service.getChannel(request);

            Response.Status status;
            if (channel == null) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(channel).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getChannel() - (LEAVE)");
        return ret;
    }

    /**
     * Creates a new channel
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param channel   the new channel to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response createChannel(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  Channel channel) {
        LOGGER.info("createChannel() - (ENTER)");

        ServiceRequest<Channel> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setBody(channel);
        Response ret;

        try {
            Channel createdchannel = service.createChannel(request);

            Response.Status status;
            if (createdchannel.getEndpointId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(createdchannel).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("createChannel() - (LEAVE)");
        return ret;
    }

    /**
     * Updates an existing channel
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param channel   the channel to be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    public Response updateChannel(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  Channel channel) {
        LOGGER.info("updateChannel() - (ENTER)");

        ServiceRequest<Channel> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(channel);
        Response ret;

        try {
            Channel updatedChannel = service.updateChannel(request);

            Response.Status status = Response.Status.OK;
            ret = Response.status(status).entity(updatedChannel).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateChannel() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing channel
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param channelId the identifier of the channel to be deleted
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @DELETE
    @Path("{channelId}")
    public Response deleteChannel(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  @PathParam("channelId") String channelId) {
        LOGGER.info("deleteChannel(" + channelId + ") - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(Long.valueOf(channelId));

        Response ret;

        try {
            service.deleteChannel(request);
            Response.Status status = Response.Status.OK;
            ret = Response.status(status).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.info("deleteChannel() - (ENTER)");

        return ret;
    }
}
