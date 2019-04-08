package eu.europa.ec.mare.usm.administration.rest.service.user;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;

public class LdapUserRestClient {

	private final WebResource webResource;
	private final Client client;

	public LdapUserRestClient(String uri) {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri).path("ldap");
	}
	
	public <T> T getUser(Class<T> responseType, ServiceRequest<GetUserQuery> request) throws UniformInterfaceException {
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return  resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}

}