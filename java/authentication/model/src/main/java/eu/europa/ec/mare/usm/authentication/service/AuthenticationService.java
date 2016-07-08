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
package eu.europa.ec.mare.usm.authentication.service;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

/**
 * Provides operations for user authentication and identity confirmation.
 */
public interface AuthenticationService {

  /**
   * Checks whether LDAP-based authentication is enabled.
   * 
   * @return <i>true</i> if LDAP-based authentication is enabled, <i>false</i> 
   * otherwise
   */
  public boolean isLDAPEnabled();
  
  /**
   * Checks whether user password is expired.
   * 
   * @return <i>true</i> if password is expired, <i>false</i> 
   * otherwise
   */
  public boolean isPasswordExpired(String userName);

  /**
   * Checks whether user password is about to expire.
   * 
   * @return <i>true</i> if password is about to expire, <i>false</i> 
   * otherwise
   */
  public boolean isPasswordAboutToExpire(String userName);

  /**
   * Asserts the identity of a user based on the provided user identifier 
   * and password.
   * 
   * @param request the user identifier and password
   * 
   * @return an AuthenticationResponse
   */
  public AuthenticationResponse authenticateUser(AuthenticationRequest request);
  
  /**
   * Retrieves a challenge suitable for confirming the identity of a user.
   * 
   * @param query the user identifier 
   * 
   * @return a challenge if the provided  user has an active user account 
   * with configured challenge response(s), <i>null</i> otherwise
   */
  public ChallengeResponse getUserChallenge(AuthenticationQuery query);
  
  /**
   * Confirms the identity of a user based on the challenge and user response.
   * 
   * @param request a challenge/response authentication request.
   * 
   * @return an AuthenticationResponse
   */
  public AuthenticationResponse authenticateUser(ChallengeResponse request);
}