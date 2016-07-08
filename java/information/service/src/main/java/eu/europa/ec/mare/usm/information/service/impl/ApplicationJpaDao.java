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
package eu.europa.ec.mare.usm.information.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.FeatureEntity;
import eu.europa.ec.mare.usm.information.entity.OptionEntity;

/**
 * JPA based data-access for the administration of Application related
 * information
 */
public class ApplicationJpaDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationJpaDao.class);

  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  /**
   * Creates a new instance
   */
  public ApplicationJpaDao() {
  }

  /**
   * Reads the Application with the provided identifier
   *
   * @param applicationId the id of the application
   *
   * @return the matching Application if it exists, null otherwise
   */
  ApplicationEntity read(Long applicationId) 
  {
    LOGGER.info("read(" + applicationId + ") - (ENTER)");
    
    ApplicationEntity ret = null;

    try {
      Query q = em.createNamedQuery("ApplicationEntity.findByApplicationId");

      q.setParameter("applicationId", applicationId);
      ret = (ApplicationEntity) q.getSingleResult();
    } catch (NoResultException ex) {
      LOGGER.info("Application with id " + applicationId + " not found", ex);
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("read() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Reads the Application with the provided name
   *
   * @param applicationName the name of the application
   *
   * @return the matching Application if it exists, null otherwise
   */
  public ApplicationEntity read(String applicationName) 
  {
    LOGGER.info("read(" + applicationName + ") - (ENTER)");
    
    ApplicationEntity ret = null;

    try {
      Query q = em.createNamedQuery("ApplicationEntity.findByName");

      q.setParameter("name", applicationName);
      ret = (ApplicationEntity) q.getSingleResult();
    } catch (NoResultException ex) {
      LOGGER.info("Application with name " + applicationName + " not found");
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("read() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Reads the Application with the provided name, together with all
   * its features, datasets and options
   *
   * @param applicationName the name of the application
   *
   * @return the matching Application if it exists, null otherwise
   */
  ApplicationEntity readApplication(String applicationName) 
  {
    LOGGER.info("readApplication(" + applicationName + ") - (ENTER)");
    
    ApplicationEntity ret = read(applicationName);

    if (ret != null) {
      // Read details
      try {
        ret.setDatasetList(readDetails(ret.getApplicationId(), 
                                       "DatasetEntity.findByApplicationId", 
                                        DatasetEntity.class));
        ret.setFeatureList(readDetails(ret.getApplicationId(), 
                                       "FeatureEntity.findByApplicationId", 
                                       FeatureEntity.class));
        ret.setOptionList(readDetails(ret.getApplicationId(), 
                                      "OptionEntity.findByApplicationId", 
                                      OptionEntity.class));
      } catch (Exception ex) {
        handleException("readApplication", ex);
      }
    }
    
    LOGGER.info("readApplication() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Creates (or persists) the provided application.
   * 
   * @param src the application to be created
   * 
   * @return the unique applicationId (PK) assigned to the application
   */
  Long create(ApplicationEntity src) 
  {
    LOGGER.info("create(" + src + ") - (ENTER)");
    
    if (src.getFeatureList() != null) {
      for (FeatureEntity fe : src.getFeatureList()) {
        fe.setApplication(src);
      }
    }
    if (src.getDatasetList() != null) {
      for (DatasetEntity de : src.getDatasetList()) {
        de.setApplication(src);
      }
    }
    if (src.getOptionList() != null) {
      for (OptionEntity oe : src.getOptionList()) {
        oe.setApplication(src);
      }
    }
    em.persist(src);
    em.flush();

    Long ret = src.getApplicationId();
    
    LOGGER.info("create() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Updates the provided Application and all its details.
   * 
   * @param src the Application to be updated
   */
  void update(ApplicationEntity src) 
  {
    LOGGER.info("update(" + src + ") - (ENTER)");
    
    if (src.getFeatureList() != null) {
      for (FeatureEntity fe : src.getFeatureList()) {
        fe.setApplication(src);
      }
    }
    if (src.getDatasetList() != null) {
      for (DatasetEntity de : src.getDatasetList()) {
        de.setApplication(src);
      }
    }
    if (src.getOptionList() != null) {
      for (OptionEntity oe : src.getOptionList()) {
        oe.setApplication(src);
      }
    }
    em.merge(src);
    em.flush();

    LOGGER.info("update() - (LEAVE)");
  }

  /**
   * Deletes the Application with the provided name.
   * 
   * @param applicationName the Application name
   */
  void delete(String applicationName) 
  {
    LOGGER.info("delete(" + applicationName + ") - (ENTER)");

    ApplicationEntity entity = readApplication(applicationName);
    if (entity != null) {
      try {
        em.remove(entity);
        em.flush();
      } catch (Exception ex) {
        handleException("delete", ex);
      }
    }

    LOGGER.info("delete() - (LEAVE)");
  }
  
  /**
   * Delete specific details (features, datasets and/or options) of an
   * Application
   * 
   * @param src the application details to be deleted
   */
  void deleteDetails(ApplicationEntity src) 
  {
    LOGGER.info("deleteDetails(" + src + ") - (ENTER)");
    int ret = 0;
    
    if (src != null) {
      try {
        // Delete features
        if (src.getFeatureList() != null && !src.getFeatureList().isEmpty()) {
          LOGGER.debug("Features to be deleted:" + src.getFeatureList().size());

          Query feat = em.createQuery("delete from FeatureEntity e " + 
                                       " where e.featureId=:featureId");
          for (FeatureEntity item : src.getFeatureList()) {
            feat.setParameter("featureId", item.getFeatureId());
            int cnt = feat.executeUpdate();        
            LOGGER.debug("deleted " + cnt + " row(s) for featureId " + 
                        item.getFeatureId());
            ret += cnt;
          }
        }
        
        // Delete datasets
        if (src.getDatasetList() != null && !src.getDatasetList().isEmpty()) {
          LOGGER.debug("Datasets to be deleted:" + src.getDatasetList().size());

          Query dset = em.createQuery("delete from DatasetEntity e " + 
                                       " where e.datasetId=:datasetId");
          for (DatasetEntity item : src.getDatasetList()) {
            dset.setParameter("datasetId", item.getDatasetId());
            int cnt = dset.executeUpdate();        
            LOGGER.debug("deleted " + cnt + " row(s) for datasetId " + 
                         item.getDatasetId());
            ret += cnt;
          }
        }
        
        // Delete options, cascading any associated preferences 
        if (src.getOptionList() != null && !src.getOptionList().isEmpty()) {
          LOGGER.debug("Options to be deleted:" + src.getOptionList().size());

          Query opt = em.createQuery("delete from OptionEntity e " + 
                                       " where e.optionId=:optionId");

          Query pref = em.createQuery("delete from PreferenceEntity e " + 
                                       " where e.option.optionId=:optionId");

          for (OptionEntity item : src.getOptionList()) {
            pref.setParameter("optionId", item.getOptionId());
            int cnt = pref.executeUpdate();        
            opt.setParameter("optionId", item.getOptionId());
            cnt += opt.executeUpdate();        
            LOGGER.debug("deleted " + cnt + " row(s) for optionId " + 
                         item.getOptionId());
            ret += cnt;
          }
        }
        em.flush();
      } catch (Exception ex) {
        handleException("deleteDetails", ex);
      }
    }

    LOGGER.info("deleteDetails() - (LEAVE): " + ret);
  }

  private <T> List<T> readDetails(Long applicationId, String queryName, 
                                   Class<T> type)
  {
    LOGGER.debug("readDetails(" + queryName + ") - (ENTER)");
    
    Query q = em.createNamedQuery(queryName, type);

    q.setParameter("applicationId", applicationId);
    List ret = q.getResultList();

    LOGGER.debug("readDetails() - (LEAVE): " + ret.size());
    return ret;
  }
  
  private void handleException(String operation, Exception ex) 
  throws RuntimeException 
  {
    String msg = "Error during " + operation + " operation: " + ex.getMessage();

    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

}