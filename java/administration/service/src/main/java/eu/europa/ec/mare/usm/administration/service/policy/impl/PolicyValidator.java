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
package eu.europa.ec.mare.usm.administration.service.policy.impl;

import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;
import java.util.List;

import java.util.Properties;
import javax.inject.Inject;

/**
 * Validates Policy related service requests.
 */
public class PolicyValidator extends RequestValidator {
  private static final int MAX_VALUE_LEN = 128;
  
  @Inject
  private PolicyJdbcDao policyJdbcDao;

  /**
   * Asserts that the provided service request is valid and that the service
   * requester is allowed to use the 'configurePolicies' feature.
   *
   * @param request the service request to be validated
   *
   * @throws IllegalArgumentException if the service request is null, empty,
   * incomplete or otherwise invalid
   * @throws UnauthorisedException if the service requester is not allowed to
   * use the 'configurePolicies' feature
   */
  public void assertValid(ServiceRequest<PolicyDefinition> request)
  throws IllegalArgumentException, UnauthorisedException 
  {
    assertValid(request, USMFeature.configurePolicies, "policy");
    assertNotEmpty("subject", request.getBody().getSubject());
    Properties props = request.getBody().getProperties();

    assertNotNull("properties", props);
    for (String key : props.stringPropertyNames()) {
      String loc = "property[" + key + "]";
      assertNotNull(loc, props.getProperty(key));
      assertNotTooLong(loc, MAX_VALUE_LEN, props.getProperty(key));
    }
  }

  public void assertValidPolicyProperty(ServiceRequest<Policy> request)
  throws IllegalArgumentException, UnauthorisedException 
  {
    assertValid(request, USMFeature.configurePolicies, "policy");
    assertNotEmpty("name", request.getBody().getName());
    String name = request.getBody().getName();
    String value = request.getBody().getValue();
    String loc = "property[" + name + "]";
    assertNotNull(loc, value);
    assertNotTooLong(loc, MAX_VALUE_LEN, value);
    assertInList("subject", policyJdbcDao.getSubjects(), 
                 request.getBody().getSubject());
  }
}