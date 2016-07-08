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

import javax.ejb.EJB;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;



/**
 * Filters incoming requests, converting Authorisation headers to a 
 * remote user identity (if the request does not already reference 
 * a remote user), extending the duration of the JWT token (if present).
 *
 */
@WebFilter(filterName = "AuthenticationFilter",
           urlPatterns = {"/rest/*"})
public class AuthenticationFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
  private static final String AUTHORIZATION = "Authorization";
  private static final String CHALLENGEAUTH = "/challengeauth";
  private static final String AUTHENTICATE = "/authenticate";
  private static final String RESETPWD = "/users/resetUserPassword";
  private static final String PING = "/ping";

  @EJB(name="secureJwtHandler")
  JwtTokenHandler tokenHandler;

  @Inject 
  BasicAuthenticationHandler basicHandler;
  
  @EJB
  private AuthenticationService service;
  
  /**
   * Creates a new instance
   */
  public AuthenticationFilter() {
  }

  /**
   * Filters an incoming request, converting a (custom) JWT token to 
   * a (standard) remote user identity (if the request does not 
   * already reference a remote user), extending the duration of 
   * the JWT token (if present). If the request contains neither a remote
   * user identity nor a JWT token, request processing is skipped and an HTTP
   * status of 403 (Forbidden) is sent back to the requester,
   *
   * @param request The request we are processing
   * @param response The response we are creating
   * @param chain The filter chain we are processing
   *
   * @exception IOException if an input/output error occurs
   * @exception ServletException if another error occurs
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain)
  throws IOException, ServletException 
  {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    LOGGER.info("doFilter(" + httpRequest.getMethod() + ", " + 
                              httpRequest.getPathInfo() + ") - (ENTER)");
    
    // if it's a preflighted requests just forward the request to the next filter
    if ( httpRequest.getMethod().equals( "OPTIONS" ) ) {
    	LOGGER.info("HTTP Method (OPTIONS) - Detected!");
    	chain.doFilter(httpRequest, response);
    } else {
    	Boolean tokenIsUsed = false;
        String remoteUser = httpRequest.getRemoteUser();
        LOGGER.debug("httpRequest.getRemoteUser(): " + remoteUser);
        String authorizationHeader = httpRequest.getHeader(AUTHORIZATION);
        if (remoteUser == null) {
          // Check for HTTP basic authentication
          remoteUser = basicHandler.handleAuthorizationHeader(authorizationHeader);
          if (remoteUser == null) {
            // Check for JWT authentication
            remoteUser = tokenHandler.parseToken(authorizationHeader);
            if (remoteUser != null) {
              tokenIsUsed = true;
            }
          }
        }
        LOGGER.info("remoteUser: " + remoteUser);
        if (remoteUser != null) {
		  //System.out.format("remoteUser => %s\n", remoteUser);
          AuthenticatedRequest arequest = new AuthenticatedRequest(httpRequest,
                  remoteUser);
          String extendedToken;
          if (tokenIsUsed) {
            extendedToken = tokenHandler.extendToken(authorizationHeader);
          } else {
            // we have a remote user but no token was provided
            extendedToken = tokenHandler.createToken(remoteUser);
          }
		  
		  if(service.isPasswordExpired(remoteUser)) {
		    httpResponse.addHeader("extStatus", "701");
		  } else {		  
			  if(service.isPasswordAboutToExpire(remoteUser)) {
				httpResponse.addHeader("extStatus", "773");
			  } else {
				httpResponse.addHeader("extStatus", "0");
			  }
		  }
		  
          httpResponse.addHeader(AUTHORIZATION, extendedToken);

          if (PING.equals(httpRequest.getPathInfo())) {
            if (httpRequest.getUserPrincipal() != null && 
                httpRequest.getUserPrincipal().getClass().toString().contains("cas")) {
              String callback = httpRequest.getParameter("jwtcallback");
              if (callback != null) {
                LOGGER.info("Redirecting to add jwt");
                String redir = callback + "?jwt=" + extendedToken;
                httpResponse.sendRedirect(redir);
              }
            }
          }
          chain.doFilter(arequest, httpResponse);
        } else {
          String pathInfo = httpRequest.getPathInfo();
		  //System.out.format("checking if special treatment : %s - %s\n", pathInfo, RESETPWD);	
          if (AUTHENTICATE.equals(pathInfo) || CHALLENGEAUTH.equals(pathInfo) || RESETPWD.equals(pathInfo)) {
			//System.out.format("ok can proceed without authentication\n");			  
            // if there is an authentication request proceed
            chain.doFilter(httpRequest, response);
          } else {
            //  Send 403 error
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
          }
        }
    }
    LOGGER.info("doFilter() - (LEAVE)");
  }

  @Override
  public void init(FilterConfig fc)
  throws ServletException 
  {
    if (basicHandler == null) {
      throw new ServletException("BasicAuthenticationHandler is undefined");
    }
    if (tokenHandler == null) {
      throw new ServletException("JwtTokenHandler is undefined");
    }
  }

  @Override
  public void destroy() {
    // NOP
  }

}