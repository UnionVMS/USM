package eu.europa.ec.mare.usm.administration.rest.service.authentication;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.service.InformationService;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.session.service.SessionTracker;
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
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

    @EJB
    private JwtTokenHandler tokenHandler;

    @EJB
    private AuthenticationService service;

    @EJB
    private InformationService informationService;

    @Context
    private HttpServletRequest servletRequest;

    @EJB
    private SessionTracker sessionTracker;

    private SessionInfo sessionInfo;


    /**
     * Authenticates a user via the provided AuthenticationRequest.
     *
     * @param request the AuthenticationRequest holding the userName and
     *                password of the user to be authenticated
     * @return an AuthenticationJwtResponse denoting whether the user was
     * authenticated or not (and why) and if yes, a JWT token identifying the user
     * @throws WebApplicationException in case the provided input is invalid
     *                                 (BAD_REQUEST) or in case a technical problem prevented processing the
     *                                 request (INTERNAL_SERVER_ERROR)
     */
    @POST
    @Path("authenticate")
    public AuthenticationJwtResponse authenticate(AuthenticationRequest request) throws WebApplicationException {
        LOGGER.info("authenticate() - (ENTER)");

        AuthenticationJwtResponse ret = new AuthenticationJwtResponse();
        try {
            AuthenticationResponse response = service.authenticateUser(request);

            if (response != null) {
                ret.setAuthenticated(response.isAuthenticated());
                ret.setStatusCode(response.getStatusCode());
                ret.setIp(servletRequest.getRemoteAddr());
                ret.setErrorDescription(response.getErrorDescription());
                if (response.isAuthenticated()) {
                    List<Integer> features = informationService.getUserFeatures(request.getUserName());
                    ret.setJwtoken(tokenHandler.createToken(request.getUserName(), features));

                    sessionInfo = new SessionInfo();
                    sessionInfo.setUserName(request.getUserName());
                    sessionInfo.setUserSite(ret.getIp());
                    ret.setSessionId(sessionTracker.startSession(sessionInfo));
                    if (ret.getSessionId() == null) {
                        ret.setAuthenticated(false);
                        ret.setStatusCode(AuthenticationResponse.MAXIMUM_SESSION_NUMBER_EXCEEDED);
                    }
                }
            }
        } catch (IllegalArgumentException exc) {
            LOGGER.error("Bad request: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc, Response.Status.BAD_REQUEST);
        } catch (Exception exc) {
            LOGGER.error("Internal Server Error: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("authenticate() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a challenge suitable for confirming the identity of a
     * user.
     *
     * @param token the JWT token identifying the service requester,
     *              optional if the service requester is authenticated by the J2EE container
     * @return a challenge if the provided user has an active user account with
     * configured challenge response(s),  <i>null</i> otherwise
     * @throws WebApplicationException in the request does not contain any
     *                                 user identity (BAD_REQUEST) or if the user does not exist or has no
     *                                 associated challenge (NOT_FOUND), or in case a technical problem
     *                                 prevented processing the request (INTERNAL_SERVER_ERROR).
     */
    @GET
    @Path("challenge")
    public ChallengeResponse getUserChallenge(@HeaderParam("authorization") String token) throws WebApplicationException {
        LOGGER.info("getUserChallenge() - (ENTER)");

        ChallengeResponse challengeResponse;

        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName(servletRequest.getRemoteUser());

        try {
            challengeResponse = service.getUserChallenge(query);
        } catch (IllegalArgumentException exc) {
            LOGGER.error("Bad request: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc, Response.Status.BAD_REQUEST);
        } catch (Exception exc) {
            LOGGER.error("Internal Server Error: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc, Response.Status.INTERNAL_SERVER_ERROR);
        }

        if (challengeResponse == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        LOGGER.info("getUserChallenge() - (LEAVE)");
        return challengeResponse;
    }

    /**
     * Confirms the identity of a user based on the challenge and user
     * response.
     *
     * @param request a challenge/response authentication request holding the
     *                user-name, the challenge and the user response .
     * @return an AuthenticationResponse denoting whether the user was
     * authenticated or not (and why)
     * @throws WebApplicationException in the request does not contain any
     *                                 user identity (BAD_REQUEST) or or if the user has no associated challenge
     *                                 (NOT_FOUND), or in case a technical problem prevented processing the
     *                                 request (INTERNAL_SERVER_ERROR)
     */
    @POST
    @Path("challengeauth")
    public AuthenticationResponse authenticate(ChallengeResponse request) throws WebApplicationException {
        LOGGER.info("authenticate() - (ENTER)");

        AuthenticationResponse ret;
        try {
            ret = service.authenticateUser(request);
        } catch (IllegalArgumentException exc) {
            LOGGER.error("Bad request: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc, Response.Status.BAD_REQUEST);
        } catch (Exception exc) {
            LOGGER.error("Internal Server Error: " + exc.getMessage(), exc);
            throw new WebApplicationException(exc, Response.Status.INTERNAL_SERVER_ERROR);
        }

        if (ret == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        LOGGER.info("authenticate() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the current users contexts list
     *
     * @param token the JWT token authenticating the service requester
     * @return an OK status and the requested context, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("userContexts")
    public Response getContexts(@HeaderParam("authorization") String token) {
        LOGGER.info("getContexts() - (ENTER)");

        Response response;
        Status status;
        UserContext userContextResponse;

        try {
            ServiceRequest<UserContextQuery> contextRequest = new ServiceRequest<>();
            String remoteUser = servletRequest.getRemoteUser();
            LOGGER.info("getContexts() - remoteUser: " + remoteUser);
            contextRequest.setRequester(remoteUser);
            UserContextQuery query = new UserContextQuery();
            query.setUserName(remoteUser);
            contextRequest.setBody(query);
            userContextResponse = informationService.getUserContext(query);
            LOGGER.info("getContexts() - userContextResponse: " + userContextResponse);
            if (userContextResponse == null || userContextResponse.getContextSet().getContexts().isEmpty()) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }
            response = Response.status(status).entity(userContextResponse).build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getContexts() - (LEAVE)");
        return response;
    }

    /**
     * Simple ping response to allow the front end to confirm the user is authenticated
     *
     * @param token the JWT token authenticating the service requester
     * @return an OK status or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("ping")
    public Response getPing(@HeaderParam("authorization") String token) {
        LOGGER.info("getPing() - (ENTER)");

        Response response;

        try {
            String remoteUser = servletRequest.getRemoteUser();
            LOGGER.info("getContexts() - remoteUser: " + remoteUser);

            Status status = Response.Status.OK;
            StatusResponse msg = new StatusResponse();
            msg.setMessage("OK");
            msg.setStatusCode(status.getStatusCode());

            response = Response.status(status).entity(msg).build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getPing() - (LEAVE)");
        return response;
    }

}
