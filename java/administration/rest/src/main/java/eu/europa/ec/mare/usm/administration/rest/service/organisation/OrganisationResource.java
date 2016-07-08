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

import eu.europa.ec.mare.usm.administration.domain.Channel;
import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
import eu.europa.ec.mare.usm.administration.domain.FindOrganisationsQuery;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.OrganisationNameResponse;
import eu.europa.ec.mare.usm.administration.domain.OrganisationQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.organisation.OrganisationService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * REST Web Service implementation of the Organisation service
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("organisations")
public class OrganisationResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationResource.class);
  private static final String ALL = "all";

  @EJB
  OrganisationService service;

  @Context
  private HttpServletRequest servletRequest;

  /**
   * Creates a new instance
   */
  public OrganisationResource() {
  }

  /**
   * Creates a new organisation
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param organisation the new organisation to be created
   *
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
  public Response createOrganisation(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName,
                                        Organisation organisation) 
  {
    LOGGER.info("createOrganisation() - (ENTER)");

    ServiceRequest<Organisation> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(organisation);

    Response ret;
    try {
      Organisation createdOrganisation = service.createOrganisation(request);

      Status status;
      if (createdOrganisation.getOrganisationId() != null) {
        status = Response.Status.OK;
      } else {
        status = Response.Status.NOT_MODIFIED;
      }
      ret = Response.status(status).entity(createdOrganisation).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("createOrganisation() - (LEAVE)");
    return ret;
  }

  /**
   * Updates an existing organisation
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param organisation the organisation to be updated
   *
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * UNAUTHORIZED error code in case the end user is not authorised to perform
   * the operation
   */
  @PUT
  @Consumes("application/json")
  @Produces("application/json")
  public Response updateOrganisation(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName,
                                        Organisation organisation) 
  {
    LOGGER.info("updateOrganisation() - (ENTER)");

    ServiceRequest<Organisation> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(organisation);

    Response ret;
    try {
      Organisation updatedOrganisation = service.updateOrganisation(request);

      Status status = Response.Status.OK;
      ret = Response.status(status).entity(updatedOrganisation).build();
    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.debug("updateOrganisation() - (LEAVE)");
    return ret;
  }

  /**
   * Deletes an existing organisation
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param organisationId the identifier of the organisation to be deleted
   *
   * @return Response with status OK (200) in case of success otherwise status
   * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
   * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
   * error prevented fulfilling the request or UnauthorisedException with an
   * FORBIDDEN error code in case the end user is not authorised to perform the
   * operation
   */
  @DELETE
  @Path("{organisationId}")
  @Produces("application/json")
  public Response deleteOrganisation(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName,
                                        @PathParam("organisationId") String organisationId) 
  {
    LOGGER.info("deleteOrganisation(" + organisationId + ") - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(Long.valueOf(organisationId));

    Response ret;
    try {
      service.deleteOrganisation(request);
      Status status = Response.Status.OK;
      ret = Response.status(status).build();

    } catch (Exception e) {
      ret = ExceptionHandler.handleException(e);
    }

    LOGGER.info("deleteOrganisation() - (ENTER)");

    return ret;
  }

  /**
   * Retrieves the list of organisation's names
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @return the list of organisation's names with an OK status, or a
   * BAD_REQUEST error code in case the provided input incomplete, with an
   * INTERNAL_SERVER_ERROR error code in case an internal error prevented
   * fulfilling the request or UnauthorisedException with an FORBIDDEN error
   * code in case the end user is not authorised to perform the operation
   */
  @GET
  @Path("names")
  @Produces("application/json")
  public Response getOrganisationNames(@HeaderParam("authorization") String jwtToken,
                                          @HeaderParam("roleName") String roleName,
                                          @HeaderParam("scopeName") String scopeName) 
  {
    LOGGER.info("getOrganisationNames() - (ENTER)");

    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(new OrganisationQuery());

    Response ret;
    try {
      List<OrganisationNameResponse> lst = service.getOrganisationNames(request);
      Status status;

      if (lst == null || lst.isEmpty()) {
        status = Response.Status.NOT_FOUND;
      } else {
        status = Response.Status.OK;
      }
      ServiceArrayResponse<OrganisationNameResponse> sar = new ServiceArrayResponse<>();
      sar.setResults(lst);
      ret = Response.status(status).entity(sar).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getOrganisationNames() - (LEAVE)");
    return ret;
  }

  /**
   * Gets the name of all possible parent organisations for a given
   * organisation.
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param organisationId the id of the candidate child organisation, or -1 
   * in case of the new (transient) organisation
   * 
   * @return the list of organisation's names with an OK status, or a
   * BAD_REQUEST error code in case the provided input incomplete, with an
   * INTERNAL_SERVER_ERROR error code in case an internal error prevented
   * fulfilling the request or UnauthorisedException with an FORBIDDEN error
   * code in case the end user is not authorised to perform the operation
   */
  @GET
  @Path("{organisationId}/parent/names")
  @Produces("application/json")
  public Response getOrganisationParentNames(@HeaderParam("authorization") String jwtToken,
                                                @HeaderParam("roleName") String roleName,
                                                @HeaderParam("scopeName") String scopeName,
                                                @PathParam("organisationId") Long organisationId) 
  {
    LOGGER.info("getOrganisationParentNames() - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(organisationId);

    Response ret;
    try {
      List<String> lst = service.getOrganisationParentNames(request);
      Status status;

      if (lst == null || lst.isEmpty()) {
        status = Response.Status.NOT_FOUND;
      } else {
        status = Response.Status.OK;
      }
      ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
      sar.setResults(lst);
      ret = Response.status(status).entity(sar).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getOrganisationParentNames() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the (distinct) names of nations for which organisations exist.
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @return the list of nation names with an OK status, or a BAD_REQUEST error
   * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR
   * error code in case an internal error prevented fulfilling the request or
   * UnauthorisedException with an FORBIDDEN error code in case the end user is
   * not authorised to perform the operation
   */
  @GET
  @Path("nations/names")
  @Produces("application/json")
  public Response getNationNames(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName) 
  {
    LOGGER.info("getNationNames() - (ENTER)");

    ServiceRequest<OrganisationQuery> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(new OrganisationQuery());

    Response ret;
    try {
      List<String> lst = service.getNationNames(request);
      Status status;

      if (lst == null || lst.isEmpty()) {
        status = Response.Status.NOT_FOUND;
      } else {
        status = Response.Status.OK;
      }
      ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
      sar.setResults(lst);
      ret = Response.status(status).entity(sar).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getNationNames() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves a list of organisations that meet the provided criteria
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param offset the pagination offset
   * @param limit the pagination limit
   * @param sortColumn the sort Column
   * @param sortDirection the sort Direction
   * @param name the organisation name
   * @param nation the organisation nation
   * @param status the organisation status
   * @return an OK status and the list of organisations that meet the criteria,
   * or a BAD_REQUEST error code in case the provided input incomplete, with an
   * INTERNAL_SERVER_ERROR error code in case an internal error prevented
   * fulfilling the request or UnauthorisedException with an FORBIDDEN error
   * code in case the end user is not authorised to perform the operation
   */
  @GET
  @Produces("application/json")
  public Response findOrganisations(@HeaderParam("authorization") String jwtToken,
                                      @HeaderParam("roleName") String roleName,
                                      @HeaderParam("scopeName") String scopeName,
                                      @DefaultValue("0") @QueryParam("offset") int offset,
                                      @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
                                      @DefaultValue("name") @QueryParam("sortColumn") String sortColumn,
                                      @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
                                      @QueryParam("name") String name,
                                      @QueryParam("nation") String nation,
                                      @QueryParam("status") String status) 
  {
    LOGGER.info("findOrganisations() - (ENTER)");

    // Setup paginator
    Paginator paginator = new Paginator();
    paginator.setOffset(offset);
    paginator.setLimit(limit);
    paginator.setSortColumn(sortColumn);
    paginator.setSortDirection(sortDirection);

    ServiceRequest<FindOrganisationsQuery> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    FindOrganisationsQuery findOrganisationsQuery = new FindOrganisationsQuery();
    findOrganisationsQuery.setName((!"".equals(name)) ? name : null);
    findOrganisationsQuery.setNation((!"".equals(nation)) ? nation : null);
    findOrganisationsQuery.setStatus((status != null && !ALL.equals(status)) ? status : null);
    findOrganisationsQuery.setPaginator(paginator);
    request.setBody(findOrganisationsQuery);

    Response ret;
    try {
      PaginationResponse<Organisation> pResponse = service.findOrganisations(request);

      Status statusCode;
      if (pResponse == null || pResponse.getResults().isEmpty()) {
        statusCode = Response.Status.NO_CONTENT;
      } else {
        statusCode = Response.Status.OK;
      }

      ret = Response.status(statusCode).entity(pResponse).build();

    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("findOrganisations() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves a organisation according to the provided organisationId
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param organisationId the requested organisation id
   * @return an OK status and the requested organisation, or a BAD_REQUEST error
   * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR
   * error code in case an internal error prevented fulfilling the request or
   * UnauthorisedException with an UNAUTHORIZED error code in case the end user
   * is not authorised to perform the operation
   */
  @GET
  @Path("{organisationId}")
  @Produces("application/json")
  public Response getOrganisation(@HeaderParam("authorization") String jwtToken,
                                    @HeaderParam("roleName") String roleName,
                                    @HeaderParam("scopeName") String scopeName,
                                    @PathParam("organisationId") Long organisationId) 
  {
    LOGGER.info("getOrganisation() - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(organisationId);

    Response ret;
    try {
      Organisation organisation = service.getOrganisation(request);

      Status status;
      if (organisation == null) {
        status = Response.Status.NO_CONTENT;
      } else {
        status = Response.Status.OK;
      }
      ret = Response.status(status).entity(organisation).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getOrganisation() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves an end point according to the provided endPointId
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endPointId the requested end point id
   * @return an OK status and the requested end point, or a BAD_REQUEST error
   * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR
   * error code in case an internal error prevented fulfilling the request or
   * UnauthorisedException with an UNAUTHORIZED error code in case the end user
   * is not authorised to perform the operation
   */
  @GET
  @Path("endpoint/{endPointId}")
  @Produces("application/json")
  public Response getEndPoint(@HeaderParam("authorization") String jwtToken,
                                @HeaderParam("roleName") String roleName,
                                @HeaderParam("scopeName") String scopeName,
                                @PathParam("endPointId") Long endPointId) 
  {
    LOGGER.info("getEndPoint() - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(endPointId);

    Response ret;
    try {
      EndPoint endPoint = service.getEndPoint(request);

      Status status;
      if (endPoint == null) {
        status = Response.Status.NO_CONTENT;
      } else {
        status = Response.Status.OK;
      }
      ret = Response.status(status).entity(endPoint).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getEndPoint() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves a communication channel according to the provided channelId
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param channelId the requested channel id
   * @return an OK status and the requested channel, or a BAD_REQUEST error code
   * in case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
   * code in case an internal error prevented fulfilling the request or
   * UnauthorisedException with an UNAUTHORIZED error code in case the end user
   * is not authorised to perform the operation
   */
  @GET
  @Path("channel/{channelId}")
  @Produces("application/json")
  public Response getChannel(@HeaderParam("authorization") String jwtToken,
                              @HeaderParam("roleName") String roleName,
                              @HeaderParam("scopeName") String scopeName,
                              @PathParam("channelId") Long channelId) 
  {
    LOGGER.info("getChannel() - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(channelId);

    Response ret;
    try {
      Channel channel = service.getChannel(request);

      Status status;
      if (channel == null) {
        status = Response.Status.NO_CONTENT;
      } else {
        status = Response.Status.OK;
      }
      ret = Response.status(status).entity(channel).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getChannel() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves an end point contact according to the provided endPointContactId
   *
   * @param jwtToken the JWT token identifying the service requester, optional
   * if the service requester is authenticated by the J2EE container
   * @param roleName the name of the Role of the selected UserContext (optional)
   * @param scopeName the name of the Scope of the selected UserContext
   * (optional)
   * @param endPointContactId the requested end point contact id
   * @return an OK status and the requested end point contact, or a BAD_REQUEST
   * error code in case the provided input incomplete, with an
   * INTERNAL_SERVER_ERROR error code in case an internal error prevented
   * fulfilling the request or UnauthorisedException with an UNAUTHORIZED error
   * code in case the end user is not authorised to perform the operation
   */
  @GET
  @Path("contact/{endPointContactId}")
  @Produces("application/json")
  public Response getEndPointContact(@HeaderParam("authorization") String jwtToken,
                                        @HeaderParam("roleName") String roleName,
                                        @HeaderParam("scopeName") String scopeName,
                                        @PathParam("endPointContactId") Long endPointContactId) 
  {
    LOGGER.info("getEndPointContact() - (ENTER)");

    ServiceRequest<Long> request = new ServiceRequest<>();
    request.setRequester(servletRequest.getRemoteUser());
    request.setRoleName(roleName);
    request.setScopeName(scopeName);
    request.setBody(endPointContactId);

    Response ret;
    try {
      EndPointContact endPointContact = service.getContact(request);

      Status status;
      if (endPointContact == null) {
        status = Response.Status.NO_CONTENT;
      } else {
        status = Response.Status.OK;
      }
      ret = Response.status(status).entity(endPointContact).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("getEndPointContact() - (LEAVE)");
    return ret;
  }

}