/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.rest;

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