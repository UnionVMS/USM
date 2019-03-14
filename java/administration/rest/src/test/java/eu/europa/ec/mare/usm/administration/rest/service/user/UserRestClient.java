package eu.europa.ec.mare.usm.administration.rest.service.user;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.ChallengeInformationResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.FindUserContextsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthenticatedException;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.domain.UserContext;
import eu.europa.ec.mare.usm.administration.domain.UserContextResponse;
import eu.europa.ec.mare.usm.administration.rest.common.StatusResponse;

public class UserRestClient {

	private final WebResource webResource;
	private final Client client;

	public UserRestClient(String uri) {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
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
	
	public <T> T getUser(Class<T> responseType, ServiceRequest<GetUserQuery> request) throws UniformInterfaceException {
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return  resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}
	
	public <T> T createUser(Class<T> responseType,ServiceRequest<UserAccount> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).post(responseType,request.getBody());
	} 
	
	public <T> T updateUser(Class<T> responseType,ServiceRequest<UserAccount> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).put(responseType,request.getBody());
	}
	
	public <T> T getUserContexts(Class<T> responseType, ServiceRequest<FindUserContextsQuery> request) throws UniformInterfaceException {
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return  resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}

	public void changePassword(ServiceRequest<ChangePassword> request) 
  throws UniformInterfaceException
  {
    try {
      webResource.path("password").
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
  
  public <T> T createUserContext(Class<T> responseType,ServiceRequest<UserContext> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).post(responseType,request.getBody());
	} 
	
	public <T> T updateUserContext(Class<T> responseType,ServiceRequest<UserContext> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userContexts", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).put(responseType,request.getBody());
	}
	
	public <T> T deleteUserContext(Class<T> responseType,String userName,ServiceRequest<String> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userContexts/{1}", new Object[]{userName,request.getBody()});
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).delete(responseType);
	}
	
	public <T> T copyUserProfiles(Class<T> responseType,ServiceRequest<UserContextResponse> request, String toUserName) throws UniformInterfaceException{
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userPreferences", new Object[]{toUserName});
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).put(responseType,request.getBody().getResults());
	}
	
    public <T> T getChallenges(Class<T> responseType, String userName, ServiceRequest<String> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/challenges", new Object[]{userName});
        resource = resource.path(path);
        return  resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }
    
    public <T> T setChallenges(Class<T> responseType, String userName, ServiceRequest<ChallengeInformationResponse> request) throws UniformInterfaceException {
        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}/challenges", new Object[]{userName});
        resource = resource.path(path);
        return  resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).put(responseType, request.getBody());
    }    

  ClientResponse getUserNames(ServiceRequest<String> request) 
  {
		return  webResource.path("/names").
                        type(MediaType.APPLICATION_JSON).
                        header("authorization", request.getRequester()).
                        get(ClientResponse.class);
  }
  
  public <T> T getUserPreferences(Class<T> responseType, ServiceRequest<FindUserPreferenceQuery> request) throws UniformInterfaceException {
		WebResource resource = webResource;
		String path = MessageFormat.format("/{0}/userPreferences", new Object[]{request.getBody().getUserName()});
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}
  
}
