package eu.europa.ec.mare.usm.administration.rest.service.user;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;

public class UserRestClient {

    private final WebResource webResource;
    private final Client client;

    public UserRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(uri).path("users");
    }

    public <T> T findUsers(Class<T> responseType, ServiceRequest<FindUsersQuery> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        FindUsersQuery query = request.getBody();
        Paginator paginator = query.getPaginator();
        resource = resource.queryParam("offset", Integer.toString(paginator.getOffset())).
                queryParam("limit", Integer.toString(paginator.getLimit())).
                queryParam("sortColumn", paginator.getSortColumn()).
                queryParam("sortDirection", paginator.getSortDirection()).
                queryParam("user", query.getName()).
                queryParam("organisation", query.getOrganisation());
        return resource.type(MediaType.APPLICATION_JSON)
                .header("authorization", request.getRequester())
                .header("roleName", request.getRoleName())
                .header("scopeName", request.getScopeName())
                .get(responseType);
    }

    public <T> T getUser(Class<T> responseType, ServiceRequest<GetUserQuery> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}", new Object[]{request.getBody().getUserName()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T createUser(Class<T> responseType, ServiceRequest<UserAccount> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).post(responseType, request.getBody());
    }

    public <T> T updateUser(Class<T> responseType, ServiceRequest<UserAccount> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody());
    }

    public <T> T getUserContexts(Class<T> responseType, ServiceRequest<FindUserContextsQuery> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }

    public void changePassword(ServiceRequest<ChangePassword> request) throws UniformInterfaceException {
        try {
            webResource.path("password").
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

    public <T> T createUserContext(Class<T> responseType, ServiceRequest<UserContext> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).post(responseType, request.getBody());
    }

    public <T> T updateUserContext(Class<T> responseType, ServiceRequest<UserContext> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody());
    }

    public <T> T deleteUserContext(Class<T> responseType, String userName, ServiceRequest<String> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userContexts/{1}", new Object[]{userName, request.getBody()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).delete(responseType);
    }

    public <T> T copyUserProfiles(Class<T> responseType, ServiceRequest<UserContextResponse> request, String toUserName)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userPreferences", new Object[]{toUserName});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody().getResults());
    }

    public <T> T getChallenges(Class<T> responseType, String userName, ServiceRequest<String> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/challenges", new Object[]{userName});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T setChallenges(Class<T> responseType, String userName, ServiceRequest<ChallengeInformationResponse> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/challenges", new Object[]{userName});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody());
    }

    ClientResponse getUserNames(ServiceRequest<String> request) {
        return webResource.path("/names").
                type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).
                get(ClientResponse.class);
    }

    public <T> T getUserPreferences(Class<T> responseType, ServiceRequest<FindUserPreferenceQuery> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/userPreferences", new Object[]{request.getBody().getUserName()});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }
}
