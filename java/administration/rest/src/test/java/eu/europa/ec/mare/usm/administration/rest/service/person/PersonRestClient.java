package eu.europa.ec.mare.usm.administration.rest.service.person;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.Person;

import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ResponseWrapper;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;

import java.util.List;

/**
 * Java Client for the REST service facade to the Person Service.
 */
public class PersonRestClient  {
  private final WebResource webResource;
  private final Client client;

  /**
   * Creates a new instance.
   * 
   * @param uri the REST service URI prefix
   */
  public PersonRestClient(String uri) 
  {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    client = Client.create(config);
    webResource = client.resource(uri).path("persons");
  }

  public List<Person> getPersons(ServiceRequest<NoBody> request)
  {
    ClientResponse r = webResource.path("/names").
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    List<Person> ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      GenericType< ServiceArrayResponse<Person>> gType;
      gType = new GenericType< ServiceArrayResponse<Person>>() {};
      ServiceArrayResponse<Person> sar = r.getEntity(gType);
      ret = sar.getResults();
    }
    
    return ret;
  }
  
  public Person getPerson(ServiceRequest<Long> request) 
  {
    ClientResponse r = webResource.path("/" + request.getBody()).
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    Person ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(Person.class);
    }
    
    return ret;
  }

  public ContactDetails getContactDetails(ServiceRequest<String> request) 
  {
    ClientResponse r = webResource.path("/contactDetails/" + request.getBody()).
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    ContactDetails ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(ContactDetails.class);
    }
    
    return ret;
  }

  
  public Boolean isUpdateContactDetailsEnabled(ServiceRequest<NoBody> request) 
  {
    ClientResponse r = webResource.path("/isUpdateContactDetailsEnabled").
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    Boolean ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      GenericType<ResponseWrapper<Boolean>> gType;
      gType = new GenericType< ResponseWrapper<Boolean>>() {};
      ResponseWrapper<Boolean> sar = r.getEntity(gType);
      ret = sar.getResult();
    }
    
    return ret;
  }
          
  public ContactDetails updateContactDetails(ServiceRequest<ContactDetailsRequest> request) 
  {
    ClientResponse r = webResource.path("/contactDetails").
                   header("Authorization", request.getRequester()).
                   type(MediaType.APPLICATION_JSON).
                   accept(MediaType.APPLICATION_JSON).
                   put(ClientResponse.class, request.getBody());
        
    ContactDetails ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(ContactDetails.class);
    }
    
    return ret;
  }


  public Boolean isReviewContactDetailsEnabled(ServiceRequest<NoBody> request) 
  {
    ClientResponse r = webResource.path("/isReviewContactDetailsEnabled").
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    Boolean ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      GenericType<ResponseWrapper<Boolean>> gType;
      gType = new GenericType< ResponseWrapper<Boolean>>() {};
      ResponseWrapper<Boolean> sar = r.getEntity(gType);
      ret = sar.getResult();
    }
    
    return ret;
  }
          
  public List<PendingContactDetails> findPendingContactDetails(ServiceRequest<NoBody> request)
  {
    ClientResponse r = webResource.path("/pendingContactDetails/").
                                header("Authorization", request.getRequester()).
                                type(MediaType.APPLICATION_JSON).
                                get(ClientResponse.class);

    List<PendingContactDetails> ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      GenericType< ServiceArrayResponse<PendingContactDetails>> gType;
      gType = new GenericType< ServiceArrayResponse<PendingContactDetails>>() {};
      ServiceArrayResponse<PendingContactDetails> sar = r.getEntity(gType);
      ret = sar.getResults();
    }
    
    return ret;
  }
  
  public PendingContactDetails getPendingContactDetails(ServiceRequest<String> request) 
  {
    ClientResponse r = webResource.path("/pendingContactDetails/" + request.getBody()).
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    PendingContactDetails ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(PendingContactDetails.class);
    }
    
    return ret;
  }

  public ContactDetails acceptPendingContactDetails(ServiceRequest<String> request) 
  {
    ClientResponse r = webResource.path("/pendingContactDetails/" + request.getBody() + 
                                        "/accept").
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    ContactDetails ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(ContactDetails.class);
    }
    
    return ret;
  }

  public ContactDetails rejectPendingContactDetails(ServiceRequest<String> request) 
  {
    ClientResponse r = webResource.path("/pendingContactDetails/" + request.getBody() + 
                                        "/reject").
                           header("Authorization", request.getRequester()).
                           type(MediaType.APPLICATION_JSON).
                           get(ClientResponse.class);

    ContactDetails ret = null;
    if (Response.Status.OK.getStatusCode() == r.getStatus()) {
      ret = r.getEntity(ContactDetails.class);
    }
    
    return ret;
  }

}
