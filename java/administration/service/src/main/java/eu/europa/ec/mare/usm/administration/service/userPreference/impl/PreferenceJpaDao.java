package eu.europa.ec.mare.usm.administration.service.userPreference.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;

public class PreferenceJpaDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceJpaDao.class);

  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  /**
   * Creates a new instance.
   */
  public PreferenceJpaDao() {
  }

  /**
   * Retrieves the preferences for the context with the provided identifier
   *
   * @param contextId the user context identifier
   * 
   * @return the possibly-empty list of preferences
   */
  public List<PreferenceEntity> read(Long contextId) 
  {
    LOGGER.info("read(" + contextId + ") - (ENTER)");

    TypedQuery<PreferenceEntity> q = em.createNamedQuery("PreferenceEntity.findByContextId",
                                                         PreferenceEntity.class);

    q.setParameter("contextId", contextId);
    List<PreferenceEntity> ret = q.getResultList();

    LOGGER.info("read() - (LEAVE)");
    return ret;
  }
}
