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
package eu.europa.ec.mare.usm.administration.service.ldap.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides operations for the retrieval of user information from an
 * LDAP compatible Identity Management system.
 */
public class LDAP {
  private static final Logger LOGGER = LoggerFactory.getLogger(LDAP.class);
  public static final String STATUS_CODE = "statusCode";

  private String serverURL;
  private String contextRoot;
  private String bindDN;
  private String bindPassword;
  private String queryFilter;
  private String ldapFirstName;
  private String ldapLastName;
  private String ldapTelephoneNumber;
  private String ldapMobileNumber;
  private String ldapFaxNumber;
  private String ldapMail;

  /**
   * Creates a new instance and configures it using the provided 
   * properties.
   *
   * @param properties the LDAP configuration properties
   */
  public LDAP(Properties properties) 
  {
    init(properties);
  }

  /**
   * Retrieves user details from an LDAP data-source for the user with
   * the provided user-name.
   * 
   * @param userName the user-name
   * 
   * @return user details as per the configuration properties
   */
  public Map<String, String> getUserDetails(String userName) 
  {
    Properties ldapEnv = new Properties();

    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    ldapEnv.put(Context.PROVIDER_URL, serverURL);
    ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
    ldapEnv.put(Context.SECURITY_PRINCIPAL, bindDN);
    ldapEnv.put(Context.SECURITY_CREDENTIALS, bindPassword);

    DirContext dirContext = null;
    NamingEnumeration<SearchResult> answer = null;
    Map<String, String> ret = new HashMap<>();
    try {
      dirContext = new InitialDirContext(ldapEnv);

      /* Define what properties are necessary comming from LDAP Entity */
      String returnedAtts[] = {ldapFirstName, ldapLastName, ldapTelephoneNumber, 
                               ldapMobileNumber, ldapFaxNumber, ldapMail};
      SearchControls searchCtls = new SearchControls();
      searchCtls.setReturningAttributes(returnedAtts);
      searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      /* Define Filter */
      String searchFor = MessageFormat.format(queryFilter, userName);

      LOGGER.debug("LDAP get User info  for " + searchFor + " under root " + contextRoot);
      answer = dirContext.search(contextRoot, searchFor, searchCtls);
      LOGGER.debug("LDAP get User info for " + searchFor + " got answer");

      while (answer.hasMoreElements()) {
        SearchResult sr = (SearchResult) answer.next();
        String dn = sr.getNameInNamespace();
        if (dn == null || answer.hasMoreElements()) {
          answer.close();
          dirContext.close();
          return null;
        }
        Attributes attrs = sr.getAttributes();
        if (attrs != null) {
          ret.put("firstName",getValue(attrs, ldapFirstName));
          ret.put("lastName", getValue(attrs, ldapLastName));
          ret.put("telephoneNumber", getValue(attrs, ldapTelephoneNumber));
          ret.put("mobileNumber", getValue(attrs, ldapMobileNumber));
          ret.put("faxNumber", getValue(attrs, ldapFaxNumber));
          ret.put("email", getValue(attrs, ldapMail));
        }
        answer.close();
        LOGGER.debug("LDAP get User info for " + searchFor + " answer closed");
        dirContext.close();
        LOGGER.debug("LDAP get User info for " + searchFor + " initial context closed");
      }
      LOGGER.debug("LDAP operations ended.");
    } catch (NamingException e) {
      LOGGER.error("LDAP operations failed: " + e.getMessage(), e);
    } finally {
      cleanUp(dirContext, answer);
    }
    return ret;
  }

  private String getValue(Attributes attrs, String attrId) 
  throws NamingException
  {
    String ret = "";
    
    if (attrs.get(attrId) != null) {
      ret = String.valueOf(attrs.get(attrId).get());
    }
    
    return ret;
  }
  
  private void cleanUp(DirContext ldapContext,
                        NamingEnumeration<SearchResult> answer) 
  {
    if (answer != null) {
      try {
        answer.close();
      } catch (NamingException e) {
        LOGGER.warn("Fail to close answer: " + e.getMessage(), e);
      }
    }
    if (ldapContext != null) {
      try {
        ldapContext.close();
      } catch (NamingException e) {
        LOGGER.warn("Failed to close context: " + e.getMessage(), e);
      }
    }
  }

  private void init(Properties props) 
  {
    serverURL = props.getProperty("ldap.server.url");
    contextRoot = props.getProperty("ldap.context.root");
    bindDN = props.getProperty("ldap.bind.dn");
    bindPassword = props.getProperty("ldap.bind.password");
    queryFilter = props.getProperty("ldap.query.filter");
    ldapFirstName = props.getProperty("ldap.label.firstName");
    ldapLastName = props.getProperty("ldap.label.lastName");
    ldapTelephoneNumber = props.getProperty("ldap.label.telephoneNumber");
    ldapMobileNumber = props.getProperty("ldap.label.mobileNumber");
    ldapFaxNumber = props.getProperty("ldap.label.faxNumber");
    ldapMail = props.getProperty("ldap.label.mail");
  }
}