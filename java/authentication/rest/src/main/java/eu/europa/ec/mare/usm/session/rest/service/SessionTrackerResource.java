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
package eu.europa.ec.mare.usm.session.rest.service;

import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.authentication.rest.ExceptionHandler;
import eu.europa.ec.mare.usm.authentication.rest.StatusResponse;
import eu.europa.ec.mare.usm.session.service.SessionTracker;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST service interface for the SessionTracker
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("sessions")
public class SessionTrackerResource {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SessionTrackerResource.class);

	@EJB
	SessionTracker service;

	/**
	 * Creates a new instance
	 */
	public SessionTrackerResource() {
	}

	/**
	 * Informs the tracker that a (new) user session is starting.
	 * 
	 * @param request
	 *            the identification of the starting user session
	 * 
	 * @return the unique identifier of the starting user session with an OK
	 *         status, or a BAD_REQUEST status if the provided service request
	 *         is invalid; or an INTERNAL_SERVER_ERROR status in case an
	 *         internal error prevented processing the request.
	 */
	@POST
	@Path("")
	@Produces("application/json")
	@Consumes("application/json")
	public Response startSession(SessionInfo request) {
		LOGGER.info("startSession() - (ENTER)");

		Response ret=null;

		try {
			String sessionId = service.startSession(request);
			if (sessionId != null) {
				SessionIdWrapper response = new SessionIdWrapper();
				response.setSessionId(sessionId);
				ret = Response.status(Response.Status.OK).entity(response)
						.build();
			}else{
				throw new IllegalStateException(
						"Maximum number of sessions exceeded");
			}
		} catch (Exception exc) {
			ret = ExceptionHandler.handleException(exc);
		}

		LOGGER.info("startSession() - (LEAVE)");
		return ret;
	}

	/**
	 * Retrieves (identification) information about the user session with the
	 * provided unique identifier.
	 * 
	 * @param sessionId
	 *            the unique identifier of the terminating user session
	 * 
	 * @return the session identification information with an OK status if the
	 *         session (still) exists; or a NOT_FOUND status if the session
	 *         (no-longer) exists; or a BAD_REQUEST status if the provided
	 *         service request is invalid; or an INTERNAL_SERVER_ERROR status in
	 *         case an internal error prevented processing the request.
	 */
	@GET
	@Path("{sessionId}")
	@Produces("application/json")
	public Response getSession(@PathParam("sessionId") String sessionId) {
		LOGGER.info("getSession() - (ENTER)");

		Response ret;

		try {
			SessionInfo response = service.getSession(sessionId);

			if (response != null) {
				ret = Response.status(Response.Status.OK).entity(response)
						.build();
			} else {
				ret = Response.status(Response.Status.NOT_FOUND).build();
			}
		} catch (Exception exc) {
			ret = ExceptionHandler.handleException(exc);
		}

		LOGGER.info("getSession() - (LEAVE)");
		return ret;
	}

	/**
	 * Informs the tracker that a user session is terminating.
	 * 
	 * @param sessionId
	 *            the unique identifier of the terminating user session
	 * 
	 * @return an empty response with OK status, or a BAD_REQUEST status if the
	 *         provided service request is invalid; or an INTERNAL_SERVER_ERROR
	 *         status in case an internal error prevented processing the
	 *         request.
	 */
	@DELETE
	@Path("{sessionId}")
	@Produces("application/json")
	public Response endSession(@PathParam("sessionId") String sessionId) {
		LOGGER.info("endSession() - (ENTER)");

		Response ret;

		try {
			service.endSession(sessionId);
			ret = Response.status(Response.Status.OK).build();
		} catch (Exception exc) {
			ret = ExceptionHandler.handleException(exc);
		}

		LOGGER.info("endSession() - (LEAVE)");
		return ret;
	}
}