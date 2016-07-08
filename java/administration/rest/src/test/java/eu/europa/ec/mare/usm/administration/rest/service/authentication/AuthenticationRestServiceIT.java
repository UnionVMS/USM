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
package eu.europa.ec.mare.usm.administration.rest.service.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;
import eu.europa.ec.mare.usm.administration.rest.service.policy.PolicyRestClient;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.session.service.impl.SessionDao;

/**
 * Integration test for the Authentication REST Service.
 */

public class AuthenticationRestServiceIT extends AuthWrapper {
  private static final String PASSWORD = "password";
  private static final String RESPONSE = "Tintin";
  private static final String CHALLENGE = "Name of first pet";
  private static final String USER_VMS_ADMIN = "vms_admin_com";
  private static final String USM_ADMIN = "usm_admin";
  private static final String USER_VMS = "vms_user_com";
  private AuthenticationRestClient testSubject = null;
  private PolicyRestClient client=null;
	private final String usm_admin;
  
  @EJB
  private SessionDao sessionDao;
  
  @EJB
  private PolicyProvider policyProvider;

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRestServiceIT.class);
  
  /**
   * Creates a new instance.
   * 
   * @throws IOException if class-path resource /test.properties can't be 
   * accessed
   */
  public AuthenticationRestServiceIT() 
  throws IOException 
  {

    super(USM_ADMIN);
    usm_admin = getAuthToken();
  }

  @Before
  public void setUp()
  {
    testSubject = new AuthenticationRestClient(endPoint);
    client = new PolicyRestClient(endPoint);
    
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
    assertEquals("Unexpected 'statusCode' value", 
                 AuthenticationResponse.SUCCESS, 
                 result.getStatusCode());
  }

  @Test
  public void testGetUserChallengeREST() 
  {
    // Execute
    String token = authenticate(USER_VMS);
    ChallengeResponse result = testSubject.getUserChallenge(token);

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
    assertEquals("Unexpected 'statusCode' value", 
                 AuthenticationResponse.SUCCESS, 
                 result.getStatusCode());
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
    request.setPassword("password");
    
    // Execute
    AuthenticationResponse response = testSubject.authenticateUser(request);
    
    // Verify
    boolean expResult = true;
    assertNotNull("Unexpected null response", response);
    assertEquals(expResult, response.isAuthenticated());
    assertEquals("Unexpected response StatusCode",
                 AuthenticationResponse.SUCCESS, response.getStatusCode());
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
    request.setPassword("password");
    
    // Execute
    AuthenticationResponse response = testSubject.authenticateUser(request);
    
    // Verify
    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected Authenticated response", response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", 
                 AuthenticationResponse.ACCOUNT_LOCKED, 
                 response.getStatusCode());
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
    
    System.out.println(response.getStatusCode());
    
    // Verify
    assertNotNull("Unexpected null response", response);
    assertFalse("Unexpected Authenticated response", response.isAuthenticated());
    assertEquals("Unexpected response StatusCode", 
                 AuthenticationResponse.INVALID_CREDENTIALS, 
                 response.getStatusCode());
   
  }
  


  @Test
  public void testUpdatePolicy()
  {
	  int initialValue = getMaxSessionAnySite();
	  setMaxSessionAnySite(20);
	  assertTrue("account.maxSessionOneSite is 20",getMaxSessionAnySite()==20);
	  setMaxSessionAnySite(initialValue);
  }
  

  public int getMaxSessionAnySite()
  {
	    ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
			findReq.setRequester(usm_admin);
			FindPoliciesQuery q = new FindPoliciesQuery();
			q.setName("account.maxSessionAnySite");
			q.setSubject("Account");
			findReq.setBody(q);
		ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
		    GenericType<List<Policy>> gtype = new GenericType<List<Policy>>(){};
			List<Policy> policiesFound = findResp.getEntity(gtype);
			
			return Integer.parseInt(policiesFound.get(0).getValue());
  }
  
  public int getMaxSessionOneSite()
  {
	    ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
			findReq.setRequester(usm_admin);
			FindPoliciesQuery q = new FindPoliciesQuery();
			q.setName("account.maxSessionOneSite");
			q.setSubject("Account");
			findReq.setBody(q);
		ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
		    GenericType<List<Policy>> gtype = new GenericType<List<Policy>>(){};
			List<Policy> policiesFound = findResp.getEntity(gtype);
			
			return Integer.parseInt(policiesFound.get(0).getValue());
  }
  
  
  public void setMaxSessionAnySite(int newValue)
  {
		ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
			findReq.setRequester(usm_admin);
			FindPoliciesQuery q = new FindPoliciesQuery();
			q.setName("account.maxSessionAnySite");
			q.setSubject("Account");
			findReq.setBody(q);
		ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
		    GenericType<List<Policy>> gtype = new GenericType<List<Policy>>(){};
			List<Policy> policiesFound = findResp.getEntity(gtype);
			
		ServiceRequest<Policy> req = new ServiceRequest<>();
			req.setRequester(usm_admin);	
			
			Policy uPolicy = policiesFound.get(0);
			uPolicy.setValue(""+newValue);
			req.setBody(uPolicy);
			
		ClientResponse resp = client.updatePolicy(ClientResponse.class, req);
  }
  
  public void setMaxSessionOneSite(int newValue)
  {
		ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
			findReq.setRequester(usm_admin);
			FindPoliciesQuery q = new FindPoliciesQuery();
			q.setName("account.maxSessionOneSite");
			q.setSubject("Account");
			findReq.setBody(q);
		ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq);
		    GenericType<List<Policy>> gtype = new GenericType<List<Policy>>(){};
			List<Policy> policiesFound = findResp.getEntity(gtype);
			
		ServiceRequest<Policy> req = new ServiceRequest<>();
			req.setRequester(usm_admin);	
			
			Policy uPolicy = policiesFound.get(0);
			uPolicy.setValue(""+newValue);
			req.setBody(uPolicy);
			
		ClientResponse resp = client.updatePolicy(ClientResponse.class, req);
  }
  
/*  @Test
  public void testAuthenticateCallingCheckMaxSessionPolicy()
  {
	  
	    // Set-up
	   AuthenticationRequest request = new AuthenticationRequest();
	    request.setUserName("quota_man_com");
	    request.setPassword("password");
			
		int maxAnySite = getMaxSessionAnySite();
		int maxOneSite = getMaxSessionOneSite();
		
	    setMaxSessionAnySite(1);
	    setMaxSessionOneSite(1);
	    
	    // Execute
										
	    AuthenticationJwtResponse response = testSubject.authenticateUser(request);
	    assertNotNull("the first time the authentication is done, a session should be created",response.getSessionId());
		response = testSubject.authenticateUser(request);
		assertNull("the second time the authentication is done, session creation should not be permitted",response.getSessionId());

	 //clean-up
		 setMaxSessionAnySite(maxAnySite);
		 setMaxSessionOneSite(maxOneSite);
  }*/
  

  
/*  @Test
  public void testAuthenticateCallingStartSession()
  {
	   AuthenticationRequest request = new AuthenticationRequest();
	    request.setUserName("invalid user");
	    request.setPassword("wrong password");
	    
	    AuthenticationJwtResponse response = testSubject.authenticateUser(request);
	    assertTrue(response.getStatusCode() == AuthenticationResponse.INVALID_CREDENTIALS);
  }*/
}