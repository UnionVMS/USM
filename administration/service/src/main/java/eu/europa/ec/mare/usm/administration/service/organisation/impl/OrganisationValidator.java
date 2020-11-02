package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Organisation
 * related service requests
 */
@Stateless
public class OrganisationValidator extends RequestValidator {
    private static final String ENABLED = "E";
    private static final String DISABLED = "D";
    private static final String[] STATUS_LOV = {ENABLED, DISABLED};

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

        assertNotEmpty("uri", ep.getUri());
        assertNotTooLong("uri", 256, ep.getUri());

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

    public void assertValidEndpoint(ServiceRequest<EndPointContact> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "endPointContact");

        EndPointContact epc = request.getBody();

        if (!isCreate) {
            assertNotNull("endPointContactId", epc.getEndPointContactId());
        } else {
            assertNotNull("endPointId", epc.getEndPointId());
            assertNotNull("contactId", epc.getPersonId());
        }
    }
}
