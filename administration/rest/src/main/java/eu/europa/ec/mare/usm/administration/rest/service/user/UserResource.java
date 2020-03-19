package eu.europa.ec.mare.usm.administration.rest.service.user;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ResponseWrapper;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.DateParser;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.user.ManageUserService;
import eu.europa.ec.mare.usm.administration.service.user.ViewUsersService;
import eu.europa.ec.mare.usm.administration.service.userContext.UserContextService;
import eu.europa.ec.mare.usm.administration.service.userPreference.UserPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @EJB
    private ViewUsersService service;

    @EJB
    private ManageUserService manageService;

    @EJB
    private UserContextService userContextService;

    @EJB
    private UserPreferenceService userPreferenceService;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Retrieves a list of users(containing only the basic info) plus a total
     * results value that meet the provided criteria and the provided pagination
     * options
     *
     * @param jwtToken      the JWT token identifying the service requester, optional if
     *                      the service requester is authenticated by the J2EE container
     * @param roleName      the name of the Role of the selected UserContext (optional)
     * @param scopeName     the name of the Scope of the selected UserContext
     *                      (optional)
     * @param offset        the pagination offset
     * @param limit         the pagination limit
     * @param sortColumn    the sort Column
     * @param sortDirection the sort Direction
     * @param user          the requested user name or first name or last name
     * @param nation        the requested nation
     * @param organisation  the requested organisation
     * @param activeFrom    the date (in ISO 8601 format) user is active from
     * @param activeTo      the date (in ISO 8601 format) user is active to
     * @param status        the requested status of the user
     * @return an OK statues and the list of users that meet the criteria, or a
     * BAD_REQUEST error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    public Response findUsers(@HeaderParam("authorization") String jwtToken,
                              @HeaderParam("roleName") String roleName,
                              @HeaderParam("scopeName") String scopeName,
                              @DefaultValue("0") @QueryParam("offset") int offset,
                              @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
                              @DefaultValue("user_name") @QueryParam("sortColumn") String sortColumn,
                              @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
                              @QueryParam("user") String user,
                              @QueryParam("nation") String nation,
                              @QueryParam("organisation") String organisation,
                              @QueryParam("activeFrom") String activeFrom,
                              @QueryParam("activeTo") String activeTo,
                              @QueryParam("status") String status) {
        LOGGER.debug("findUsers() - (ENTER)");

        ServiceRequest<FindUsersQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);

        // Setup paginator
        Paginator paginator = new Paginator();
        paginator.setOffset(offset);
        paginator.setLimit(limit);
        paginator.setSortColumn(sortColumn);
        paginator.setSortDirection(sortDirection);

        // Setup query
        FindUsersQuery query = new FindUsersQuery();
        query.setName(user);
        query.setNation(nation);
        query.setOrganisation(organisation);
        query.setActiveFrom(DateParser.parseDate("activeFrom", activeFrom));
        query.setActiveTo(DateParser.parseDate("activeTo", activeTo));
        query.setStatus(status);
        query.setPaginator(paginator);
        request.setBody(query);

        Response ret;

        try {
            Status repStatus;
            PaginationResponse<UserAccount> users;
            users = service.findUsers(request);
            if (users == null) {
                repStatus = Response.Status.NOT_FOUND;
            } else {
                repStatus = Response.Status.OK;
            }
            ret = Response.status(repStatus).entity(users).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("findUsers() - (LEAVE)");
        return ret;
    }

    /**
     * Creates a new user
     *
     * @param jwtToken  the JWT token identifying the service requester, optional if
     *                  the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param user      the details of the user to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response createUser(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               UserAccount user) {
        LOGGER.debug("createUser() - (ENTER)");

        ServiceRequest<UserAccount> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(user);
        Response ret;

        try {
            Status status;
            UserAccount createdUser = manageService.createUser(request);
            if (createdUser != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(createdUser).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("createUser() - (LEAVE)");
        return ret;
    }

    /**
     * Updates an existing user
     *
     * @param jwtToken  the JWT token identifying the service requester, optional if
     *                  the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param user      the updated user details
     * @return Response with status OK (200) in case of success or a BAD_REQUEST
     * error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @PUT
    public Response updateUser(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               UserAccount user) {
        LOGGER.debug("updateUser() - (ENTER)");

        ServiceRequest<UserAccount> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(user);

        Response ret;

        try {
            UserAccount updated = manageService.updateUser(request);
            ret = Response.status(Response.Status.OK).entity(updated).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateUser() - (LEAVE)");
        return ret;
    }

    /**
     * Changes the password of an existing user.<br/>
     * May be used either by an administrator to change the password of other
     * users or by a regular user to change his/her own password. When used to
     * change the password of another user the service requester must have a right
     * to manager-users, while when used to change a user own password the user
     * status may not be disabled and the current user password must be provided
     * in the request.
     *
     * @param jwtToken       the JWT token identifying the service requester, optional if
     *                       the service requester is authenticated by the J2EE container
     * @param roleName       the name of the Role of the selected UserContext (optional)
     * @param scopeName      the name of the Scope of the selected UserContext
     *                       (optional)
     * @param changePassword the request to change the password of a user
     * @return an OK status code in case of success or a BAD_REQUEST status code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR
     * status code in case an internal error prevented fulfilling the request or a
     * FORBIDDEN status code in case the end user is not authorised to perform the
     * operation or an UNAUTHORIZED status code in case the end user is not
     * authenticated
     */
    @PUT
    @Path("password")
    public Response changePassword(@HeaderParam("authorization") String jwtToken,
                                   @HeaderParam("roleName") String roleName,
                                   @HeaderParam("scopeName") String scopeName,
                                   ChangePassword changePassword) {
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

    /**
     * Retrieves the user with the provided user name
     *
     * @param jwtToken  the JWT token identifying the service requester, optional if
     *                  the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param userName  the requested user name
     * @return an OK status and the requested user, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("{userName}")
    public Response getUser(@HeaderParam("authorization") String jwtToken,
                            @HeaderParam("roleName") String roleName,
                            @HeaderParam("scopeName") String scopeName,
                            @PathParam("userName") String userName) {
        LOGGER.debug("getUser() - (ENTER)");

        ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        GetUserQuery query = new GetUserQuery();
        query.setUserName(userName);
        request.setBody(query);
        Response ret;

        try {
            UserAccount user = service.getUser(request);
            Status status;

            if (user == null) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(user).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getUser() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a user roles list according to the provided user name
     *
     * @param jwtToken  the JWT token authenticating the service requester
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param userName  the requested user name
     * @return an OK status and the requested user, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorized to perform the operation
     */
    @GET
    @Path("{userName}/userContexts")
    public Response getUserContexts(@HeaderParam("authorization") String jwtToken,
                                    @HeaderParam("roleName") String roleName,
                                    @HeaderParam("scopeName") String scopeName,
                                    @PathParam("userName") String userName) {
        LOGGER.debug("getUserContexts() - (ENTER)");

        ServiceRequest<FindUserContextsQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindUserContextsQuery query = new FindUserContextsQuery();
        query.setUserName(userName);
        request.setBody(query);
        Response ret;

        try {
            UserContextResponse ucr = userContextService.getUserContexts(request);
            Status status;

            if (ucr == null || ucr.getResults().isEmpty()) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(ucr).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getUserContexts() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing user context
     *
     * @param jwtToken      the JWT token identifying the service requester, optional if
     *                      the service requester is authenticated by the J2EE container
     * @param roleName      the name of the Role of the selected UserContext (optional)
     * @param scopeName     the name of the Scope of the selected UserContext
     *                      (optional)
     * @param userName      the userName of the target user (unused)
     * @param userContextId the identifier of the user role to be deleted
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @DELETE
    @Path("{userName}/userContexts/{userContextId}")
    public Response deleteUserContext(@HeaderParam("authorization") String jwtToken,
                                      @HeaderParam("roleName") String roleName,
                                      @HeaderParam("scopeName") String scopeName,
                                      @PathParam("userName") String userName,
                                      @PathParam("userContextId") String userContextId) {
        LOGGER.debug("deleteUserContext(" + userContextId + ") - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userContextId);
        Response ret;

        try {
            userContextService.deleteUserContext(request);
            ret = Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("deleteUserContext() - (LEAVE)");
        return ret;
    }

    /**
     * Creates a new user context
     *
     * @param jwtToken    the JWT token identifying the service requester, optional if
     *                    the service requester is authenticated by the J2EE container
     * @param roleName    the name of the Role of the selected UserContext (optional)
     * @param scopeName   the name of the Scope of the selected UserContext
     *                    (optional)
     * @param userName    the userName of the target user (unused)
     * @param userContext the new user role to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    @Path("{userName}/userContexts")
    public Response createUserContext(@HeaderParam("authorization") String jwtToken,
                                      @HeaderParam("roleName") String roleName,
                                      @HeaderParam("scopeName") String scopeName,
                                      @PathParam("userName") String userName,
                                      UserContext userContext) {
        LOGGER.debug("createUserContext() - (ENTER)");

        ServiceRequest<UserContext> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userContext);
        Response ret;

        try {
            UserContext createdRole = userContextService.createUserContext(request);

            Status status;
            if (createdRole.getUserContextId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(createdRole).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("createUserContext() - (LEAVE)");
        return ret;
    }

    /**
     * Update a user context
     *
     * @param jwtToken    the JWT token identifying the service requester, optional if
     *                    the service requester is authenticated by the J2EE container
     * @param roleName    the name of the Role of the selected UserContext (optional)
     * @param scopeName   the name of the Scope of the selected UserContext
     *                    (optional)
     * @param userName    the target user name (unused)
     * @param userContext the new user role to be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    @Path("{userName}/userContexts")
    public Response updateUserContext(@HeaderParam("authorization") String jwtToken,
                                      @HeaderParam("roleName") String roleName,
                                      @HeaderParam("scopeName") String scopeName,
                                      @PathParam("userName") String userName,
                                      UserContext userContext) {
        LOGGER.debug("updateUserContext() - (ENTER)");

        ServiceRequest<UserContext> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userContext);
        Response ret;

        try {
            UserContext updatedRole = userContextService.updateUserContext(request);

            Status status;
            if (updatedRole.getUserContextId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(updatedRole).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateUserContext() - (LEAVE)");
        return ret;
    }

    /**
     * Gets the list of all userNames, regardless of the status
     * (active/inactive, enabled/disabled/locked-out) of the users.
     *
     * @param jwtToken  the JWT token identifying the service requester, optional if
     *                  the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the non-empty list of all user names with an OK status OK, or a
     * NOT_FOUND status if the system contains no users; or a FORBIDDEN status if
     * the requester is not authenticated; or an UNAUTHORIZED status if the
     * requester is not authorised to use the service; or a BAD_REQUEST status
     * if the service request is null, empty, incomplete or otherwise invalid;
     * or an INTERNAL_SERVER_ERROR status if an internal error prevented
     * servicing the request.
     */
    @GET
    @Path("names")
    public Response getUsersNames(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getUsersNames() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody("");

        Response ret;
        try {
            List<String> lst = service.getUsersNames(request);
            Status status;

            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("getUsersNames() - (LEAVE)");
        return ret;
    }

    /**
     * Copy a list of a given contexts/preferences to a specified user
     *
     * @param jwtToken        the JWT token identifying the service requester, optional if
     *                        the service requester is authenticated by the J2EE container
     * @param roleName        the name of the Role of the selected UserContext (optional)
     * @param scopeName       the name of the Scope of the selected UserContext
     *                        (optional)
     * @param toUserName      the name of the user for which the copy operation will be
     *                        performed
     * @param userContextList The list with contexts which must be copied
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    @Path("{toUserName}/userPreferences")
    public Response copyUserProfiles(@HeaderParam("authorization") String jwtToken,
                                     @HeaderParam("roleName") String roleName,
                                     @HeaderParam("scopeName") String scopeName,
                                     @PathParam("toUserName") String toUserName,
                                     List<ComprehensiveUserContext> userContextList) {
        LOGGER.debug("copyUserProfiles() - (ENTER)");

        ServiceRequest<UserContextResponse> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        UserContextResponse ucr = new UserContextResponse();
        ucr.setResults(userContextList);
        request.setBody(ucr);
        Response ret;

        try {
            userContextService.copyUserProfiles(request, toUserName);

            ret = Response.status(Response.Status.OK).build();
        } catch (Exception ex) {
            ret = ExceptionHandler.handleException(ex);
        }

        LOGGER.debug("copyUserProfiles() - (LEAVE)");
        return ret;
    }

    /**
     * Get challenge information for a specified user
     *
     * @param jwtToken the JWT token identifying the service requester, optional
     *                 if the service requester is authenticated by the J2EE container
     * @param userName you are on your won dear
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @GET
    @Path("{userName}/challenges")
    public Response getChallenges(@HeaderParam("authorization") String jwtToken,
                                  @PathParam("userName") String userName) {
        LOGGER.debug("getChallenges() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setBody(userName);
        Response ret;

        try {
            ChallengeInformationResponse response = manageService.getChallengeInformation(request);

            Status status;
            if (response == null) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }

            ret = Response.status(status).entity(response).build();
        } catch (Exception ex) {
            ret = ExceptionHandler.handleException(ex);
        }

        LOGGER.debug("getChallenges() - (LEAVE)");
        return ret;
    }

    /**
     * Set challenge information for a specified user
     *
     * @param jwtToken                     the JWT token identifying the service requester, optional
     *                                     if the service requester is authenticated by the J2EE container
     * @param userName                     you are on your own dear
     * @param challengeInformationResponse you are on your own dear
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    @Path("{userName}/challenges")
    public Response setChallenges(@HeaderParam("authorization") String jwtToken,
                                  @PathParam("userName") String userName,
                                  ChallengeInformationResponse challengeInformationResponse) {
        LOGGER.debug("setChallenges() - (ENTER)");

        ServiceRequest<ChallengeInformationResponse> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setBody(challengeInformationResponse);

        Response ret;

        try {
            ChallengeInformationResponse response = manageService.setChallengeInformation(request, userName);

            ret = Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception ex) {
            ret = ExceptionHandler.handleException(ex);
        }

        LOGGER.debug("setChallenges() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a user preferences list according to the provided user name and the optional group name
     *
     * @param jwtToken  the JWT token authenticating the service requester
     * @param roleName  the name of the Role of the selected UserPreference (optional)
     * @param scopeName the name of the Scope of the selected UserPreference
     *                  (optional)
     * @param userName  the requested user name
     * @param groupName the requested group name
     * @return an OK status and the requested user preferences, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorized to perform the operation
     */
    @GET
    @Path("{userName}/userPreferences")
    public Response getUserPreferences(@HeaderParam("authorization") String jwtToken,
                                       @HeaderParam("roleName") String roleName,
                                       @HeaderParam("scopeName") String scopeName,
                                       @PathParam("userName") String userName,
                                       @QueryParam("groupName") String groupName) {
        LOGGER.debug("getUserPreferences() - (ENTER)");

        ServiceRequest<FindUserPreferenceQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindUserPreferenceQuery query = new FindUserPreferenceQuery();
        query.setUserName(userName);
        query.setGroupName(groupName);
        request.setBody(query);
        Response ret;

        try {
            UserPreferenceResponse upr = userPreferenceService.getUserPrefernces(request);
            Status status;

            if (upr == null || upr.getResults().isEmpty()) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(upr).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getUserPreferences() - (LEAVE)");
        return ret;
    }

    /**
     * Reset a userName's password
     *
     * @param userName Name of the userName that is asking for a password reset
     */
    @GET
    @Path("resetUserPassword")
    public Response resetUserPassword(@HeaderParam("userName") String userName)
            throws WebApplicationException {
        LOGGER.debug("resetUserPassword() - (ENTER)");

        Response ret = null;

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(userName);
        request.setBody(userName);
        try {
            // Execute
            ChallengeInformationResponse infoChallenge = manageService.getChallengeInformation(request);
            List<ChallengeInformation> challengeInformations = infoChallenge.getResults();

            List<String> response = new ArrayList<>();

            int countChallengeInformation = 0;

            for (int i = 1; i < challengeInformations.size() + 1; i++) {
                ChallengeInformation challengeInformation = challengeInformations.get(i - 1);
                //System.out.format("step x: %d - %s -> %s\n", i, challengeInformation.getChallenge(), challengeInformation.getResponse());
                if (challengeInformation.getChallenge() != null) {
                    countChallengeInformation++;
                    response.add(challengeInformation.getChallenge());
                }
            }

            Status status_ok;
            Status status_nok;

            status_ok = Response.Status.OK;
            status_nok = Response.Status.BAD_REQUEST;

            //
            // The backend checks if the userName should:
            //
            // - receive an email
            //
            // or
            //
            // - initiate a second stage in which the userName respond to security questions along with providing a new password
            //
            // and in the first case we just returns 200 - OK to the front-end while in the second case we return 200 with
            // an object containing the list of security questions to answer and send through another rest call.
            //
            if (countChallengeInformation <= 0) {
                //System.out.format("userName '%s' has NO challenge information! Email Notification is used\n", userName);
                if (userName.length() == 0) {
                    ret = Response.status(status_nok).build();
                } else {
                    NotificationQuery nq = new NotificationQuery();
                    ServiceRequest<NotificationQuery> requestRstPasswordNotify = new ServiceRequest<>();
                    requestRstPasswordNotify.setRequester(userName);
                    requestRstPasswordNotify.setBody(nq);
                    requestRstPasswordNotify.getBody().setUserName(userName);
                    //System.out.format("GET: resetUserPassword: %s, calling notify\n", userName);
                    manageService.resetPasswordAndNotify(requestRstPasswordNotify);

                    List<String> responseEmpty = new ArrayList<String>();
                    ResetPasswordResponse sar = new ResetPasswordResponse();
                    sar.setResults(responseEmpty);
                    ret = Response.status(status_ok).entity(sar).build();
                }
            } else {
                //System.out.format("userName '%s' has challenge information. Let's use them!\n", userName);
                ResetPasswordResponse sar = new ResetPasswordResponse();
                sar.setResults(response);
                ret = Response.status(status_ok).entity(sar).build();
            }

            //ret = Response.status(status).entity(upr).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("resetUserPassword() - (LEAVE)");
        return ret;
    }

    @PUT
    @Path("resetUserPassword")
    public Response resetUserPassword(@HeaderParam("userName") String userName,
                                      @HeaderParam("isTemporaryPassword") Boolean isTemporaryPassword,
                                      ChallengeInformationResponse challengeInformationResponse) {
        LOGGER.debug("resetUserPassword() - (ENTER)");

        //System.out.format("POST: resetUserPassword: %s, %s\n", userName, challengeInformationResponse.getUserPassword());
        ServiceRequest<ChallengeInformationResponse> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setBody(challengeInformationResponse);

        Response ret;

        try {
            ServiceRequest<ResetPasswordQuery> requestRstPassword = new ServiceRequest<>();
            requestRstPassword.setRequester(userName); //servletRequest.getRemoteUser());
            requestRstPassword.setBody(new ResetPasswordQuery());
            requestRstPassword.getBody().setUserName(userName);
            requestRstPassword.getBody().setChallenges(challengeInformationResponse.getResults());
            requestRstPassword.getBody().setPassword(challengeInformationResponse.getUserPassword());
            requestRstPassword.getBody().setIsTemporaryPassword(isTemporaryPassword);

            //System.out.format("userName -> %s\n", requestRstPassword.getBody().getUserName());
            //for (int i = 0; i < challengeInformationResponse.getResults().size(); i++) {
            //	System.out.format("challenge -> %s - %s\n", requestRstPassword.getBody().getChallenges().get(i).getChallenge(), requestRstPassword.getBody().getChallenges().get(i).getResponse());
            //}
            //System.out.format("password -> %s\n", requestRstPassword.getBody().getPassword());

            manageService.resetPassword(requestRstPassword);

            ret = Response.status(Response.Status.OK).build();
        } catch (Exception ex) {
            ret = ExceptionHandler.handleException(ex);
        }

        LOGGER.debug("resetUserPassword() - (LEAVE)");
        return ret;
    }

    @GET
    @Path("passpolicy")
    public Response getPasswordPolicy() {
        LOGGER.debug("getPasswordPolicy() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());

        Response ret;
        try {
            String passwordPolicy = manageService.getPasswordPolicy(request);

            Status status;

            if (passwordPolicy == null || passwordPolicy.length() <= 0) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.OK;
            }

            //ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            ResponseWrapper<String> response = new ResponseWrapper<>();
            response.setResult(passwordPolicy);
            //response.setResults(results);
            ret = Response.status(status).entity(response).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("getPasswordPolicy() - (LEAVE)");
        return ret;
    }

}
