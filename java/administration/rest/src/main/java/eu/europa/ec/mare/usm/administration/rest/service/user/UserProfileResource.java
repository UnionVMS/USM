package eu.europa.ec.mare.usm.administration.rest.service.user;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.user.ManageUserService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Yet another REST Web Service exposing the changePassword 
 * operation offered by the Manage User service.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("profile")
public class UserProfileResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileResource.class);

  @EJB
  ManageUserService manageService;
  
  @Context 
  private HttpServletRequest servletRequest;

  /**
   * Creates a new instance
   */
  public UserProfileResource() 
  {
  }

  /**
   * Changes the password of an existing user.<br/> 
   * May be used either by an administrator to change the password of 
   * other users or by a regular user to change his/her own password. 
   * When used to change the password of another user the service requester 
   * must have a right to manager-users, while when used to change a user own
   * password the user status may not be disabled and the current user password
   * must be provided in the request.
   *
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param changePassword the request to change the password of a user
   *
   *
   * @return an OK status code in case of success or a BAD_REQUEST status code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR 
   * status code in case an internal error prevented fulfilling the request 
   * or a FORBIDDEN status code in case the end user is not authorised to 
   * perform the operation or an UNAUTHORIZED status code in case the end user 
   * is not authenticated
   */
  @PUT
  @Path("userPassword")
  @Consumes("application/json")
  @Produces("application/json")
  public Response changePassword(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   ChangePassword changePassword) 
  {
    LOGGER.debug("changePassword() - (ENTER)");

    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(changePassword);
    	
    Response ret;
    try {
    	manageService.changePassword(request);
    	ret = Response.status(Response.Status.OK).build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("changePassword() - (LEAVE)");
    return ret;
  }

}