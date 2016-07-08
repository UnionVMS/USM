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
package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import eu.europa.ec.mare.usm.administration.domain.Channel;
import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of Organisation
 * related service requests
 */
public class OrganisationValidator extends RequestValidator {
	private static final String ENABLED = "E";
	private static final String DISABLED = "D";
	private static final String[] STATUS_LOV = { ENABLED, DISABLED };

	/**
	 * Creates a new instance.
	 */
	public OrganisationValidator() {
	}

	public void assertValid(ServiceRequest<Organisation> request, USMFeature feature, boolean isCreate) {
		assertValid(request, feature, "organisation");

		Organisation org = request.getBody();
		assertNotEmpty("name", org.getName());
		assertNotTooLong("name", 128, org.getName());

		assertNotEmpty("nation", org.getNation());
		assertNotTooLong("nation", 9, org.getNation());

		assertNotEmpty("status", org.getStatus());
		assertNotTooLong("status", 1, org.getStatus());
		assertInList("status", STATUS_LOV, org.getStatus());

		assertNotTooLong("description", 512, org.getDescription());
		assertNotTooLong("email", 64, org.getEmail());

		if (!isCreate) {
			assertNotNull("organisationId", org.getOrganisationId());
		}
	}

	public void assertValidEndPoint(ServiceRequest<EndPoint> request, USMFeature feature, boolean isCreate) {
		assertValid(request, feature, "endPoint");

		EndPoint ep = request.getBody();
		assertNotEmpty("name", ep.getName());
		assertNotTooLong("name", 128, ep.getName());

		assertNotEmpty("URI", ep.getURI());
		assertNotTooLong("URI", 256, ep.getURI());

		assertNotEmpty("status", ep.getStatus());
		assertNotTooLong("status", 1, ep.getStatus());
		assertInList("status", STATUS_LOV, ep.getStatus());
		
		assertNotEmpty("organisationName", ep.getOrganisationName());
	
		assertNotTooLong("description", 512, ep.getDescription());
		assertNotTooLong("email", 64, ep.getEmail());

		if (!isCreate) {
			assertNotNull("endPointId", ep.getEndpointId());
		}
		
		
	}
	public void assertValidChannel(ServiceRequest<Channel> request, USMFeature feature, boolean isCreate) {
		assertValid(request, feature, "channel");

		Channel ep = request.getBody();
		assertNotEmpty("dataflow", ep.getDataflow());
		assertNotTooLong("dataflow", 255, ep.getDataflow());

		assertNotEmpty("service", ep.getService());
		assertNotTooLong("service", 64, ep.getService());

		assertNotNull("priority", ep.getPriority());
				
		assertNotNull("endPointId", ep.getEndpointId());
		
		if (!isCreate) {
			assertNotNull("channelId", ep.getChannelId());
		}
	}
	
	public void assertValidEndpoint(ServiceRequest<EndPointContact> request, USMFeature feature, boolean isCreate){
		
		assertValid(request, feature, "endPointContact");
		
		EndPointContact epc=request.getBody();

		if(!isCreate){
			assertNotNull("endPointContactId",epc.getEndPointContactId());
		} else {
            assertNotNull("endPointId", epc.getEndPointId());
            assertNotNull("contactId",epc.getPersonId());
        }
	}
}