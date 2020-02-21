package eu.europa.ec.mare.usm.information.rest.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;

import javax.json.bind.annotation.JsonbCreator;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST service client implementation of the InformationService interface.
 */
public class InformationRestClient {

    private final WebResource webResource;
    private final Client client;

    /**
     * Creates a new instance.
     *
     * @param uri the REST Service end-point URI. For example:
     *            http://localhost:8080/usm-information/rest/
     */
    public InformationRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JsonbCreator.class);
        client = Client.create(config);
        webResource = client.resource(uri);
    }

    public ContactDetails getContactDetails(String userName) throws RuntimeException {
        String path = MessageFormat.format("contactDetails/{0}",
                new Object[]{userName});

        ClientResponse response = webResource.path(path).
                accept(MediaType.APPLICATION_JSON).
                get(ClientResponse.class);
        ContactDetails ret = null;
        if (ClientResponse.Status.OK == response.getStatusInfo()) {
            ret = response.getEntity(ContactDetails.class);
        } else if (ClientResponse.Status.NOT_FOUND != response.getStatusInfo()) {
            handleError(response);
        }
        return ret;
    }

    public List<Organisation> findOrganisations(String nation) throws RuntimeException {
        String path = MessageFormat.format("organisation/nation/{0}",
                new Object[]{nation});

        ClientResponse response = webResource.path(path).
                accept(MediaType.APPLICATION_JSON).
                get(ClientResponse.class);
        List<Organisation> ret = null;

        if (ClientResponse.Status.OK == response.getStatusInfo()) {
            ret = response.getEntity(new GenericType<>() {
            });
        } else if (ClientResponse.Status.NO_CONTENT == response.getStatusInfo()) {
            ret = new ArrayList<>();
        } else if (ClientResponse.Status.NOT_FOUND != response.getStatusInfo()) {
            handleError(response);
        }
        return ret;
    }

    public Organisation getOrganisation(String organisationName) throws RuntimeException {
        String path = MessageFormat.format("organisation/{0}",
                new Object[]{organisationName});

        ClientResponse response = webResource.path(path).
                accept(MediaType.APPLICATION_JSON).
                get(ClientResponse.class);
        Organisation ret = null;
        if (ClientResponse.Status.OK == response.getStatusInfo()) {
            ret = response.getEntity(Organisation.class);
        } else if (ClientResponse.Status.NOT_FOUND != response.getStatusInfo()) {
            handleError(response);
        }
        return ret;
    }

    public UserContext getUserContext(UserContextQuery query) throws RuntimeException {
        StringBuilder path = new StringBuilder("userContext/");
        if (query.getApplicationName() != null) {
            path.append(query.getApplicationName()).append('/');
        }
        path.append(query.getUserName());
        ClientResponse response = webResource.path(path.toString()).
                accept(MediaType.APPLICATION_JSON).
                get(ClientResponse.class);

        UserContext ret = null;
        if (ClientResponse.Status.OK == response.getStatusInfo()) {
            ret = response.getEntity(UserContext.class);
        } else if (ClientResponse.Status.NOT_FOUND != response.getStatusInfo()) {
            handleError(response);
        }
        return ret;
    }

    public void updateUserPreferences(UserContext userContext) throws RuntimeException {
        ClientResponse response = webResource.path("userContext").
                type(MediaType.APPLICATION_JSON).
                put(ClientResponse.class, userContext);
        if (ClientResponse.Status.OK != response.getStatusInfo()) {
            handleError(response);
        }
    }

    private void handleError(ClientResponse response) throws RuntimeException {
        if (ClientResponse.Status.BAD_REQUEST == response.getStatusInfo()) {
            StatusResponse sr = response.getEntity(StatusResponse.class);
            throw new IllegalArgumentException(sr.getMessage());
        } else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == response.getStatusInfo()) {
            StatusResponse sr = response.getEntity(StatusResponse.class);
            throw new RuntimeException(sr.getMessage());
        }
    }
}
