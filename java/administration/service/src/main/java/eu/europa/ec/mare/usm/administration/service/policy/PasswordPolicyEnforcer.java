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
package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import java.util.Date;

/**
 * Enforces password related policies.
 */
public interface PasswordPolicyEnforcer {

  /**
   * Asserts that the provided request to set the password of a user
   * complies with all applicable policies.
   * 
   * @param request a request to set the password of a user
   * 
   * @return the password expiry date for the newly set password
   *
   * @throws IllegalArgumentException in case the provided subject is null, 
   * empty or incomplete or if the user does not exist or if the password 
   * violates one or more of the applicable policies
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Date assertValid(ServiceRequest<ChangePassword> request)
  throws IllegalArgumentException, RuntimeException;

  public String getPasswordPolicy();
}