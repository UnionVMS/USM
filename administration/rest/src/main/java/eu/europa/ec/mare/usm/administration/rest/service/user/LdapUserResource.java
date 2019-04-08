package eu.europa.ec.mare.usm.administration.rest.service.user;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.LdapUser;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.ldap.LdapUserInfoService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * REST Web Service implementation of the LdapUserInfoService.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("ldap")
public class LdapUserResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserResource.class);

  @EJB
  LdapUserInfoService service;

  @Context 
  private HttpServletRequest servletRequest;
  
  /**
   * Creates a new instance
   */
  public LdapUserResource() {
  }

  /**
   * Retrieves a user according to the provided user name
   *
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param userName the requested user name
   *
   * @return an OK status and the requested user, or a BAD_REQUEST error code in
   * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
   * code in case an internal error prevented fulfilling the request or
   * UnauthorisedException with an UNAUTHORIZED error code in case the end user
   * is not authorised to perform the operation
   */
  @GET
  @Path("{userName}")
  @Produces("application/json")
  public Response getLdapUser(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                @PathParam("userName") String userName) 
  {
    LOGGER.info("getLdapUser() - (ENTER)");

    ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    GetUserQuery query = new GetUserQuery();
    query.setUserName(userName);
    request.setBody(query);

    Response ret;
    try {
      LdapUser usr = service.getLdapUserInfo(request);
      Status status;
      if (usr == null) {
        status = Response.Status.NOT_FOUND;
      } else {
        status = Response.Status.OK;
      }
      ret = Response.status(status).entity(usr).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }
    
    LOGGER.info("getLdapUser() - (LEAVE)");
    return ret;
  }
}