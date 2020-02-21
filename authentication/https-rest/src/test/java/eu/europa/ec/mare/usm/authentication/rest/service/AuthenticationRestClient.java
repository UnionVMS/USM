package eu.europa.ec.mare.usm.authentication.rest.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

import javax.ws.rs.core.MediaType;

public class AuthenticationRestClient {
    private final WebResource webResource;
    private final Client client;

    public AuthenticationRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(uri);
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest request) throws UniformInterfaceException {
        return webResource.path("/authenticate").
                type(MediaType.APPLICATION_JSON).
                post(AuthenticationResponse.class, request);
    }

    public ChallengeResponse getUserChallenge(String userNameToken) throws UniformInterfaceException {
        return webResource.path("/challenge").
                accept(MediaType.APPLICATION_JSON).
                header("authorization", userNameToken).
                get(ChallengeResponse.class);
    }

    public AuthenticationResponse authenticateUser(ChallengeResponse request) throws UniformInterfaceException {
        return webResource.path("/challengeauth").
                type(MediaType.APPLICATION_JSON).
                post(AuthenticationResponse.class, request);
    }
}
