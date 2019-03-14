package eu.europa.ec.mare.usm.information.rest;

import eu.europa.ec.mare.usm.information.rest.service.InformationResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 *
 */
@ApplicationPath("rest")
public class ApplicationConfig extends Application {

  @Override
  public Set<Class<?>> getClasses() 
  {
    Set<Class<?>> resources = new HashSet<>();
    // following code to customize Jersey 1.x JSON provider:
    resources.add(JacksonJsonProvider.class);

    addRestResourceClasses(resources);
    return resources;
  }

  private void addRestResourceClasses(Set<Class<?>> resources) 
  {
    resources.add(eu.europa.ec.mare.usm.information.rest.JacksonConfigurator.class);
    resources.add(eu.europa.ec.mare.usm.information.rest.service.InformationResource.class);
    resources.add(eu.europa.ec.mare.usm.information.rest.service.DeploymentResource.class);
  }
  
}
