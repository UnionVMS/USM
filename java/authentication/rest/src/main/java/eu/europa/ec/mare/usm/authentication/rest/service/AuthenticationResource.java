package eu.europa.ec.mare.usm.authentication.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.rest.ExceptionHandler;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.HeaderParam;

/**
 * REST Web Service for User Authentication.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("")
public class AuthenticationResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

	@EJB
	AuthenticationService service;

  /**
   * Creates a new instance
   */
	public AuthenticationResource() 
  {
	}

  /**
   * Asserts the identity of a user based on the provided user 
   * identifier and password.
   * 
   * @param request request holding the user identifier and password
   * 
   * @return an AuthenticationResponse with an OK status, or a BAD_REQUEST
   * status if the provided service request is invalid; or an 
   * INTERNAL_SERVER_ERROR status in case an internal error prevented 
   * processing the request.
   */
	@POST
	@Path("authenticate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticateUser(AuthenticationRequest request)
  {
		LOGGER.info("authenticateUser() - (ENTER)");
    
		Response ret;
    
		try {
			AuthenticationResponse response = service.authenticateUser(request);
      ret = Response.status(Response.Status.OK).
                     entity(response).
                     build();
		} catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
		}
    
		LOGGER.info("authenticateUser() - (LEAVE)");
		return ret;
	}

  /**
   * Retrieves a challenge suitable for confirming the identity of 
   * a user.
   * 
   * @param userName the user Name 
   * 
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
	public Response getUserChallenge(@HeaderParam("Authorization") String userName)
  {
		LOGGER.info("getUserChallenge() - (ENTER)");
    
		AuthenticationQuery query = new AuthenticationQuery();
		query.setUserName(userName);

		Response ret;
		try {
			ChallengeResponse response = service.getUserChallenge(query);
      if (response != null) {
        ret = Response.status(Response.Status.OK).
                       entity(response).
                       build();
      } else {
        ret = Response.status(Response.Status.NOT_FOUND).
                       build();
      }
		} catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
		}

		LOGGER.info("getUserChallenge() - (LEAVE)");
		return ret;
	}

  /**
   * Confirms the identity of a user based on the challenge and 
   * user response.
   * 
   * @param request a challenge/response authentication request.
   * 
   * @return an AuthenticationResponse with an OK status, or a NOT_FOUND
   * status in case the challenge does not exist, or a BAD_REQUEST
   * status if the provided service request is invalid or an 
   * INTERNAL_SERVER_ERROR status in case an internal error prevented 
   * processing the request. 
   */
	@POST
	@Path("challengeauth")
	@Produces("application/json")
	public Response authenticateUserByChallenge(ChallengeResponse request)
  throws WebApplicationException 
  {
		LOGGER.info("authenticateUserByChallenge() - (ENTER)");
    
		Response ret;
		try {
			AuthenticationResponse response = service.authenticateUser(request);
      if (response != null) {
        ret = Response.status(Response.Status.OK).
                       entity(response).
                       build();
      } else {
        ret = Response.status(Response.Status.NOT_FOUND).
                       build();
      }
		} catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
		}

		LOGGER.info("authenticateUserByChallenge() - (LEAVE)");
		return ret;
	}
	
}
