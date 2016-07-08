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
package eu.europa.ec.mare.usm.administration.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.rest.service.authentication.AuthenticationRestClient;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;

public class AuthWrapper {

  private static final String PASSWORD = "password";
  protected final String endPoint;
  protected final Properties props;
  private String authToken = "";
  
  protected String getAuthToken() {
    return authToken;
  }
  protected void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  private AuthenticationRestClient auth = null;
  
  public AuthWrapper() throws IOException{
    InputStream is = getClass().getResourceAsStream("/test.properties");
    props = new Properties();
    props.load(is);
    endPoint = props.getProperty("rest.endpoint");
    auth = new AuthenticationRestClient(endPoint);
    
  }
  public AuthWrapper(String testUser) throws IOException{
    InputStream is = getClass().getResourceAsStream("/test.properties");
    props = new Properties();
    props.load(is);
    endPoint = props.getProperty("rest.endpoint");
    auth = new AuthenticationRestClient(endPoint);
    authenticate(testUser);
  }
  
  protected String authenticate(String testUser){

    // Execute
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName(testUser);
    request.setPassword(PASSWORD);
    AuthenticationJwtResponse result = auth.authenticateUser(request);

    // Verify
    assertNotNull("Unexpected null result in auth", result);
    assertTrue(result.isAuthenticated());
    assertEquals("Unexpected 'statusCode' value", 
                 AuthenticationResponse.SUCCESS, 
                 result.getStatusCode());
    authToken  = result.getJWToken();
    return authToken;
  }
  
  
}