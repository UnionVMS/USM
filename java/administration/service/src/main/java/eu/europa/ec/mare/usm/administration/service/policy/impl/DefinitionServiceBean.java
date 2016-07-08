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
package eu.europa.ec.mare.usm.administration.service.policy.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.information.entity.PolicyEntity;

/**
 * J2EE Singleton implementation of the Policy DefinitionService.<br/>
 * Policy definitions are cached for 5 minutes.
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DefinitionServiceBean implements DefinitionService{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionServiceBean.class);
  private static final long TTL = (5 * 60 * 1000);

  @Inject
  private PolicyValidator validator;
  
  @Inject
  private PolicyJpaDao  jpaDao;

  private final Map<String,TimedKeeper> cache;

  /**
   * Creates a new instance.
   */
  public DefinitionServiceBean() 
  {
    cache = new HashMap<>();
  }
  
  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public PolicyDefinition getDefinition(String subject) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("getDefinition(" + subject + ") - (ENTER)");

    validator.assertNotEmpty("subject", subject);
    
    PolicyDefinition ret = null;
    TimedKeeper tk = cache.get(subject);
    if (tk != null) {
      if (tk.timestamp + TTL > System.currentTimeMillis()) {
        ret = tk.definition;
      } else {
        // Evict expired cached version
        evictDefinition(subject);
      }
    }
    
    if (ret == null) {
      ret = readDefinition(subject);
      if (ret != null) {
        cacheDefinition(subject, ret);
      }
    }
    
    LOGGER.info("getDefinition() - (LEAVE)");
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)  
  public String getPolicyProperty(String subject, String property)
  {
    LOGGER.info("getPolicyProperty(" + subject +", " + property + ") - (ENTER)");
    
    validator.assertNotEmpty("property", property);    
    
    String propertyValue = null;
    
    PolicyDefinition def = getDefinition(subject);
    if (def != null) {
      Properties props = def.getProperties();
      
      propertyValue = props.getProperty(property);
    }
    
    LOGGER.info("getPolicyProperty() - (LEAVE): " + propertyValue);
    return propertyValue;
  }  
  
  @Override
  public void setDefinition(ServiceRequest<PolicyDefinition> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("setDefinition(" + request + ") - (ENTER)");
    
    validator.assertValid(request);
    String subject = request.getBody().getSubject();

    // Evict any cached version
    evictDefinition(subject);
    
    // Read existing properties
    List<PolicyEntity> existing = jpaDao.readPolicy(subject);
    List<PolicyEntity> updated = new ArrayList<>();
    if (existing != null) {
      Properties properties = request.getBody().getProperties();
      // Update only the provided properties
      for (PolicyEntity policy: existing) {
        if (properties.getProperty(policy.getName()) != null) {
          policy.setValue(properties.getProperty(policy.getName()));
          policy.setModifiedBy(request.getRequester());
          policy.setModifiedOn(new Date());
          updated.add(policy);
        }
      }
    }
    // Write back any updated properties
    if (!updated.isEmpty()) {
      jpaDao.updatePolicy(updated);
    }
    
    LOGGER.info("setDefinition() - (LEAVE)");
  }

  private void cacheDefinition(String subject, PolicyDefinition ret) 
  {
    synchronized  (cache) {
      cache.put(subject, new TimedKeeper(ret));
    }
  }

  public void evictDefinition(String subject) 
  {
    synchronized  (cache) {
      cache.remove(subject);
    }
  }
  
  private PolicyDefinition readDefinition(String subject) 
  {
    LOGGER.debug("readDefinition() - (ENTER)");
    PolicyDefinition ret = null;
    
    List<PolicyEntity> lst = jpaDao.readPolicy(subject);
    if (lst != null && !lst.isEmpty()) {
      ret = new PolicyDefinition();
      ret.setSubject(subject);
      ret.setProperties(new Properties());
      for (PolicyEntity e: lst) {
        ret.getProperties().put(e.getName(), e.getValue());
      }
    }
    
    LOGGER.debug("readDefinition() - (LEAVE)");
    return ret;
  }

  private class TimedKeeper {
    long timestamp;
    PolicyDefinition definition;
    
    private TimedKeeper(PolicyDefinition def) 
    {
      definition = def;
      timestamp = System.currentTimeMillis();
    }
  }

}