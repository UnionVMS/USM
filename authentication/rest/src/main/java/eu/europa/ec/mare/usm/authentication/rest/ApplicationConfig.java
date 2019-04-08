package eu.europa.ec.mare.usm.authentication.rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

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
