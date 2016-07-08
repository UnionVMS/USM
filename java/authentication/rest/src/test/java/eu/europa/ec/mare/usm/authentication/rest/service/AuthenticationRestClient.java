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
package eu.europa.ec.mare.usm.authentication.rest.service;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

public class AuthenticationRestClient {
	private final WebResource webResource;
	private final Client client;

	public AuthenticationRestClient(String uri) 
  {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri);
	}

	public AuthenticationResponse authenticateUser(AuthenticationRequest request) 
  throws UniformInterfaceException 
  {
		return webResource.path("/authenticate").
                       type(MediaType.APPLICATION_JSON).
                       post(AuthenticationResponse.class, request);
	}

	public ChallengeResponse getUserChallenge(String userNameToken) 
  throws UniformInterfaceException 
  {
		return webResource.path("/challenge").
                       accept(MediaType.APPLICATION_JSON).
                       header("authorization", userNameToken).
                       get(ChallengeResponse.class);
	}
	
	public AuthenticationResponse authenticateUser(ChallengeResponse request) 
  throws UniformInterfaceException 
  {
		return webResource.path("/challengeauth").
                      type(MediaType.APPLICATION_JSON).
                      post(AuthenticationResponse.class, request);
	}

}