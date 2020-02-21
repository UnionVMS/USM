package eu.europa.ec.mare.usm.administration.rest.service.user;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthenticatedException;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;

import javax.ws.rs.core.MediaType;

public class UserProfileRestClient {

    private final WebResource webResource;
    private final Client client;

    public UserProfileRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(uri).path("profile");
    }

    public void changePassword(ServiceRequest<ChangePassword> request) throws UniformInterfaceException {
        try {
            webResource.path("userPassword").
                    type(MediaType.APPLICATION_JSON).
                    accept(MediaType.APPLICATION_JSON).
                    header("authorization", request.getRequester()).
                    put(ClientResponse.class, request.getBody());
        } catch (UniformInterfaceException e) {
            handleException(e);
        }
    }

    private void handleException(UniformInterfaceException e) throws RuntimeException {
        ClientResponse r = e.getResponse();

        if (ClientResponse.Status.BAD_REQUEST == r.getStatusInfo()) {
            throw new IllegalArgumentException(getMessage(r));
        } else if (ClientResponse.Status.UNAUTHORIZED == r.getStatusInfo()) {
            throw new UnauthenticatedException(getMessage(r));
        } else if (ClientResponse.Status.FORBIDDEN == r.getStatusInfo()) {
            throw new UnauthorisedException(getMessage(r));
        } else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == r.getStatusInfo()) {
            throw new RuntimeException(getMessage(r));
        }
    }

    private String getMessage(ClientResponse r) {
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
