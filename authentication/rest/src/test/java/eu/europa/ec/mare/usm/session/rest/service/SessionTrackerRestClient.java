package eu.europa.ec.mare.usm.session.rest.service;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.authentication.domain.StatusResponse;
import eu.europa.ec.mare.usm.session.domain.SessionIdWrapper;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;

/**
 * (Test) Client for the REST service implementation of the SessionTracker.
 */
public class SessionTrackerRestClient  {
	private final WebResource webResource;
	private final Client client;

  /**
   * Creates a new instance.
   * 
   * @param uri the REST service end-point URI
   */
	public SessionTrackerRestClient(String uri) 
  {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri);
	}

  
	public String startSession(SessionInfo request) 
  {
		ClientResponse cr = webResource.path("/sessions").
                                    type(MediaType.APPLICATION_JSON).
                                    post(ClientResponse.class, request);
    String ret = null;
    
    if (ClientResponse.Status.OK == cr.getClientResponseStatus()) {
      SessionIdWrapper wrapper = cr.getEntity(SessionIdWrapper.class);
      ret = wrapper.getSessionId() ;
    } else {
      handleError(cr);
    }
    
    return ret;
	}

  
	public SessionInfo getSession(String userNameToken) 
  {
		ClientResponse cr = webResource.path("/sessions/" + userNameToken).
                                    accept(MediaType.APPLICATION_JSON).
                                    get(ClientResponse.class);

    SessionInfo ret = null;
    if (ClientResponse.Status.OK == cr.getClientResponseStatus()) {
      ret = cr.getEntity(SessionInfo.class);
    } else {
      handleError(cr);
    }
    
    return ret;
	}
	
  
	public void endSession(String userNameToken) 
  {
		ClientResponse cr = webResource.path("/sessions/" + userNameToken).
                                    delete(ClientResponse.class);

    if (ClientResponse.Status.OK != cr.getClientResponseStatus()) {
      handleError(cr);
    }
	}
	
  private void handleError(ClientResponse cr) 
  throws IllegalArgumentException, RuntimeException 
  {
    if (ClientResponse.Status.BAD_REQUEST  == cr.getClientResponseStatus()) {
      StatusResponse sr = cr.getEntity(StatusResponse.class);
      throw new IllegalArgumentException(sr.getMessage());
    } else if (ClientResponse.Status.CONFLICT  == cr.getClientResponseStatus()) {
      StatusResponse sr = cr.getEntity(StatusResponse.class);
      throw new IllegalStateException(sr.getMessage());
    } else if (ClientResponse.Status.INTERNAL_SERVER_ERROR  == cr.getClientResponseStatus()) {
      StatusResponse sr = cr.getEntity(StatusResponse.class);
      throw new RuntimeException(sr.getMessage());
    }
  }

}