/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.rest.service.policy;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;

public class PolicyRestClient {
	
	private final WebResource webResource;
	private final Client client;
	
	public PolicyRestClient(String uri) {
		ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri).path("policies");
	}
	
	public <T> T updatePolicy(Class<T> responseType,ServiceRequest<Policy> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).put(responseType,request.getBody());
	}

	public <T> T findPolicies(Class<T> respType, ServiceRequest<FindPoliciesQuery> req) 
			throws UniformInterfaceException {
		WebResource resource = webResource;

	    if (req.getBody() != null) {
	        if (req.getBody().getName() != null) {
	          resource = resource.queryParam("name", req.getBody().getName());
	        }
	        if (req.getBody().getSubject() != null) {
	          resource = resource.queryParam("subject", req.getBody().getSubject());
	        }
	    }
	    
	    return resource.type(MediaType.APPLICATION_JSON).
	            header("authorization", req.getRequester()).
	            get(respType);	    
	}
	
	public <T> T getSubjects(Class<T> responseType, ServiceRequest<NoBody> request) throws UniformInterfaceException {
		WebResource resource =  webResource;
		String path = MessageFormat.format("/{0}", new Object[]{"subjects"});
		resource = resource.path(path);
		
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}
}