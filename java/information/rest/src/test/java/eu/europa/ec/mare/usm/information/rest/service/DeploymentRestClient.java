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
package eu.europa.ec.mare.usm.information.rest.service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.service.DeploymentService;

/**
 * REST client implementation of the DeploymentService interface
 */
public class DeploymentRestClient implements DeploymentService {
	private final WebResource webResource;
	private final Client client;

	public DeploymentRestClient(String uri) {
		ClientConfig config = new DefaultClientConfig();

		try {
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					}, DomainTrustManager.getSslContext()));
		} catch (Exception e) {
		}

		config.getClasses().add(JacksonJsonProvider.class);
		client = Client.create(config);
		webResource = client.resource(uri).path("deployments");
	}

	@Override
	public Application getDeploymentDescriptor(String request) throws IllegalArgumentException, RuntimeException {
		ClientResponse r = webResource.path(request).type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);

		Application ret = null;

		if (ClientResponse.Status.OK == r.getClientResponseStatus()) {
			ret = r.getEntity(Application.class);
		} else if (ClientResponse.Status.BAD_REQUEST == r.getClientResponseStatus()) {
			if (MediaType.APPLICATION_XML_TYPE == r.getType()) {
				StatusResponse sr = r.getEntity(StatusResponse.class);
				throw new IllegalArgumentException(sr.getMessage());
			}
			throw new IllegalArgumentException();
		} else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == r.getClientResponseStatus()) {
			if (MediaType.APPLICATION_XML_TYPE == r.getType()) {
				StatusResponse sr = r.getEntity(StatusResponse.class);
				throw new RuntimeException(sr.getMessage());
			}
			StatusResponse sr = r.getEntity(StatusResponse.class);
			throw new RuntimeException(sr.getMessage());
		}

		return ret;
	}

	@Override
	public void deployApplication(Application request) throws IllegalArgumentException, RuntimeException {
		try {
			webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(request);
		} catch (UniformInterfaceException e) {
			handleException(e);
		}
	}

	@Override
	public void redeployApplication(Application request) throws IllegalArgumentException, RuntimeException {
		try {
			webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(request);
		} catch (UniformInterfaceException e) {
			handleException(e);
		}
	}

	@Override
	public void undeployApplication(String request) throws IllegalArgumentException, RuntimeException {
		try {
			webResource.path(request).accept(MediaType.APPLICATION_XML).delete();
		} catch (UniformInterfaceException e) {
			handleException(e);
		}
	}

	@Override
	public void deployDatasets(Application request) throws IllegalArgumentException, RuntimeException {
		try {
			webResource.path("datasets").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(request);
		} catch (UniformInterfaceException e) {
			handleException(e);
		}
	}

	private void handleException(UniformInterfaceException e) throws IllegalArgumentException, RuntimeException {
		ClientResponse r = e.getResponse();
		if (ClientResponse.Status.BAD_REQUEST == r.getClientResponseStatus()) {
			throw new IllegalArgumentException();
		} else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == r.getClientResponseStatus()) {
			throw new RuntimeException("");
		}
	}

}