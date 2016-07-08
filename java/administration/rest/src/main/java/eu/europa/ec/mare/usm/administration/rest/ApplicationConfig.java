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
  public Set<Class<?>> getClasses() {
    Set<Class<?>> resources = new HashSet<>();
    // following code to customize Jersey 1.x JSON provider:
    resources.add(JacksonJsonProvider.class);

    addRestResourceClasses(resources);
    return resources;
  }

  private void addRestResourceClasses(Set<Class<?>> resources) 
  {
    resources.add(eu.europa.ec.mare.usm.administration.rest.JacksonConfigurator.class);
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