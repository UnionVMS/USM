package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;

import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Stateless
public class DeploymentRestClient extends BuildInformationRestDeployment {

    private static final String DEPLOYMENTS = "deployments";

    public Application getApplicationByName(String applicationName) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS)
                .path(applicationName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Application.class);
    }


}
