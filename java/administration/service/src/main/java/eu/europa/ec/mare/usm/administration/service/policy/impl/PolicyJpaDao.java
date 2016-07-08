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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.information.entity.PolicyEntity;

/**
 * JPA based data-access-object for the retrieval and storage of Policy 
 * definition/configuration properties.
 */
public class PolicyJpaDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(PolicyJpaDao.class);
  
  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  
  /**
   * Reads the Policy definition for the provided subject
   * 
   * @param subject the policy subject
   * 
   * @return the possibly-empty list of Policy entities
   */
  List<PolicyEntity> readPolicy(String subject) 
  {
    LOGGER.info("readPolicy(" + subject + ") - (ENTER)");
    
    List<PolicyEntity> ret = null;
    try{
      TypedQuery<PolicyEntity> q = em.createNamedQuery("PolicyEntity.findBySubject", 
                                                       PolicyEntity.class);
      q.setParameter("subject", subject);

      ret = q.getResultList();
    } catch (Exception ex) {
      String msg = "Failed to read policy: " + ex.getMessage();
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }
    
    LOGGER.info("readPolicy() - (LEAVE): " + ret.size());
    return ret;
  }

  /**
   * Updates the provided Policy definition
   * 
   * @param entityList the policy definition
   */
  void updatePolicy(List<PolicyEntity> entityList) 
  {
    LOGGER.info("updatePolicy(" + entityList + ") - (ENTER)");

    try{
      if (entityList != null && !entityList.isEmpty()) {
        for (PolicyEntity entity: entityList) {
          LOGGER.info("updating: " + entity);
          em.merge(entity);
          em.flush();
          LOGGER.info("updated: " + entity.getName());
        }
        em.flush();
      }
    } catch (Exception ex) {
      String msg = "Failed to update policy: " + ex.getMessage();
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }
    
    LOGGER.info("updatePolicy() - (LEAVE)");
  }
  
  public PolicyEntity updatePolicyProperty(PolicyEntity e) {
	  LOGGER.info("updatePolicyProperty(" + e + ") - (ENTER)");
	  
	  LOGGER.info("updating: " + e);
	  em.merge(e);
      em.flush();
      LOGGER.info("updated: " + e.getName());
	  LOGGER.info("updatePolicyProperty() - (LEAVE)");
	  return e;
  }
  
  public List<PolicyEntity> findPolicies(FindPoliciesQuery request) 
  {
    LOGGER.info("findPolicies(" + request + ") - (ENTER)");

    String subject = request.getSubject();
    String name = request.getName();

    List<PolicyEntity> ret = null;
    try {
      StringBuilder jpql = new StringBuilder("SELECT p FROM PolicyEntity p WHERE 1=1 ");
      if (subject != null) {
        jpql.append(" AND p.subject = :subject ");
      }
      if (name != null) {
        jpql.append(" AND lower(p.name) like :name ");
      }
      TypedQuery<PolicyEntity> query = em.createQuery(jpql.toString(), PolicyEntity.class);
      if (subject != null) {
        query.setParameter("subject", subject);
      }
      if (name != null) {
        query.setParameter("name",  "%" + name.toLowerCase() + "%");
      }
      ret = query.getResultList();
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("findPolicies() - (LEAVE)");
    return ret;
  }  
  
  private void handleException(String operation, Exception ex)
  throws RuntimeException 
  {
	  String msg = "Failed to " + operation + " feature: " + ex.getMessage();
	  

    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }
  
}