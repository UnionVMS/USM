package eu.europa.ec.mare.usm.authentication.rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;

/**
 *
 */
@ApplicationPath("rest")
public class ApplicationConfig extends Application {

  @Override
  public Set<Class<?>> getClasses() 
  {
    Set<Class<?>> resources = new HashSet<>();
    resources.add(JsonBConfigurator.class);

    addRestResourceClasses(resources);
    return resources;
  }

  /**
   * Do not modify addRestResourceClasses() method.
   * It is automatically populated with
   * all resources defined in the project.
   * If required, comment out calling this method in getClasses().
   */
  private void addRestResourceClasses(Set<Class<?>> resources) 
  {
    resources.add(eu.europa.ec.mare.usm.authentication.rest.AdministrationResource.class);
    resources.add(eu.europa.ec.mare.usm.authentication.rest.service.AuthenticationResource.class);
    resources.add(eu.europa.ec.mare.usm.session.rest.service.SessionTrackerResource.class);
  }
  
}
