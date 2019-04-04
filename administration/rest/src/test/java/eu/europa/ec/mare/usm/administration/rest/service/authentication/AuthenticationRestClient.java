package eu.europa.ec.mare.usm.administration.rest.service.authentication;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
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

	public AuthenticationJwtResponse authenticateUser(AuthenticationRequest request) 
  throws UniformInterfaceException 
  {
		return webResource.path("/authenticate").
                       type(MediaType.APPLICATION_JSON).
                       post(AuthenticationJwtResponse.class, request);
	}

	public ChallengeResponse getUserChallenge(String userNameToken) 
  throws UniformInterfaceException 
  {
		return webResource.path("/challenge").
                       accept(MediaType.APPLICATION_JSON).
                       header("authorization", userNameToken).
                       get(ChallengeResponse.class);
	}
	
	public AuthenticationJwtResponse authenticateUser(ChallengeResponse request) 
  throws UniformInterfaceException 
  {
		return webResource.path("/challengeauth").
                      type(MediaType.APPLICATION_JSON).
                      post(AuthenticationJwtResponse.class, request);
	}

}
