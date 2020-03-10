package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.information.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InformationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformationResource.class);

    @EJB
    private InformationService service;

    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    /**
     * Retrieves a UserContext for the provided user and application
     *
     * @param applicationName the application name
     * @param userName        the user name
     * @return the UserContext with an OK status, or a NO_CONTENT error code
     * in case the UserContext does not exist, or a BAD_REQUEST error code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request
     */
    @GET
    @Path("userContext/{applicationName}/{userName}")
    public Response getUserContext(@PathParam("applicationName") String applicationName,
                                   @PathParam("userName") String userName) {
        LOGGER.info("getUserContext(" + applicationName + "," + userName + ") - (ENTER)");

        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(applicationName);
        query.setUserName(userName);

        Response response;
        try {
            UserContext ctx = service.getUserContext(query);

            if (ctx == null) {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            } else {
                String returnString = jsonb.toJson(ctx);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getUserContext() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves the UserContext for the specified user
     *
     * @param userName the user name
     * @return the UserContext with an OK status, or a NO_CONTENT error code
     * in case the UserContext does not exist, or a BAD_REQUEST error code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request
     */
    @GET
    @Path("userContext/{userName}")
    public Response getUserContext(@PathParam("userName") String userName) {
        LOGGER.info("getUserContext(" + userName + ") - (ENTER)");

        UserContextQuery query = new UserContextQuery();
        query.setUserName(userName);

        Response response;
        try {
            UserContext ctx = service.getUserContext(query);

            if (ctx == null) {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            } else {
                String returnString = jsonb.toJson(ctx);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getUserContext() - (LEAVE)");
        return response;
    }

    /**
     * Updates or creates the preferences held in the provided
     * UserContext.
     *
     * @param content JSON representation of the UserContext
     * @return an OK status, or a BAD_REQUEST error code in case the provided
     * input is incomplete, or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request
     */
    @PUT
    @Path("userContext")
    public Response putUserContext(UserContext content) {
        LOGGER.info("putUserContext(" + content + ") - (ENTER)");

        Response response;
        try {
            service.updateUserPreferences(content);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("putUserContext() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves information about all organisations associated with the
     * provided nation.
     *
     * @param nation the nation name
     * @return the matching Organisations with an OK status, or a NO_CONTENT
     * status code if there are no organisations associated with the nation,
     * or a BAD_REQUEST error code in case the provided input is incomplete,
     * or an  INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request
     */
    @GET
    @Path("organisation/nation/{nation}")
    public Response findOrganisations(@PathParam("nation") String nation) {
        LOGGER.info("findOrganisations(" + nation + ") - (ENTER)");

        Response response;
        try {
            List<Organisation> organisationList = service.findOrganisations(nation);

            if (organisationList.isEmpty()) {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            } else {
                String returnString = jsonb.toJson(organisationList);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("findOrganisations() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves the Organisation with the provided name.
     *
     * @param organisationName the organisation name
     * @return the Organisation with an OK status, or a BAD_REQUEST error code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request
     */
    @GET
    @Path("organisation/{organisationName}")
    public Response getOrganisation(@PathParam("organisationName") String organisationName) {
        LOGGER.info("getOrganisation(" + organisationName + ") - (ENTER)");

        Response response;
        try {
            Organisation organisation = service.getOrganisation(organisationName);

            if (organisation == null) {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            } else {
                String returnString = jsonb.toJson(organisation);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getOrganisation() - (LEAVE)");
        return response;
    }

    /**
     * Retrieves the ContactDetails of the user with the provided name.
     *
     * @param userName the user name
     * @return the ContactDetails with an OK status, or a BAD_REQUEST error code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request
     */
    @GET
    @Path("contactDetails/{userName}")
    public Response getContactDetails(@PathParam("userName") String userName) {
        LOGGER.info("getContactDetails(" + userName + ") - (ENTER)");

        Response response;

        try {
            ContactDetails contactDetails = service.getContactDetails(userName);

            if (contactDetails == null) {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            } else {
                String returnString = jsonb.toJson(contactDetails);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.info("getContactDetails() - (LEAVE)");
        return response;
    }

}
