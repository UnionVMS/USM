package eu.europa.ec.mare.usm.administration.rest.service.scope;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.administration.domain.*;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;

public class ScopeRestClient {

    private final WebResource webResource;
    private final Client client;

    public ScopeRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(uri).path("scopes");
    }

    public <T> T findScopes(Class<T> responseType, ServiceRequest<FindScopesQuery> request)
            throws UniformInterfaceException {

        WebResource resource = webResource;
        Paginator paginator = request.getBody().getPaginator();

        resource = resource.queryParam("offset", Integer.toString(paginator.getOffset())).
                queryParam("limit", Integer.toString(paginator.getLimit())).
                queryParam("sortColumn", paginator.getSortColumn()).
                queryParam("sortDirection", paginator.getSortDirection());

        if (request.getBody() != null) {
            if (request.getBody().getApplicationName() != null) {
                resource = resource.queryParam("application",
                        request.getBody().getApplicationName());
            }
            if (request.getBody().getScopeName() != null) {
                resource = resource.queryParam("name",
                        request.getBody().getScopeName());
            }
            if (request.getBody().getStatus() != null) {
                resource = resource.queryParam("status",
                        request.getBody().getStatus());
            }
        }

        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).
                get(responseType);
    }

    public <T> T getScope(Class<T> responseType, ServiceRequest<GetScopeQuery> request)
            throws UniformInterfaceException {
        String path = MessageFormat.format("/{0}",
                new Object[]{request.getBody().getScopeId().toString()});

        WebResource resource = webResource;
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T createScope(Class<T> responseType, ServiceRequest<Scope> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).post(responseType, request.getBody());
    }

    public <T> T updateScope(Class<T> responseType, ServiceRequest<Scope> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody());
    }

    public <T> T deleteScope(Class<T> responseType, ServiceRequest<String> request) throws UniformInterfaceException {
        String path = MessageFormat.format("/{0}",
                new Object[]{request.getBody()});
        WebResource resource = webResource;
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).delete(responseType);
    }

    public <T> T findDatasets(Class<T> responseType, ServiceRequest<FindDataSetQuery> request)
            throws UniformInterfaceException {
        String path = MessageFormat.format("/{0}", new Object[]{"datasets"});

        WebResource resource = webResource;
        resource = resource.path(path);
        resource = resource.queryParam("application", request.getBody().getApplicationName())
                .queryParam("category", request.getBody().getCategory());
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T getScopes(Class<T> responseType, ServiceRequest<ScopeQuery> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}", new Object[]{"names"});
        resource = resource.path(path);
        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }
}
