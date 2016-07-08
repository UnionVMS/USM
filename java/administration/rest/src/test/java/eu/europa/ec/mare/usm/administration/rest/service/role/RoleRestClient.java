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
package eu.europa.ec.mare.usm.administration.rest.service.role;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.ComprehensiveRole;
import eu.europa.ec.mare.usm.administration.domain.FindPermissionsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindRolesQuery;
import eu.europa.ec.mare.usm.administration.domain.GetRoleQuery;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.RoleQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;

public class RoleRestClient {

  private final WebResource webResource;
  private final Client client;

  public RoleRestClient(String uri) {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    client = Client.create(config);
    webResource = client.resource(uri).path("roles");
  }

  public <T> T getRoleNames(Class<T> responseType,
          ServiceRequest<RoleQuery> request) throws UniformInterfaceException {
    WebResource resource = webResource;
    String path = MessageFormat.format("/{0}", new Object[]{"names"});
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T findRoles(Class<T> responseType,
                            ServiceRequest<FindRolesQuery> request)
  throws UniformInterfaceException 
  {
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
      if (request.getBody().getRoleName() != null) {
        resource = resource.queryParam("role", 
                                       request.getBody().getRoleName());
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

  public <T> T getRole(Class<T> responseType,
          ServiceRequest<GetRoleQuery> request) 
  throws UniformInterfaceException 
  {
    String path = MessageFormat.format("/{0}",
            new Object[]{request.getBody().getRoleId().toString()});
  
    WebResource resource = webResource;
    resource = resource.path(path);
    return resource.type(MediaType.APPLICATION_JSON).
            header("authorization", request.getRequester()).get(responseType);
  }

  public <T> T createRole(Class<T> responseType,ServiceRequest<ComprehensiveRole> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).post(responseType,request.getBody());
	} 
	
	public <T> T updateRole(Class<T> responseType,ServiceRequest<ComprehensiveRole> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).put(responseType,request.getBody());
	}
	
	public <T> T deleteRole(Class<T> responseType,ServiceRequest<String> request) throws UniformInterfaceException{
		String path = MessageFormat.format("/{0}",
	            new Object[]{request.getBody()});
		WebResource resource = webResource;
		resource = resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).delete(responseType);
	}
	
	public <T> T getFeatureGroupNames(Class<T> responseType,ServiceRequest<String> request) throws UniformInterfaceException{
		WebResource resource = webResource;
		 String path = MessageFormat.format("/{0}/{1}/{2}", new Object[]{"features","group","names"});
		 resource.path(path);
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}
	
	public <T> T findPermissions(Class<T> responseType, ServiceRequest<FindPermissionsQuery> request) throws UniformInterfaceException{
		String path = MessageFormat.format("/{0}", new Object[]{"permissions"});
		WebResource resource = webResource;
		resource=resource.path(path);
		
		 if (request.getBody() != null) {
		      if (request.getBody().getApplication() != null) {
		        resource = resource.queryParam("application", 
		                                     request.getBody().getApplication());
		      }
		      if (request.getBody().getGroup() != null) {
		        resource = resource.queryParam("group", 
		                                       request.getBody().getGroup());
		      }
		    }
		 
		return resource.type(MediaType.APPLICATION_JSON).
				header("authorization", request.getRequester()).get(responseType);
	}
	
	public <T> T getRoles(Class<T> responseType,
	         ServiceRequest<RoleQuery> request) throws UniformInterfaceException {
	   WebResource resource = webResource;
	   String path = MessageFormat.format("/{0}", new Object[]{"comprehensives"});
	   resource = resource.path(path);
	   return resource.type(MediaType.APPLICATION_JSON).
	           header("authorization", request.getRequester()).get(responseType);
	 }
}