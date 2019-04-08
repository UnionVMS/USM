package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.administration.domain.*;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;

public class OrganisationRestClient {
  private final WebResource webResource;
  private final WebResource endpointWebResource;
  private final WebResource channelWebResource;
  private final WebResource contactWebResource;
  private final Client client;

  public OrganisationRestClient(String uri) 
  {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    client = Client.create(config);
    webResource = client.resource(uri).path("organisations");
    endpointWebResource = client.resource(uri).path("endpoint");
    channelWebResource = client.resource(uri).path("channel");
    contactWebResource = client.resource(uri).path("endpointcontact");
  }

  public <T> T getOrganisationNames(Class<T> responseType,
          ServiceRequest<OrganisationQuery> request)
          throws UniformInterfaceException {
    WebResource resource = webResource;
    resource = resource.path("/names");
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).
            get(responseType);
  }

  public <T> T getOrganisationParentNames(Class<T> responseType,
          ServiceRequest<Long> request)
          throws UniformInterfaceException {
    WebResource resource = webResource;
    resource = resource.path("/" + request.getBody() + "/parent/names");
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).
            get(responseType);
  }

  public <T> T getNationNames(Class<T> responseType,
          ServiceRequest<OrganisationQuery> request)
          throws UniformInterfaceException {
    WebResource resource = webResource;
    resource = resource.path("/nations/names");
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).
            get(responseType);
  }

  public <T> T findOrganisations(Class<T> responseType,
          ServiceRequest<FindOrganisationsQuery> request)
          throws UniformInterfaceException {
    WebResource resource = webResource;
    Paginator paginator = request.getBody().getPaginator();

    resource = resource.queryParam("offset", Integer.toString(paginator.getOffset())).
            queryParam("limit", Integer.toString(paginator.getLimit())).
            queryParam("sortColumn", paginator.getSortColumn()).
            queryParam("sortDirection", paginator.getSortDirection());

    if (request.getBody() != null) {
      if (request.getBody().getName() != null) {
        resource = resource.queryParam("name", request.getBody().getName());
      }
      if (request.getBody().getNation() != null) {
        resource = resource.queryParam("name", request.getBody().getNation());
      }
      if (request.getBody().getStatus() != null) {
        resource = resource.queryParam("status", request.getBody().getStatus());
      }
    }

    return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T getOrganisation(Class<T> responseType, ServiceRequest<Long> request)
          throws UniformInterfaceException {
    String path = MessageFormat.format("/{0}", request.getBody().toString());

    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T createOrganisation(Class<T> responseType, ServiceRequest<Organisation> request) throws UniformInterfaceException {
    WebResource resource = webResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).post(responseType, request.getBody());
  }

  public <T> T updateOrganisation(Class<T> responseType, ServiceRequest<Organisation> request) throws UniformInterfaceException {
    WebResource resource = webResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).put(responseType, request.getBody());
  }

  public <T> T deleteOrganisation(Class<T> responseType, ServiceRequest<String> request) throws UniformInterfaceException {
    String path = MessageFormat.format("/{0}", request.getBody());
    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).delete(responseType);
  }

  public <T> T getEndPoint(Class<T> responseType, ServiceRequest<Long> request)
          throws UniformInterfaceException {
    String path = MessageFormat.format("endpoint/{0}", request.getBody().toString());

    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T createEndPoint(Class<T> responseType, ServiceRequest<EndPoint> request) throws UniformInterfaceException {
    WebResource resource = endpointWebResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).post(responseType, request.getBody());
  }

  public <T> T updateEndPoint(Class<T> responseType, ServiceRequest<EndPoint> request) throws UniformInterfaceException {
    WebResource resource = endpointWebResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).put(responseType, request.getBody());
  }

  public <T> T deleteEndPoint(Class<T> responseType, ServiceRequest<String> request) throws UniformInterfaceException {
    String path = MessageFormat.format("/{0}", request.getBody());
    WebResource resource = endpointWebResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).delete(responseType);
  }

  public <T> T getChannel(Class<T> responseType, ServiceRequest<Long> request)
          throws UniformInterfaceException {
    String path = MessageFormat.format("channel/{0}", request.getBody().toString());

    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T createChannel(Class<T> responseType, ServiceRequest<Channel> request) throws UniformInterfaceException {
    WebResource resource = channelWebResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).post(responseType, request.getBody());
  }

  public <T> T updateChannel(Class<T> responseType, ServiceRequest<Channel> request) throws UniformInterfaceException {
    WebResource resource = channelWebResource;

    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).put(responseType, request.getBody());
  }

  public <T> T deleteChannel(Class<T> responseType, ServiceRequest<String> request) throws UniformInterfaceException {
    String path = MessageFormat.format("/{0}", request.getBody());
    WebResource resource = channelWebResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).delete(responseType);
  }

  public <T> T getEndpointContact(Class<T> responseType, ServiceRequest<Long> request)
          throws UniformInterfaceException {
    String path = MessageFormat.format("contact/{0}", request.getBody().toString());

    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T assignContact(Class<T> responseType, ServiceRequest<EndPointContact> request) throws UniformInterfaceException {
    WebResource resource = contactWebResource;
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).post(responseType, request.getBody());
  }

  public <T> T removeContact(Class<T> responseType, ServiceRequest<String> request) throws UniformInterfaceException {
    String path = MessageFormat.format("/{0}", request.getBody());
    WebResource resource = contactWebResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).delete(responseType);
  }

}
