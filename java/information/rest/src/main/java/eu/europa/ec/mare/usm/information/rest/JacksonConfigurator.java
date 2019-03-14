package eu.europa.ec.mare.usm.information.rest;

import javax.ws.rs.ext.ContextResolver;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Produces;

/**
 * Configures Jackson JSON provider to format/parse dates in ISO 8601 format.
 */
@Provider
@Produces("application/json")
public class JacksonConfigurator implements ContextResolver<ObjectMapper> {
  private final ObjectMapper mapper;

  /**
   * Creates a new instance.
   */
  public JacksonConfigurator() 
  {
    mapper = new ObjectMapper();
    mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, 
                     false);
  }

  @Override
  public ObjectMapper getContext(Class<?> arg0) 
  {
    return mapper;
  }
}
