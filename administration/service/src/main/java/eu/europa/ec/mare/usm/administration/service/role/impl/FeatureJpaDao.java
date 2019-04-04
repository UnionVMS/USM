package eu.europa.ec.mare.usm.administration.service.role.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.FindPermissionsQuery;
import eu.europa.ec.mare.usm.information.entity.FeatureEntity;

public class FeatureJpaDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureJpaDao.class);

  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  /**
   * Creates a new instance
   */
  public FeatureJpaDao() {
  }

  /**
   * Reads the Feature with the provided identifier
   *
   * @param featureId the id of the feature
   *
   * @return the matching FeatureEntity if it exists, null otherwise
   */
  public FeatureEntity read(Long featureId) 
  {
    LOGGER.info("read(" + featureId + ") - (ENTER)");
    FeatureEntity ret = null;

    try {
      TypedQuery<FeatureEntity> q = em.createNamedQuery("FeatureEntity.findByFeatureId",
              FeatureEntity.class);

      q.setParameter("featureId", featureId);
      ret = q.getSingleResult();
    } catch (NoResultException ex) {
      LOGGER.info("Feature with id " + featureId + " not found", ex);
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("read() - (LEAVE)");
    return ret;
  }

  public List<FeatureEntity> getFeaturesByApplication(String applicationName) 
  {
    LOGGER.info("getFeaturesByApplication(" + applicationName + ") - (ENTER)");
    
    List<FeatureEntity> ret = null;

    try {
      TypedQuery<FeatureEntity> q = em.createNamedQuery("FeatureEntity.findByApplicationName",
              FeatureEntity.class);

      q.setParameter("appName", applicationName);
      ret = q.getResultList();
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("getFeaturesByApplication() - (LEAVE)");
    return ret;
  }

  public List<FeatureEntity> getAllFeatures() 
  {
    LOGGER.info("getAllFeatures() - (ENTER)");

    List<FeatureEntity> ret = null;
    try {
      TypedQuery<FeatureEntity> q = em.createNamedQuery("FeatureEntity.findAll", FeatureEntity.class);
      ret = q.getResultList();
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("getAllFeatures() - (LEAVE)");
    return ret;
  }

  public List<FeatureEntity> findFeatures(FindPermissionsQuery request) 
  {
    LOGGER.info("findFeatures(" + request + ") - (ENTER)");

    String application = request.getApplication();
    String group = request.getGroup();

    List<FeatureEntity> ret = null;
    try {
      StringBuilder jpql = new StringBuilder("SELECT distinct f FROM FeatureEntity f left join fetch f.roleList WHERE 1=1 ");
      if (application != null) {
        jpql.append(" AND f.application.name = :application ");
      }
      if (group != null) {
        jpql.append(" AND f.groupName = :group ");
      }
      TypedQuery<FeatureEntity> query = em.createQuery(jpql.toString(), FeatureEntity.class);
      if (application != null) {
        query.setParameter("application", application);
      }
      if (group != null) {
        query.setParameter("group", group);
      }
      ret = query.getResultList();
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("findFeatures() - (LEAVE)");
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
