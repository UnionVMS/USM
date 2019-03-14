package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.ChannelEntity;

/**
 * JPA based data access of Channel data.
 */
public class ChannelJpaDao {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ChannelJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    /**
     * Creates a new instance
     */
    public ChannelJpaDao() {
    }

    /**
     * Reads a Channel
     *
     * @param dataflow the dataflow of the channel
     * @param service the service of the channel
     * @param endPointId the end-point (internal) unique identifier
     * @return the matching channel if it exists, null otherwise
     */
    public ChannelEntity findByDataFlowServiceEndPoint(String dataflow, String service, Long endPointId) {
        LOGGER.info(" findByDataFlowServiceEndPoint(" + dataflow + ", " + service + " , " + endPointId + ") - (ENTER)");
        ChannelEntity ret = null;

        try {
            TypedQuery<ChannelEntity> q = em.createNamedQuery(
                    "ChannelEntity.findByDataFlowServiceEndPoint", ChannelEntity.class);

            q.setParameter("dataflow", dataflow);
            q.setParameter("service", service);
            q.setParameter("endPointId", endPointId);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("Endpoint " + dataflow + ", " + service + " , " + endPointId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info(" findByDataFlowServiceEndPoint() - (LEAVE)");
        return ret;
    }

    /**
     * Reads the Channel with the provided (internal) unique identifier
     *
     * @param channelId the channel (internal) unique identifier
     * @return the matching channel if it exists, null otherwise
     */
    public ChannelEntity read(Long channelId) {
        LOGGER.info("read(" + channelId + ") - (ENTER)");

        ChannelEntity ret = null;

        try {
            ret = em.find(ChannelEntity.class, channelId);
        } catch (NoResultException exc) {
            LOGGER.debug("Channel " + channelId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("read() - (LEAVE)");
        return ret;
    }

    /**
     * Reads the channels associated with the provided end-point identifier
     *
     * @param endPointId the id of the end-point
     * @return the list of the associated channels if there are any, null otherwise
     */
    public List<ChannelEntity> findByEndPointId(Long endPointId) {
        LOGGER.info(" findByEndPointId(" + endPointId + ") - (ENTER)");
        List<ChannelEntity> ret = null;

        try {
            TypedQuery<ChannelEntity> q = em.createNamedQuery("ChannelEntity.findByEndPointId", ChannelEntity.class);
            q.setParameter("endPointId", endPointId);
            ret = q.getResultList();
        } catch (NoResultException exc) {
            LOGGER.debug("Endpoint " + endPointId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info(" findByEndPointId() - (LEAVE) + ret");
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Error during " + operation + " channelEntity : "
                + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }
    
    /**
     * Creates (or persists) the provided channel for a specific end point
	 * 
	 * @param newEntity the channel to be created
	 * @return the identifier of the newly inserted channel
	 */
	
	public ChannelEntity create(ChannelEntity newEntity){
		LOGGER.info("create(" + newEntity + ") - (ENTER)");

	    try {
	      em.persist(newEntity);
	      em.flush();
	    } catch (Exception ex) {
	      String msg = "Failed to create channel: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	    
	    LOGGER.info("create() - (LEAVE)");
	    return newEntity;
	}
	/**
	 * Updates an existing channel
	 * @param channel the channel to be updated
	 * @return the updated channel
	 */
	public ChannelEntity update(ChannelEntity channel){
		LOGGER.info("update(" + channel + ") - (ENTER)");
	    
		ChannelEntity ret=null;
	    try {
	      ret=em.merge(channel);
	      em.flush();
	    } catch (Exception ex) {
	      String msg = "Failed to update channel: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	    LOGGER.info("update() - (LEAVE)");
	    return ret;
	}
	
	 /**
	   * Deletes an existing channel
	   *
	   * @param channelId the id of the channel
	   */
	  public void delete(Long channelId) 
	  {
	    LOGGER.info("delete(channel with " + channelId +") - (ENTER)");

	    try {
	      ChannelEntity entity = read(channelId);
	      if (entity != null) {
	        em.remove(entity);
	        em.flush();
	        em.clear();
	      }
	    } catch (Exception ex) {
	      String msg = "Failed to delete channel: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	    
	    LOGGER.info("delete() - (LEAVE)");
	  }

}
