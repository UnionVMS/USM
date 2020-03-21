package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.information.service.DeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.json.bind.Jsonb;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service implementation of the (Application) Deployment Service
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("deployments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeploymentResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentResource.class);

    @EJB
    private DeploymentService deploymentService;

    @Context
    private HttpServletRequest servletRequest;

    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    /**
     * Retrieves the Deployment Descriptor for the application with the
     * provided name
     *
     * @param applicationName the Application Name
     * @return an OK status and the requested Descriptor in case of success or
     * a BAD_REQUEST status code in case the provided input is incomplete, or
     * an INTERNAL_SERVER_ERROR status code in case an internal error prevented
     * fulfilling the request or a FORBIDDEN status code in case the end user is
     * not authorised to perform the operation or an UNAUTHORIZED status code in
     * case the end user is not authenticated
     */
    @GET
    @Path("{applicationName}")
    public Response getDeployment(@PathParam("applicationName") String applicationName) {
        LOGGER.debug("getDeployment(" + applicationName + ") - (ENTER)");

        Response response;
        try {
            Application application = deploymentService.getDeploymentDescriptor(applicationName);
            if (application != null) {
                String returnString = jsonb.toJson(application);
                response = Response.ok(returnString).type(MediaType.APPLICATION_JSON).build();
            } else {
                response = Response.noContent().type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            response = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("getDeployment() - (LEAVE)");
        return response;
    }

    /**
     * Deploys the provided Application as a new Application.
     *
     * @param application the new application to be deployed
     * @return an OK status code in case of success or a BAD_REQUEST status code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR
     * status code in case an internal error prevented  fulfilling the request
     * or a FORBIDDEN status code in case the end user is not authorised to
     * perform the operation or an UNAUTHORIZED status code in case the end user
     * is not authenticated
     */
    @POST
    @Path("")
    public Response deployApplication(Application application) {
        LOGGER.debug("deployApplication(" + application + ") - (ENTER)");

        Response response;
        try {
            deploymentService.deployApplication(application);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response = ExceptionHandler.handleException(e);
        }
        LOGGER.debug("deployApplication() - (LEAVE)");
        return response;
    }

    /**
     * Updates an application
     *
     * @param application the application to be updated
     * @return an OK status code in case of success or a BAD_REQUEST status code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR
     * status code in case an internal error prevented  fulfilling the request
     * or a FORBIDDEN status code in case the end user is not authorised to
     * perform the operation or an UNAUTHORIZED status code in case the end user
     * is not authenticated
     */
    @PUT
    @Path("")
    public Response redeployApplication(Application application) {
        LOGGER.debug("redeployApplication(" + application + ") - (ENTER)");

        Response response;
        try {
            deploymentService.redeployApplication(application);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response = ExceptionHandler.handleException(e);
        }
        LOGGER.debug("redeployApplication() - (LEAVE)");
        return response;
    }

    /**
     * Deploys the provided Datasets to an existing Application.
     *
     * @param application the application to be updated
     * @return an OK status code in case of success or a BAD_REQUEST status code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR
     * status code in case an internal error prevented  fulfilling the request
     * or a FORBIDDEN status code in case the end user is not authorised to
     * perform the operation or an UNAUTHORIZED status code in case the end user
     * is not authenticated
     */
    @PUT
    @Path("datasets")
    public Response deployDatasets(Application application) {
        LOGGER.debug("deployDatasets(" + application + ") - (ENTER)");

        Response response;
        try {
            deploymentService.deployDatasets(application);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response = ExceptionHandler.handleException(e);
        }
        LOGGER.debug("deployDatasets() - (LEAVE)");
        return response;
    }


    /**
     * Un-deploys the provided Application.
     *
     * @param applicationName the name of the application to be un-deployed
     * @return an OK status code in case of success or a BAD_REQUEST status code
     * in case the provided input is incomplete, or an INTERNAL_SERVER_ERROR
     * status code in case an internal error prevented  fulfilling the request
     * or a FORBIDDEN status code in case the end user is not authorised to
     * perform the operation or an UNAUTHORIZED status code in case the end user
     * is not authenticated
     */
    @DELETE
    @Path("{applicationName}")
    public Response undeployApplication(@PathParam("applicationName") String applicationName) {
        LOGGER.debug("undeployApplication(" + applicationName + ") - (ENTER)");

        Response response;
        try {
            deploymentService.undeployApplication(applicationName);
            response = Response.ok().type(MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response = ExceptionHandler.handleException(e);
        }
        LOGGER.debug("undeployApplication() - (ENTER)");
        return response;
    }
}
