package eu.europa.ec.mare.usm.administration.rest.service.person;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ResponseWrapper;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.person.PersonService;
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
@Path("persons")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);

    @EJB
    private PersonService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Retrieves the list of all persons (dead or alive) held in the system
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @return the list of persons or a NO_CONTENT status code in case the list
     * of persons is empty or an INTERNAL_SERVER_ERROR error code in
     * case an internal error prevented fulfilling the request
     */
    @GET
    @Path("names")
    public Response getPersons(@HeaderParam("Authorization") String credential,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getPersons() - (ENTER)");

        Response ret;
        try {
            List<Person> lst = service.getPersons();

            if (lst == null || lst.isEmpty()) {
                ret = Response.status(Response.Status.NO_CONTENT).build();
            } else {
                ServiceArrayResponse<Person> response = new ServiceArrayResponse<>();
                response.setResults(lst);
                ret = Response.status(Response.Status.OK).entity(response).build();
            }

        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getPersons() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the person with the provided unique identifier
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @param personId   the person unique identifier
     * @return the person if it exists or a NOT_FOUND status code in case the
     * person does not exist; or a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("{personId}")
    public Response getPerson(@HeaderParam("Authorization") String credential,
                              @HeaderParam("roleName") String roleName,
                              @HeaderParam("scopeName") String scopeName,
                              @PathParam("personId") Long personId) {
        LOGGER.debug("getPerson() - (ENTER)");
        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(personId);

        Response ret;
        try {
            Person response = service.getPerson(request);

            if (response == null) {
                ret = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                ret = Response.status(Response.Status.OK).entity(response).build();
            }
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getPerson() - (LEAVE) ");
        return ret;
    }

    /**
     * Updates the contact details of a user.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param details    the contact details to be updated
     * @return the contact details; or a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @PUT
    @Path("contactDetails")
    public Response updateContactDetails(@HeaderParam("Authorization") String credential,
                                         ContactDetailsRequest details) {
        LOGGER.debug("updateContactDetails(" + details + ") - (ENTER)");

        ServiceRequest<ContactDetails> request = new ServiceRequest<>();
        request.setRequester(details.getUserName());
        request.setPassword(details.getPassword());
        request.setBody(details);

        Response ret;
        try {
            ContactDetails response = service.updateContactDetails(request);

            Status status = Response.Status.OK;
            ret = Response.status(status).entity(response).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateContactDetails() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the contact details of the user with the provided user-name.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param userName   the user-name
     * @return the contact details; or a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("contactDetails/{userName}")
    public Response getContactDetails(@HeaderParam("Authorization") String credential,
                                      @PathParam("userName") String userName) {
        LOGGER.debug("getContactDetails() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setBody(userName);
        Response ret;

        try {
            ContactDetails response = service.getContactDetails(request);

            ret = Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getContactDetails() - (LEAVE)");
        return ret;
    }

    /**
     * Checks whether the Update Contact Details feature is enabled.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @return true if the feature is enabled, false otherwise.
     */
    @GET
    @Path("isUpdateContactDetailsEnabled")
    public Response isUpdateContactDetailsEnabled(@HeaderParam("Authorization") String credential) {
        LOGGER.debug("isUpdateContactDetailsEnabled() - (ENTER)");

        Response ret;

        try {
            Boolean flag = service.isUpdateContactDetailsEnabled();
            ResponseWrapper<Boolean> response = new ResponseWrapper<>();
            response.setResult(flag);

            ret = Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("isUpdateContactDetailsEnabled() - (LEAVE)");
        return ret;
    }

    /**
     * Checks whether the Review Contact Details feature is enabled.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @return true if the feature is enabled, false otherwise.
     */
    @GET
    @Path("isReviewContactDetailsEnabled")
    public Response isReviewContactDetailsEnabled(@HeaderParam("Authorization") String credential) {
        LOGGER.debug("isReviewContactDetailsEnabled() - (ENTER)");

        Response ret;
        try {
            Boolean flag = service.isReviewContactDetailsEnabled();
            ResponseWrapper<Boolean> response = new ResponseWrapper<>();
            response.setResult(flag);

            ret = Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("isReviewContactDetailsEnabled() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the list of pending contact details.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @return the list of pending contact details or a NO_CONTENT status code
     * in case the list is empty; or a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("pendingContactDetails/")
    public Response findPendingContactDetails(@HeaderParam("Authorization") String credential,
                                              @HeaderParam("roleName") String roleName,
                                              @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("findPendingContactDetails() - (ENTER)");

        ServiceRequest<NoBody> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);

        Response ret;
        try {
            List<PendingContactDetails> lst = service.findPendingContactDetails(request);

            if (lst == null || lst.isEmpty()) {
                ret = Response.status(Response.Status.NO_CONTENT).build();
            } else {
                ServiceArrayResponse<PendingContactDetails> response = new ServiceArrayResponse<>();
                response.setResults(lst);
                ret = Response.status(Response.Status.OK).entity(response).build();
            }

        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("findPendingContactDetails() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the pending contact details of the user with the
     * provided name.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @param userName   the userName of the user
     * @return the pending contact details if they exist or a NOT_FOUND status
     * code in case pending contact details do not exist for the user;
     * or a FORBIDDEN status if the requester is not authenticated;
     * or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("pendingContactDetails/{userName}")
    public Response getPendingContactDetails(@HeaderParam("Authorization") String credential,
                                             @HeaderParam("roleName") String roleName,
                                             @HeaderParam("scopeName") String scopeName,
                                             @PathParam("userName") String userName) {
        LOGGER.debug("getPendingContactDetails() - (ENTER)");
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userName);

        Response ret;
        try {
            PendingContactDetails response = service.getPendingContactDetails(request);

            if (response == null) {
                ret = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                ret = Response.status(Response.Status.OK).entity(response).build();
            }
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getPendingContactDetails() - (LEAVE) ");
        return ret;
    }

    /**
     * Accepts the pending contact details update of the user with the
     * provided name.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @param userName   the userName of the user
     * @return the accepted contact details or a BAD_REQUEST status
     * code in case pending contact details do not exist for the user;
     * or a FORBIDDEN status if the requester is not authenticated;
     * or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("pendingContactDetails/{userName}/accept")
    public Response acceptPendingContactDetails(@HeaderParam("Authorization") String credential,
                                                @HeaderParam("roleName") String roleName,
                                                @HeaderParam("scopeName") String scopeName,
                                                @PathParam("userName") String userName) {
        LOGGER.debug("acceptPendingContactDetails() - (ENTER)");
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userName);

        Response ret;
        try {
            ContactDetails response = service.acceptPendingContactDetails(request);

            if (response == null) {
                ret = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                ret = Response.status(Response.Status.OK).entity(response).build();
            }
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("acceptPendingContactDetails() - (LEAVE) ");
        return ret;
    }

    /**
     * Rejects the pending contact details update of the user with the
     * provided name.
     *
     * @param credential credentials identifying the service requester, optional
     *                   if the service requester is authenticated by the J2EE container
     * @param roleName   the name of the Role of the selected UserContext (optional)
     * @param scopeName  the name of the Scope of the selected UserContext
     *                   (optional)
     * @param userName   the userName of the user
     * @return the accepted contact details or a NOT_FOUND status
     * code in case pending contact details do not exist for the user;
     * or a FORBIDDEN status if the requester is not authenticated;
     * or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    @GET
    @Path("pendingContactDetails/{userName}/reject")
    public Response rejectPendingContactDetails(@HeaderParam("Authorization") String credential,
                                                @HeaderParam("roleName") String roleName,
                                                @HeaderParam("scopeName") String scopeName,
                                                @PathParam("userName") String userName) {
        LOGGER.debug("rejectPendingContactDetails() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(userName);

        Response ret;
        try {
            ContactDetails response = service.rejectPendingContactDetails(request);

            if (response == null) {
                ret = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                ret = Response.status(Response.Status.OK).entity(response).build();
            }
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("rejectPendingContactDetails() - (LEAVE) ");
        return ret;
    }

}
