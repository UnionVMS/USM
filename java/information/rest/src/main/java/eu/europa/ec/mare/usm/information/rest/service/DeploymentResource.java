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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.information.service.DeploymentService;

/**
 * REST Web Service implementation of the (Application) Deployment Service
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("deployments")
public class DeploymentResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentResource.class);

  @EJB
  DeploymentService service;

  @Context 
  private HttpServletRequest servletRequest;

  /**
   * Creates a new instance
   */
  public DeploymentResource() {
  }

  /**
   * Retrieves the Deployment Descriptor for the application with the 
   * provided name
   *
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param applicationName the Application Name
   *
   * @return an OK status and the requested Descriptor in case of success or 
   * a BAD_REQUEST status code in case the provided input is incomplete, or 
   * an INTERNAL_SERVER_ERROR status code in case an internal error prevented 
   * fulfilling the request or a FORBIDDEN status code in case the end user is 
   * not authorised to perform the operation or an UNAUTHORIZED status code in 
   * case the end user is not authenticated
   */
  @GET
  @Path("{applicationName}")
  @Produces("application/xml")
  public Response getDeployment(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  @PathParam("applicationName") String applicationName)
  {
    LOGGER.info("getDeployment(" + applicationName + ") - (ENTER)");

    Response ret;
    
    try {
      Application dd = service.getDeploymentDescriptor(applicationName);

      if (dd != null) {
        ret = Response.status(Response.Status.OK).
                       entity(dd).
                       build();
      } else {
        ret = Response.status(Response.Status.NO_CONTENT).build();
      }
      
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getDeployment() - (LEAVE)");
    return ret;
  }
  
  /**
   * Deploys the provided Application as a new Application.
   * 
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param application the new application to be deployed
   * 
   * @return an OK status code in case of success or a BAD_REQUEST status code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR 
   * status code in case an internal error prevented  fulfilling the request 
   * or a FORBIDDEN status code in case the end user is not authorised to 
   * perform the operation or an UNAUTHORIZED status code in case the end user 
   * is not authenticated
   */
  @POST
  @Path("")
  @Consumes("application/xml")
  @Produces("application/xml")
  public Response deployApplication(@HeaderParam("authorization") String jwtToken,
                                      @HeaderParam("roleName") String roleName,
                                      @HeaderParam("scopeName") String scopeName,
                                      Application application)
  {
    LOGGER.info("deployApplication(" + application + ") - (ENTER)");

    Response ret;
    try {
      service.deployApplication(application);

      ret = Response.status(Response.Status.OK).
                     build();
    } catch (Exception e) {
    	ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("deployApplication() - (LEAVE)");
    return ret;
  }
  
  /**
   * Updates an application 
   * 
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param application the application to be updated
   * 
   * @return an OK status code in case of success or a BAD_REQUEST status code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR 
   * status code in case an internal error prevented  fulfilling the request 
   * or a FORBIDDEN status code in case the end user is not authorised to 
   * perform the operation or an UNAUTHORIZED status code in case the end user 
   * is not authenticated
   */
  @PUT
  @Path("")
  @Consumes("application/xml")
  @Produces("application/xml")
  public Response redeployApplication(@HeaderParam("authorization") String jwtToken,
                                         @HeaderParam("roleName") String roleName,
                                         @HeaderParam("scopeName") String scopeName,
                                        Application application)
  {
    LOGGER.info("redeployApplication(" + application + ") - (ENTER)");

    Response ret;

    try {
      service.redeployApplication(application);
      ret = Response.status(Response.Status.OK).
                     build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("redeployApplication() - (LEAVE)");
    return ret;
  }
 
  /**
   * Deploys the provided Datasets to an existing Application.
   * 
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param application the application to be updated
   * 
   * @return an OK status code in case of success or a BAD_REQUEST status code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR 
   * status code in case an internal error prevented  fulfilling the request 
   * or a FORBIDDEN status code in case the end user is not authorised to 
   * perform the operation or an UNAUTHORIZED status code in case the end user 
   * is not authenticated
   */
  @PUT
  @Path("datasets")
  @Consumes("application/xml")
  @Produces("application/xml")
  public Response deployDatasets(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   Application application)
  {
    LOGGER.info("deployDatasets(" + application + ") - (ENTER)");

    Response ret;

    try {
      service.deployDatasets(application);
      ret = Response.status(Response.Status.OK).
                     build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("deployDatasets() - (LEAVE)");
    return ret;
  }
   
  
  /**
   * Un-deploys the provided Application.
   * 
   * @param jwtToken the JWT token identifying the service requester, 
   * optional if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext (optional)
   * @param applicationName the name of the application to be un-deployed
   *
   * @return an OK status code in case of success or a BAD_REQUEST status code 
   * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR 
   * status code in case an internal error prevented  fulfilling the request 
   * or a FORBIDDEN status code in case the end user is not authorised to 
   * perform the operation or an UNAUTHORIZED status code in case the end user 
   * is not authenticated
   */
  @DELETE
  @Path("{applicationName}")
  @Produces("application/xml")
  public Response undeployApplication(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName,
                                        @PathParam("applicationName") String applicationName)
  {
    LOGGER.info("undeployApplication("+applicationName+") - (ENTER)");

    Response ret;

    try {
    	
      service.undeployApplication(applicationName);
      ret = Response.status(Response.Status.OK).
                     build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }
    
    LOGGER.info("undeployApplication() - (ENTER)");
    
    return ret;
  }
}