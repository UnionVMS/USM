package eu.europa.ec.mare.usm.administration.rest;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(JsonBConfigurator.class);
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.application.ApplicationResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.authentication.AuthenticationResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.organisation.ChannelResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.organisation.EndpointContactResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.organisation.EndpointResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.organisation.OrganisationResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.person.PersonResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.policy.PolicyResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.role.RoleResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.scope.ScopeResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.user.LdapUserResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.user.UserProfileResource.class);
        resources.add(eu.europa.ec.mare.usm.administration.rest.service.user.UserResource.class);
    }

}
