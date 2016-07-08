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
package eu.europa.ec.mare.usm.administration.service.ldap;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.LdapUser;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for the retrieval of user information from an
 * LDAP compatible Identity Management system.
 */
public interface LdapUserInfoService {
  /**
   * Checks whether the retrieval of user information from an
   * LDAP compatible Identity Management system is enabled,
   *
   * @return <i>true</i> if it is enabled, <i>false</i> otherwise
   */
  public boolean isEnabled();

  /**
   * Gets user details from the configured LDAP compatible Identity 
   * Management system, for the user with the provided name.
   *
   * @param request holds the details of the user to be updated
   * 
   * @return the requested user details if the user exists, or <i>null</i> 
   * if the feature is disabled or the user does not exist
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public LdapUser getLdapUserInfo(ServiceRequest<GetUserQuery> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}