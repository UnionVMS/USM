package eu.europa.ec.mare.usm.session.rest.service;

import eu.europa.ec.mare.usm.authentication.rest.ExceptionHandler;
import eu.europa.ec.mare.usm.session.domain.SessionIdWrapper;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.session.service.SessionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionTrackerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTrackerResource.class);

    @EJB
    private SessionTracker service;

    /**
     * Informs the tracker that a (new) user session is starting.
     *
     * @param request the identification of the starting user session
     * @return the unique identifier of the starting user session with an OK
     * status, or a BAD_REQUEST status if the provided service request
     * is invalid; or an INTERNAL_SERVER_ERROR status in case an
     * internal error prevented processing the request.
     */
    @POST
    @Path("")
    public Response startSession(SessionInfo request) {
        LOGGER.info("startSession() - (ENTER)");

        Response response;
        try {
            String sessionId = service.startSession(request);
            if (sessionId != null) {
                SessionIdWrapper wrapper = new SessionIdWrapper();
                wrapper.setSessionId(sessionId);
                response = Response.ok(wrapper).type(MediaType.APPLICATION_JSON).build();
            } else {
                throw new IllegalStateException("Maximum number of sessions exceeded");
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("startSession() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves (identification) information about the user session with the
     * provided unique identifier.
     *
     * @param sessionId the unique identifier of the terminating user session
     * @return the session identification information with an OK status if the
     * session (still) exists; or a NOT_FOUND status if the session
     * (no-longer) exists; or a BAD_REQUEST status if the provided
     * service request is invalid; or an INTERNAL_SERVER_ERROR status in
     * case an internal error prevented processing the request.
     */
    @GET
    @Path("{sessionId}")
    public Response getSession(@PathParam("sessionId") String sessionId) {
        LOGGER.info("getSession() - (ENTER)");

        Response response;
        try {
            SessionInfo sessionInfo = service.getSession(sessionId);
            if (sessionInfo != null) {
                response = Response.ok(sessionInfo).type(MediaType.APPLICATION_JSON).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getSession() - (LEAVE)");
        return response;
    }

    /**
     * Informs the tracker that a user session is terminating.
     *
     * @param sessionId the unique identifier of the terminating user session
     * @return an empty response with OK status, or a BAD_REQUEST status if the
     * provided service request is invalid; or an INTERNAL_SERVER_ERROR
     * status in case an internal error prevented processing the
     * request.
     */
    @DELETE
    @Path("{sessionId}")
    public Response endSession(@PathParam("sessionId") String sessionId) {
        LOGGER.info("endSession() - (ENTER)");

        Response response;
        try {
            service.endSession(sessionId);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("endSession() - (LEAVE)");
        return response;
    }
}
