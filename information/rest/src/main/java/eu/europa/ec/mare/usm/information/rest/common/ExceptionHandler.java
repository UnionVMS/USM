package eu.europa.ec.mare.usm.information.rest.common;

import eu.europa.ec.mare.usm.information.rest.service.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Logs the provided exception and generates a pertinent HTTP response.
     *
     * @param ex the exception to be handled.
     * @return an HTTP response with a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    public static Response handleException(Exception ex) {
        StatusResponse statusResponse = new StatusResponse();
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        Throwable cause = ex;

        if (ex.getCause() != null) {
            cause = ex.getCause();
        }

        if (cause instanceof IllegalArgumentException) {
            LOGGER.warn("Bad request: " + cause.getMessage());
            statusResponse.setMessage(cause.getMessage());
            status = Response.Status.BAD_REQUEST;
        } else if (cause instanceof RuntimeException) {
            LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
            statusResponse.setMessage(cause.getMessage());
        } else {
            LOGGER.error("Unknown Error: " + ex.getMessage(), ex);
            statusResponse.setMessage(ex.getMessage());
        }

		return Response.status(status).entity(statusResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
