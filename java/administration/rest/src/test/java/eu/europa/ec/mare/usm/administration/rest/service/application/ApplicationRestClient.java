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
package eu.europa.ec.mare.usm.administration.rest.service.application;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import eu.europa.ec.mare.usm.administration.domain.FindScopesQuery;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.mare.usm.administration.domain.ApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.FindApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;

public class ApplicationRestClient {
    private final WebResource webResource;
    private final Client client;

    public ApplicationRestClient(String uri) {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(config);
        webResource = client.resource(uri).path("applications");
    }

    public <T> T findApplications(Class<T> responseType, ServiceRequest<FindApplicationQuery> request) throws UniformInterfaceException {
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
        }

        return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T getApplicationNames(Class<T> responseType, ServiceRequest<ApplicationQuery> request)
            throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path("/names");
        return resource.type(MediaType.APPLICATION_JSON).header("authorization", request.getRequester()).get(responseType);
    }

    public <T> T getApplicationFeatures(Class<T> responseType, ServiceRequest<String> request)
            throws UniformInterfaceException {

        WebResource resource = webResource;
        String path = MessageFormat.format("/{0}",
                new Object[]{request.getBody()});
        resource = resource.path(path);
        path = MessageFormat.format("/{0}",
                new Object[]{"features"});
        resource = resource.path(path);

        return resource.type(MediaType.APPLICATION_JSON).
                header("authorization", request.getRequester()).get(responseType);
    }
}