package eu.europa.ec.mare.usm.information.rest;

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
        resources.add(eu.europa.ec.mare.usm.information.rest.service.InformationResource.class);
        resources.add(eu.europa.ec.mare.usm.information.rest.service.DeploymentResource.class);
    }

}
