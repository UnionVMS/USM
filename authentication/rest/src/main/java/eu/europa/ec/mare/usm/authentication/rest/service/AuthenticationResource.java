package eu.europa.ec.mare.usm.authentication.rest.service;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.rest.ExceptionHandler;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
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
@Path("")
public class AuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

    @EJB
    private AuthenticationService service;

    /**
     * Asserts the identity of a user based on the provided user
     * identifier and password.
     *
     * @param request request holding the user identifier and password
     * @return an AuthenticationResponse with an OK status, or a BAD_REQUEST
     * status if the provided service request is invalid; or an
     * INTERNAL_SERVER_ERROR status in case an internal error prevented
     * processing the request.
     */
    @POST
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(AuthenticationRequest request) {
        LOGGER.info("authenticateUser() - (ENTER)");

        Response response;
        try {
            AuthenticationResponse ar = service.authenticateUser(request);
            response = Response.ok(ar).build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("authenticateUser() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves a challenge suitable for confirming the identity of
     * a user.
     *
     * @param userName the user Name
     * @return a challenge if the provided user has an active user account with
     * configured challenge response(s),
     * or a NOT_FOUND status in case the not challenge exist, or a BAD_REQUEST
     * status if the provided service request is invalid or an
     * INTERNAL_SERVER_ERROR status in case an internal error prevented
     * processing the request.
     */
    @GET
    @Path("challenge")
    @Produces("application/json")
    public Response getUserChallenge(@HeaderParam("Authorization") String userName) {
        LOGGER.info("getUserChallenge() - (ENTER)");

        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName(userName);

        Response response;
        try {
            ChallengeResponse cr = service.getUserChallenge(query);
            if (cr != null) {
                response = Response.ok(cr).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getUserChallenge() - (LEAVE)");
        return response;
    }

    /**
     * Confirms the identity of a user based on the challenge and
     * user response.
     *
     * @param request a challenge/response authentication request.
     * @return an AuthenticationResponse with an OK status, or a NOT_FOUND
     * status in case the challenge does not exist, or a BAD_REQUEST
     * status if the provided service request is invalid or an
     * INTERNAL_SERVER_ERROR status in case an internal error prevented
     * processing the request.
     */
    @POST
    @Path("challengeauth")
    @Produces("application/json")
    public Response authenticateUserByChallenge(ChallengeResponse request) throws WebApplicationException {
        LOGGER.info("authenticateUserByChallenge() - (ENTER)");

        Response response;
        try {
            AuthenticationResponse ar = service.authenticateUser(request);
            if (ar != null) {
                response = Response.ok(ar).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("authenticateUserByChallenge() - (LEAVE)");
        return response;
    }

}
