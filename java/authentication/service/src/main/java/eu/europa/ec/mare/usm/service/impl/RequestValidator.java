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
package eu.europa.ec.mare.usm.service.impl;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;

/**
 * Service request validator.
 */
public class RequestValidator  {
  private static final String MUST_BE_DEFINED = " must be defined";
  
  public void assertValid(SessionInfo request) 
  {
    assertNotNull("request", request);
    assertNotEmpty("userName", request.getUserName());
    assertNotTooLong("userName",64, request.getUserName());
    assertNotEmpty("userSite", request.getUserSite());
    assertNotTooLong("userSite",256, request.getUserSite());
  }

  public void assertValid(AuthenticationQuery request) 
  {
    assertNotNull("request", request);
    assertNotEmpty("request.userName", request.getUserName());
  }

  public void assertValid(AuthenticationRequest request) 
  {
    assertNotNull("request", request);
    assertNotEmpty("request.userName", request.getUserName());
    assertNotEmpty("request.password", request.getPassword());
  }

  public void assertValid(ChallengeResponse request) 
  {
    assertNotNull("request", request);
    assertNotEmpty("request.userName", request.getUserName());
    assertNotEmpty("request.challenge", request.getChallenge());
    assertNotEmpty("request.response", request.getResponse());
  }

  public void assertNotTooLong(String name, int maxLen, String value) 
  {
    if (value != null && value.length() > maxLen) {
      throw new IllegalArgumentException(name + " is too long (max " + maxLen + ")");
    }
  }
  
  public void assertNotEmpty(String name, String value) 
  {
    assertNotNull(name, value);
    if (value.trim().length() == 0) {
      throw new IllegalArgumentException(name + MUST_BE_DEFINED);
    }
  }

  public void assertNotNull(String name, Object value) 
  {
    if (value == null) {
      throw new IllegalArgumentException(name + MUST_BE_DEFINED);
    }
  }

}