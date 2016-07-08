/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.service.InformationService;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 */
@Stateless
@Path("")
public class InformationResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationResource.class);

  @EJB
  InformationService service;
  
  
  /**
   * Creates a new instance
   */
  public InformationResource() {
  }

  /**
   * Retrieves a UserContext for the provided user and application
   * 
   * @param applicationName the application name
   * @param userName the user name
   * 
	 * @return the UserContext with an OK status, or a NO_CONTENT error code 
   * in case the UserContext does not exist, or a BAD_REQUEST error code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error 
   * code in case an internal error prevented fulfilling the request
   */
  @GET
  @Path("userContext/{applicationName}/{userName}")
  @Produces("application/json")
  public Response getUserContext(@PathParam("applicationName") String applicationName, 
                                   @PathParam("userName") String userName) 
  {
    LOGGER.info("getUserContext(" + applicationName + "," + userName + ") - (ENTER)");
    
    UserContextQuery query = new UserContextQuery();
    query.setApplicationName(applicationName);
    query.setUserName(userName);
    
    Response ret;
    try {
      UserContext ctx = service.getUserContext(query);
      
      if (ctx == null){
        ret = Response.status(Response.Status.NO_CONTENT).build();
      } else {
        ret = Response.status(Response.Status.OK).entity(ctx).build();
      }
		} catch (Exception exc) {
      ret = handleException(exc);
    }
    
    LOGGER.info("getUserContext() - (LEAVE)");
    return ret;
  }
  
  /**
   * Retrieves the UserContext for the specified user
   * 
   * @param userName the user name
   * 
   * @return the UserContext with an OK status, or a NO_CONTENT error code 
   * in case the UserContext does not exist, or a BAD_REQUEST error code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error 
   * code in case an internal error prevented fulfilling the request
   */
  @GET
  @Path("userContext/{userName}")
  @Produces("application/json")
  public Response getUserContext(@PathParam("userName") String userName) 
  {
    LOGGER.info("getUserContext(" + userName + ") - (ENTER)");
    
    UserContextQuery query = new UserContextQuery();
    query.setUserName(userName);
    
    Response ret;
    try {
      UserContext ctx = service.getUserContext(query);
      
      if (ctx == null){
        ret = Response.status(Response.Status.NO_CONTENT).build();
      } else {
        ret = Response.status(Response.Status.OK).entity(ctx).build();
      }
    } catch (Exception exc) {
      ret = handleException(exc);
    }
    
    LOGGER.info("getUserContext() - (LEAVE)");
    return ret;
  }

  /**
   * Updates or creates the preferences held in the provided 
   * UserContext. 
   * 
   * @param content JSON representation of the UserContext
   * 
	 * @return an OK status, or a BAD_REQUEST error code in case the provided 
   * input is incomplete, or an INTERNAL_SERVER_ERROR error 
   * code in case an internal error prevented fulfilling the request
   */
  @PUT
  @Path("userContext")
  @Consumes("application/json")
  @Produces("application/json")
  public Response putUserContext(UserContext content) 
  {
    LOGGER.info("putUserContext(" + content + ") - (ENTER)");
    
    Response ret;
    try {
      service.updateUserPreferences(content);
      ret = Response.ok().build();
		} catch (Exception exc) {
      ret = handleException(exc);
    }

    LOGGER.info("putUserContext() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves information about all organisations associated with the
   * provided nation.
   * 
   * @param nation the nation name
   * 
	 * @return the matching Organisations with an OK status, or a NO_CONTENT 
   * status code if there are no organisations associated with the nation, 
   * or a BAD_REQUEST error code in case the provided input is incomplete, 
   * or an  INTERNAL_SERVER_ERROR error code in case an internal error prevented 
   * fulfilling the request
   */
  @GET
  @Path("organisation/nation/{nation}")
  @Produces("application/json")
  public Response findOrganisations(@PathParam("nation") String nation) 
  {
    LOGGER.info("findOrganisations(" + nation + ") - (ENTER)");
    Response ret;
    
    try {
      List<Organisation> xx = service.findOrganisations(nation);
      
      if (xx.isEmpty()){
        ret = Response.status(Response.Status.NO_CONTENT).build();
      } else {
        ret = Response.status(Response.Status.OK).entity(xx).build();
      }
	} catch (Exception exc) {
      ret = handleException(exc);
    }
    
    LOGGER.info("findOrganisations() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the Organisation with the provided name.
   * 
   * @param organisationName the organisation name
   * 
	 * @return the Organisation with an OK status, or a BAD_REQUEST error code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error 
   * code in case an internal error prevented fulfilling the request
   */
  @GET
  @Path("organisation/{organisationName}")
  @Produces("application/json")
  public Response getOrganisation(@PathParam("organisationName") String organisationName) 
  {
    LOGGER.info("getOrganisation(" + organisationName + ") - (ENTER)");
    Response ret;
    
    try {
      Organisation xx = service.getOrganisation(organisationName);
      
      if (xx == null){
        ret = Response.status(Response.Status.NO_CONTENT).build();
      } else {
        ret = Response.status(Response.Status.OK).entity(xx).build();
      }
		} catch (Exception exc) {
      ret = handleException(exc);
    }
    
    LOGGER.info("getOrganisation() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the ContactDetails of the user with the provided name.
   * 
   * @param userName the user name
   * 
	 * @return the ContactDetails with an OK status, or a BAD_REQUEST error code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error 
   * code in case an internal error prevented fulfilling the request
   */
  @GET
  @Path("contactDetails/{userName}")
  @Produces("application/json")
  public Response getContactDetails(@PathParam("userName") String userName) 
  {
    LOGGER.info("getContactDetails(" + userName + ") - (ENTER)");
    Response ret;
    
    try {
      ContactDetails xx = service.getContactDetails(userName);
      
      if (xx == null){
        ret = Response.status(Response.Status.NO_CONTENT).build();
      } else {
        ret = Response.status(Response.Status.OK).entity(xx).build();
      }
		} catch (Exception exc) {
      ret = handleException(exc);
    }
    
    LOGGER.info("getContactDetails() - (LEAVE)");
    return ret;
  }


  private Response handleException(Exception exc) 
  {
    StatusResponse msg = new StatusResponse();
    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
    
    Throwable cause = exc;

    if (exc.getCause() != null) {
      cause = exc.getCause();
    }

    if (cause instanceof IllegalArgumentException) {
      LOGGER.warn("Bad request: " + cause.getMessage());
      msg.setMessage(cause.getMessage());
      status = Response.Status.BAD_REQUEST;
    } else if (cause instanceof RuntimeException) {
      LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
      msg.setMessage(cause.getMessage());
    } else {
      LOGGER.error("Unknown Error: " + exc.getMessage(), exc);
      msg.setMessage(exc.getMessage());
    }
    
    Response ret = Response.status(status).entity(msg).build();

    return ret;
  }

}