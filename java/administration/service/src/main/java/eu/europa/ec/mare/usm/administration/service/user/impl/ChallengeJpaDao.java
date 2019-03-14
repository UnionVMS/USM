package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.ChallengeEntity;

/**
 * JPA based data access of user data.
 */
public class ChallengeJpaDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeJpaDao.class);

  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  /**
   * Creates a new instance.
   */
  public ChallengeJpaDao() {
  }

  /**
   * Creates a new challenge entity.
   *
   * @param challengeEntity the challenge entity to be created
   * 
   * @return challengeEntity the new challenge entity
   */
  public ChallengeEntity create(ChallengeEntity challengeEntity) 
  {
    LOGGER.info("create(" + challengeEntity + ") - (ENTER)");
    
    try {
      em.persist(challengeEntity);
    } catch (Exception ex) {
      String msg = "Failed to create challenge: " + ex.getMessage();
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }
    
    LOGGER.info("create() - (LEAVE)");
    return challengeEntity;
  }

  /**
   * Updates an existing challenge entity.
   *
   * @param challengeEntity the challenge entity to be updated
   * 
   * @return the updated challenge entity
   */
  public ChallengeEntity update(ChallengeEntity challengeEntity) 
  {
    LOGGER.info("update(" + challengeEntity + ") - (ENTER)");
    
    ChallengeEntity ret = null;
    try {
      ret = em.merge(challengeEntity);
    } catch (Exception ex) {
      String msg = "Failed to update challenge: " + ex.getMessage();
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }

    LOGGER.info("update() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves a list of challenge entities providing userName
   *
   * @param userName the name of the user the challenges of whom to be found
   * 
   * @return null if nothing was found otherwise a list of challenge entities
   */
  public List<ChallengeEntity> getChallenges(String userName) 
  {
    LOGGER.info("getChallenges(" + userName + ") - (ENTER)");

    List<ChallengeEntity> ret = null;

    try {
      TypedQuery<ChallengeEntity> q = em.createNamedQuery("ChallengeEntity.findByUserName", ChallengeEntity.class);

      q.setParameter("userName", userName);
      ret = q.getResultList();
    } catch (NoResultException exc) {
      LOGGER.debug("No challenge entity was found with userName " + userName);
    }

    LOGGER.info("getChallenges() - (LEAVE)");
    return ret;
  }
  
}
