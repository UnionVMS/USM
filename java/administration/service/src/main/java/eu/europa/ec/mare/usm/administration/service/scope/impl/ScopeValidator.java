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
package eu.europa.ec.mare.usm.administration.service.scope.impl;

import eu.europa.ec.mare.usm.administration.domain.Scope;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;

/**
 * Provides operations for the validation and authorisation of Scope related
 * service requests
 */
public class ScopeValidator extends RequestValidator {

  /**
   * Creates a new instance.
   */
  public ScopeValidator() {
  }

  public void assertValid(ServiceRequest<Scope> request,
                            USMFeature feature, boolean isCreate) 
  {
    assertValid(request, feature, "scope");

    Scope scope = request.getBody();
    assertNotEmpty("scopeName", scope.getName());
    assertNotEmpty("status", scope.getStatus());
    assertNotNull("activeTo", scope.getActiveTo());
    assertNotNull("activeFrom", scope.getActiveFrom());
    assertValidPeriod("active", scope.getActiveFrom(), scope.getActiveTo());
    assertValidPeriod("data", scope.getDataFrom(), scope.getDataTo());
    if (!isCreate) {
      assertNotNull("scopeId", scope.getScopeId());
    }
  }
}