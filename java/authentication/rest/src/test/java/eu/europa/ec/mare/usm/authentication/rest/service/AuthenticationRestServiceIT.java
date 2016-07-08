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
package eu.europa.ec.mare.usm.authentication.rest.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test for the Authentication REST Service.
 */
public class AuthenticationRestServiceIT {
  private static final String PASSWORD = "password";
  private static final String RESPONSE = "Tintin";
  private static final String CHALLENGE = "Name of first pet";
  private static final String USER_VMS_ADMIN = "vms_admin_com";
  private static final String USER_VMS = "vms_user_com";
  private AuthenticationRestClient testSubject = null;
  private final String endPoint;

  /**
   * Creates a new instance.
   * 
   * @throws IOException if class-path resource /test.properties can't be 
   * accessed
   */
  public AuthenticationRestServiceIT() 
  throws IOException 
  {
    InputStream is = getClass().getResourceAsStream("/test.properties");
    Properties props = new Properties();
    props.load(is);
    endPoint = props.getProperty("rest.endpoint");
  }
  
  @Before
  public void setUp()
  {
    testSubject = new AuthenticationRestClient(endPoint);
  }

  @Test
  public void testAuthenticateUserREST() 
  {
    // Execute
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName(USER_VMS_ADMIN);
    request.setPassword(PASSWORD);
    AuthenticationResponse result = testSubject.authenticateUser(request);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertTrue(result.isAuthenticated());
    assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());
    
    // Verify user's LDAP attributes not present in response
    assertTrue("Unexpected LDAP attributes is response", result.getUserMap() == null);
  }

  @Test
  public void testGetUserChallengeREST() 
  {
    // Execute
    ChallengeResponse result = testSubject.getUserChallenge(USER_VMS);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected 'challenge' value", CHALLENGE, result.getChallenge());
  }

  @Test
  public void testAuthenticateUserByChallenge() 
  {
    // Execute
    ChallengeResponse request = new ChallengeResponse();
    request.setUserName(USER_VMS);
    request.setChallenge(CHALLENGE);
    request.setResponse(RESPONSE);
    AuthenticationResponse result = testSubject.authenticateUser(request);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertTrue(result.isAuthenticated());
    assertEquals("Unexpected 'statusCode' value", AuthenticationResponse.SUCCESS, result.getStatusCode());
    
    // Verify user's LDAP attributes not present in response
    assertTrue("Unexpected LDAP attributes is response", result.getUserMap() == null);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserSuccess() 
  {
    // Set-up
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName("quota_man_com");
    request.setPassword(PASSWORD);
    
    // Execute
    AuthenticationResponse response = testSubject.authenticateUser(request);
    
    // Verify
    boolean expResult = true;
    assertNotNull("Unexpected null response", response);
    assertEquals(expResult, response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", AuthenticationResponse.SUCCESS, response.getStatusCode());
    
    // Verify user's LDAP attributes not present in response
    assertTrue("Unexpected LDAP attributes is response", response.getUserMap() == null);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserFailureAccountLocked()
  {
    // Set-up
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName("lockout");
    request.setPassword(PASSWORD);
    
    // Execute
    AuthenticationResponse response = testSubject.authenticateUser(request);
    
    // Verify
    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected Authenticated response", response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", AuthenticationResponse.ACCOUNT_LOCKED, response.getStatusCode());
    
    // Verify user's LDAP attributes not present in response
    assertTrue("Unexpected LDAP attributes is response", response.getUserMap() == null);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserFailureInvalidCredentials()
  {
    // Set-up
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName("quota_usr_com");
    request.setPassword("wrong password");
    
    // Execute
    AuthenticationResponse response = testSubject.authenticateUser(request);
    
    // Verify
    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected Authenticated response", response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", AuthenticationResponse.INVALID_CREDENTIALS, 
    		response.getStatusCode());
    
    // Verify user's LDAP attributes not present in response
    assertTrue("Unexpected LDAP attributes is response", response.getUserMap() == null);
    
    // tear down to avoid user lock out 
    request.setPassword(PASSWORD);
    response = testSubject.authenticateUser(request);
    
    assertNotNull("Unexpected null response", response);
    assertTrue("Unexpected Authenticated response", response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", AuthenticationResponse.SUCCESS, response.getStatusCode());
  }
  
}