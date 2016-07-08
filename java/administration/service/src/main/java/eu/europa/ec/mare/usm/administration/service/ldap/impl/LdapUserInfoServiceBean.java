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

import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.LdapUser;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.ldap.LdapUserInfoService;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import javax.inject.Inject;

/**
 * Stateless session bean implementation of the LdapUserInfoService.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LdapUserInfoServiceBean implements LdapUserInfoService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserInfoServiceBean.class);
  private static final String POLICY_SUBJECT = "Administration";

  @EJB
  private DefinitionService policy;
  
  @Inject
  private LDAPValidator validator;
  
  /**
   * Creates a new instance
   */
  public LdapUserInfoServiceBean()
  {
  }
  
  @Override
  public boolean isEnabled() 
  {
    LOGGER.info("isEnabled() - (ENTER)");
  
    Properties props = getProperties();
    
    boolean ret = Boolean.parseBoolean(props.getProperty("ldap.enabled", 
                                                         "false"));

    LOGGER.info("isEnabled() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public LdapUser getLdapUserInfo(ServiceRequest<GetUserQuery> request) 
  {
    LOGGER.info("getLdapUserInfo(" + request + ") - (ENTER)");
  
    validator.assertValid(request);
    
    LdapUser ret = null;
    if (isEnabled()) {
      LDAP ldap = new LDAP(getProperties());
      String userName = request.getBody().getUserName();
      Map<String,String> userMap = ldap.getUserDetails(userName);

      if (userMap !=null && !userMap.isEmpty()){
        ret = new LdapUser();
        ret.setUserName(request.getBody().getUserName());
        ret.setFirstName(userMap.get("firstName"));
        ret.setLastName(userMap.get("lastName"));
        ret.setPhoneNumber(userMap.get("telephoneNumber"));
        ret.setMobileNumber(userMap.get("mobileNumber"));
        ret.setFaxNumber(userMap.get("faxNumber"));
        ret.setEmail(userMap.get("email"));
      }
    }

    LOGGER.info("getLdapUserInfo() - (LEAVE): " + ret);
    return ret;
  }

  private Properties getProperties() 
  {
    Properties ret = null;
    
    PolicyDefinition pf = policy.getDefinition(POLICY_SUBJECT);
    if (pf != null) {
      ret = pf.getProperties();
    } else {
      ret = new Properties();
    }
    
    return ret;
  }
}