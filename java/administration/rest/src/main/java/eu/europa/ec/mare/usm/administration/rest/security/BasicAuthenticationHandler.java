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
package eu.europa.ec.mare.usm.administration.rest.security;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import javax.ejb.EJB;
import javax.xml.bind.DatatypeConverter;

/**
 *
 */
public class BasicAuthenticationHandler {
  private static final String PREFIX = "Basic ";

  @EJB
  AuthenticationService service;
  
  String handleAuthorizationHeader(String header)
  {
    String ret = null;
    
    if (header != null && header.startsWith(PREFIX)) {
      String encoded = header.substring(PREFIX.length());
      byte[] bytes = DatatypeConverter.parseBase64Binary(encoded);
      String decoded = new String(bytes);
      String[] parts = decoded.split(":");
      
      if (parts != null && parts.length == 2) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName(parts[0]);
        request.setPassword(parts[1]);
        
        AuthenticationResponse r = service.authenticateUser(request);
        if (r != null && r.isAuthenticated()) {
          ret = request.getUserName();
        } 
      }
    }
    
    return ret;
  }
}