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

import javax.ejb.EJB;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import static eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse.SUCCESS;
import static eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse.ACCOUNT_LOCKED;
import static eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse.INVALID_CREDENTIALS;

/**
 * Abstract unit-test for the AuthenticationService 
 */
@RunWith(Arquillian.class)
public abstract class AbstractAuthenticationServiceTest {
  
	protected static final String POLICY_SUBJECT = "Authentication";

    //Tear-down: do a successful login to avoid account lockout
    protected static final String TEAR_DOWN_PASSWORD = "password";
  
  @EJB
  private AuthenticationService testSubject;
  
  protected AuthenticationResponse resp;
  protected ChallengeResponse challengeResp;
  protected boolean authenticateCalled = false;
  protected String invalidCredentialsUsername = null;
  
  @Before
  public void clearResp()
  {
	  authenticateCalled = false;
	  invalidCredentialsUsername = null;
	  resp =null;
	  challengeResp = null;
  }
  
  @After
  public void tearDown() {
	  if (invalidCredentialsUsername != null) {
		  AuthenticationRequest request = new AuthenticationRequest();
		  request.setUserName(invalidCredentialsUsername);
		  request.setPassword(TEAR_DOWN_PASSWORD);
		  AuthenticationResponse tearDown = testSubject.authenticateUser(request);
		  assertNotNull("Unexpected null response", tearDown);
		  System.out.println("AuthenticationResponse: " + tearDown);
		  assertTrue("Unexpected Unauthenticated response", tearDown.isAuthenticated());
	  }
  }
  
  /**
   * Creates a new instance
   */
  public AbstractAuthenticationServiceTest() {
  }
  
  /*
   * Set up and execute authenticateUser for the given user name and password, in case of invalid credentials
   * tear down is prepared to avoid account lockout 
   */
  protected void authenticateUser(String userName, String password) {
	  authenticateCalled = true;
	  
	  // Set-up
      AuthenticationRequest request = new AuthenticationRequest();
	  request.setUserName(userName);
	  request.setPassword(password);
	
	  // Execute
	  resp = testSubject.authenticateUser(request);
	  
	  if (resp.getStatusCode() == INVALID_CREDENTIALS) {
		  invalidCredentialsUsername = userName;
	  }
  }
  
  protected void authenticateUser(ChallengeResponse challengeTaken) {
	  authenticateCalled = true;
	  
	  // Execute
	  resp = testSubject.authenticateUser(challengeTaken);
  }
  
  protected boolean isLDAPEnabled () {
	 return testSubject.isLDAPEnabled();
  }
  
  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserSuccess() {
    authenticateUser("quota_man_com", "password");
    verify(true, SUCCESS);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserFailureAccountLocked() {
    authenticateUser("lockout", "password");
    verify(false, ACCOUNT_LOCKED);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserFailureInvalidCredentials() {
    authenticateUser("quota_usr_com", "wrong password");
    verify(false, INVALID_CREDENTIALS);
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  public void testGetUserChallengeSuccess() 
  {
    // Set-up
    AuthenticationQuery query = new AuthenticationQuery();
    query.setUserName("quota_usr_com");
    
    // Execute
    challengeResp = testSubject.getUserChallenge(query);
    
    // Verify
    assertNotNull("Unexpected null response", challengeResp);
    assertEquals("Name of street where you grew up", challengeResp.getChallenge());
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  public void testGetUserChallengeFailure() 
  {
    // Set-up
    AuthenticationQuery query = new AuthenticationQuery();
    query.setUserName("wrong user name");
    
    challengeResp = testSubject.getUserChallenge(query);
    
    // Verify
    assertNull("Unexpected not-null response", challengeResp);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserChallengeResponseSuccess() 
  {
    // Set-up
    AuthenticationQuery query = new AuthenticationQuery();
    query.setUserName("quota_usr_com");
    ChallengeResponse request = testSubject.getUserChallenge(query);
    request.setResponse("Grand rue");
    
    authenticateUser(request);
    verify(true, SUCCESS);
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  public void testAuthenticateUserChallengeResponseFailure() 
  {
    AuthenticationQuery query = new AuthenticationQuery();
    query.setUserName("quota_man_com");
    ChallengeResponse request = testSubject.getUserChallenge(query);
    request.setResponse("wrong answer");
    
    authenticateUser(request);
    
    verify(false, INVALID_CREDENTIALS);
  }
  
  protected AuthenticationService getTestSubject() {
	  return testSubject;
  }
  
  private void verify(boolean authenticated, int statusCode) {
	    assertNotNull("Unexpected null response", resp);
	    assertEquals("Unexpected Authenticated response", resp.isAuthenticated(), authenticated);
	    assertEquals("Unexpected response StatusCode", statusCode, resp.getStatusCode());
  }
}