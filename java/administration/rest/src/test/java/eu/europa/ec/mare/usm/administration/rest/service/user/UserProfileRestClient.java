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
package eu.europa.ec.mare.usm.administration.rest.service.user;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthenticatedException;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;

public class UserProfileRestClient {

	private final WebResource webResource;
	private final Client client;

	public UserProfileRestClient(String uri) {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri).path("profile");
	}
	
	public void changePassword(ServiceRequest<ChangePassword> request) 
  throws UniformInterfaceException
  {
    try {
      webResource.path("userPassword").
                  type(MediaType.APPLICATION_JSON).
                  accept(MediaType.APPLICATION_JSON).
                  header("authorization", request.getRequester()).
                  put(ClientResponse.class,request.getBody());
    } catch (UniformInterfaceException e) {
      handleException(e);
    }
	}

  private void handleException(UniformInterfaceException e) 
  throws IllegalArgumentException, UnauthenticatedException, 
         UnauthorisedException, RuntimeException 
  {
    ClientResponse r = e.getResponse();
    
    if (ClientResponse.Status.BAD_REQUEST == r.getClientResponseStatus()) {
      throw new IllegalArgumentException(getMessage(r));
    } else if (ClientResponse.Status.UNAUTHORIZED == r.getClientResponseStatus()) {
      throw new UnauthenticatedException(getMessage(r));
    } else if (ClientResponse.Status.FORBIDDEN == r.getClientResponseStatus()) {
      throw new UnauthorisedException(getMessage(r));
    } else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == r.getClientResponseStatus()) {
      throw new RuntimeException(getMessage(r));
    }
  }

  private String getMessage(ClientResponse r)
  {
    String ret = "<unknown>";

    try {
      StatusResponse a = r.getEntity(StatusResponse.class);
      ret = a.getMessage();
    } catch (ClientHandlerException | UniformInterfaceException e) {
      // NOP
    }
    
    return ret;
  }
  
}