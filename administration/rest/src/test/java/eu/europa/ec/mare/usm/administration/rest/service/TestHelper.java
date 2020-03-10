package eu.europa.ec.mare.usm.administration.rest.service;

import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class TestHelper {

    @EJB
    private AdministrationRestClient restClient;

    public EndPoint findOrganisationEndPoint(String organisationName, String endpointName, String jwtToken) {
        List<EndPoint> endPoints = findOrganisationEndPoints(jwtToken, organisationName);
        return endPoints.stream().filter(ep -> ep.getName().equals(endpointName)).findAny().orElse(new EndPoint());
    }

    public List<EndPoint> findOrganisationEndPoints(String jwtToken, String organisationName) {
        long orgId = getOrganisationIdByName(jwtToken, organisationName);
        Response response = restClient.getOrganisationById(jwtToken, String.valueOf(orgId));
        Organisation organisation = response.readEntity(Organisation.class);
        return organisation.getEndpoints();
    }

    public long getOrganisationIdByName(String jwtToken, String orgName) {
        Response response = restClient.findOrganisations(jwtToken, orgName);
        PaginationResponse<Organisation> sar = response.readEntity(new javax.ws.rs.core.GenericType<>() {
        });
        List<Organisation> organisations = sar.getResults();
        return organisations.get(0).getOrganisationId();
    }
}
