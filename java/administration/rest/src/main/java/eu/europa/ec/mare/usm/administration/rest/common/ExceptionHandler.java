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
package eu.europa.ec.mare.usm.administration.rest.common;

import eu.europa.ec.mare.usm.administration.domain.UnauthenticatedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import javax.ejb.EJBException;

public class ExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * Logs the provided exception and generates a pertinent HTTP response.
   * 
   * @param exc the exception to be handled.
   * 
   * @return an HTTP response with a FORBIDDEN status if the requester is 
   * not authenticated; or an UNAUTHORIZED status if the requester is not 
   * authorised to use the service; or a BAD_REQUEST status if the service 
   * request is null, empty, incomplete or otherwise invalid; or an 
   * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the 
   * request.
   */
  public static Response handleException(Exception exc) 
  {
    Throwable cause = getCause(exc);

    StatusResponse msg = new StatusResponse();
    msg.setMessage(cause.getMessage());
    Status status = Response.Status.INTERNAL_SERVER_ERROR;
    
    if (cause instanceof UnauthenticatedException) {
      LOGGER.info("Unauthenticated request: " + cause.getMessage(), cause);
      status = Response.Status.FORBIDDEN;
    } else if (cause instanceof UnauthorisedException) {
      LOGGER.info("Unauthorised request: " + cause.getMessage(), cause);
      status = Response.Status.UNAUTHORIZED;
    } else if (cause instanceof IllegalArgumentException) {
      LOGGER.info("Bad request: " + cause.getMessage());
      status = Response.Status.BAD_REQUEST;
    } else if (cause instanceof RuntimeException) {
      LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
    }else if (cause instanceof IllegalStateException) {
      LOGGER.error("Illegal request: " + cause.getMessage(), cause);
    } else {
      LOGGER.error("Unknown Error: " + exc.getMessage(), exc);
      msg.setMessage(exc.getMessage());
    }

    msg.setStatusCode(status.getStatusCode());
    Response ret = Response.status(status).entity(msg).build();

    return ret;
  }
  
  private static Throwable getCause(Throwable src)
  {
    Throwable ret = src;
    
    if (src != null && 
        src instanceof EJBException && 
        src.getCause() != null) {
      ret = getCause(src.getCause());
    }
    
    return ret;
  }
}