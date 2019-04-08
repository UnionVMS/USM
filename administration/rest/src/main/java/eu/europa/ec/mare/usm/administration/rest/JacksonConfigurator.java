package eu.europa.ec.mare.usm.administration.rest;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, 
                     false);
  }

  @Override
  public ObjectMapper getContext(Class<?> arg0) 
  {
    return mapper;
  }
}
