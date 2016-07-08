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
package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("endpointcontact")
public class EndpointContactResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(EndpointContactResource.class);

  @EJB
  OrganisationService service;

  @Context
  private HttpServletRequest servletRequest;

  public EndpointContactResource() {
  }

  /**
   * Creates a new endPointContact
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endPointContact the new endPointContact to be created
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @POST
  @Consumes("application/json")
  @Produces("application/json")
  public Response assignContact(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  EndPointContact endPointContact) 
  {
    LOGGER.info("assignContact() - (ENTER)");

    Response ret;

    try {
      ServiceRequest<EndPointContact> request = new ServiceRequest<>();
      request.setRequester(servletRequest.getRemoteUser());
      request.setRoleName(roleName);
      request.setScopeName(scopeName);
      request.setBody(endPointContact);
      EndPointContact assignedContact = service.assignContact(request);

      Response.Status status;
      if (assignedContact.getEndPointContactId() != null) {
        status = Response.Status.OK;
      } else {
        status = Response.Status.NOT_MODIFIED;
      }
      ret = Response.status(status).entity(assignedContact).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("assignContact() - (LEAVE)");
    return ret;
  }

  /**
   * Deletes an existing contact
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endpointcontactId the identifier of the contact to be deleted
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @DELETE
  @Path("{endpointcontactId}")
  @Produces("application/json")
  public Response removeContact(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  @PathParam("endpointcontactId") Long endpointcontactId) 
  {
    LOGGER.info("removeContact(" + endpointcontactId + ") - (ENTER)");

    Response ret;

    try {
      EndPointContact endPointContact = new EndPointContact();
      endPointContact.setEndPointContactId(endpointcontactId);

      ServiceRequest<EndPointContact> request = new ServiceRequest<>();
      request.setRequester(servletRequest.getRemoteUser());
      request.setRoleName(roleName);
      request.setScopeName(scopeName);
      request.setBody(endPointContact);

      service.removeContact(request);
      Response.Status status = Response.Status.OK;
      ret = Response.status(status).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.info("removeContact() - (ENTER)");

    return ret;
  }
}