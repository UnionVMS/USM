package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.EndPointEntity;

/**
 * JPA based data access of End-Point data.
 */
public class EndPointJpaDao {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EndPointJpaDao.class);

	@PersistenceContext(unitName = "USM-Administration")
	private EntityManager em;

	/**
	 * Creates a new instance
	 */
	public EndPointJpaDao() {
	}

	/*public EndPointEntity read(String name) {
		LOGGER.info(" read(" + name + ") - (ENTER)");
		EndPointEntity ret = null;

		try {
			TypedQuery<EndPointEntity> q = em.createNamedQuery(
					"EndPointEntity.findByName", EndPointEntity.class);

			q.setParameter("name", name);
			ret = q.getSingleResult();
		} catch (NoResultException exc) {
			LOGGER.debug("Endpoint " + name + " not found");
		} catch (Exception ex) {
			handleException("read", ex);
		}

		LOGGER.info(" read() - (LEAVE)");
		return ret;
	}
*/
	/**
	   * Reads the Endpoint with the provided (internal) unique identifier
	   * 
	   * @param endpointId the end point (internal) unique identifier
	   * 
	   * @return the matching end point if it exists, null otherwise
	   */
	  public EndPointEntity read(Long endPointId) 
	  {
	    LOGGER.info("read() - (ENTER)");

	    EndPointEntity ret = null;
	    
	    try {
	      TypedQuery<EndPointEntity> q = em.createNamedQuery(
                  "EndPointEntity.findByEndPointId", EndPointEntity.class);
	      q.setParameter("endPointId", endPointId);
	      ret=q.getSingleResult();
	    } catch (NoResultException exc) {
	      LOGGER.debug("EndPointEntity " + endPointId + " not found");
	    } catch (Exception ex) {
	      handleException("read", ex);
	    }

	    LOGGER.info("read() - (LEAVE)");
	    return ret;
	  }

	  /**
	     * Reads the end-points associated with the provided organisation identifier
	     *
	     * @param organisationId the id of the organisation
	     * @return the list of the associated end-points if there are any
	     */
    public List<EndPointEntity> getEndPointsByOrganisationId(Long organisationId)
    {
        LOGGER.info("getEndPointsByOrganisationId() - (ENTER)");

        List<EndPointEntity> ret = null;

        try {
            TypedQuery<EndPointEntity> q = em.createNamedQuery(
                    "EndPointEntity.findByOrganisationId", EndPointEntity.class);

            q.setParameter("organisationId", organisationId);
            ret = q.getResultList();
        } catch (NoResultException exc) {
            LOGGER.debug("EndPointEntity " + organisationId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("getEndPointsByOrganisationId() - (LEAVE)");
        return ret;
    }
    
    
	
	private void handleException(String operation, Exception ex)
			throws RuntimeException {
		String msg = "Error during " + operation + " endPoint : "
				+ ex.getMessage();
		LOGGER.error(msg, ex);
		throw new RuntimeException(msg, ex);
	}
	
	/**
	 * Creates (or persists) the provided end point for a specific organisation
	 * 
	 * @param newEntity the end-point to be created
	 * @return the newly inserted end-point entity
	 */
	
	public EndPointEntity create(EndPointEntity newEntity){
		LOGGER.info("create(" + newEntity + ") - (ENTER)");
	    
	    try {
	      em.persist(newEntity);
	      em.flush();
	     
	    } catch (Exception ex) {
	      String msg = "Failed to create endPoint: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	    
	    LOGGER.info("create() - (LEAVE)");
	    return newEntity;
	}
	
	/**
     * Reads the end-point
     *
     * @param entityName the name of the end-point
     * @param organisationName the name of the organisation
     * @return the end-points if it exists, null otherwise
     */
	public EndPointEntity retrieveEndPointByOrganisation(String entityName, String organisationName){
		LOGGER.info("retrieveEndPointByOrganisation(" + entityName + "," + organisationName + ") - (ENTER)");

        EndPointEntity ret = null;

        try {
            TypedQuery<EndPointEntity> q = em.createNamedQuery(
                    "EndPointEntity.findByOrganisationName", EndPointEntity.class);

            q.setParameter("organisationName", organisationName);
            q.setParameter("endpointName", entityName);
            ret = q.getSingleResult();
        } catch(NoResultException ex){
        	LOGGER.debug("EndPointEntity " + entityName + " was not inserted");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("retrieveEndPointByOrganisation() - (LEAVE)");
        return ret;
	}
	
	/**
	 * Updates and existing end-point
	 * @param endpoint the end-point to be updated
	 * @return the updated end-point entity
	 */
	public EndPointEntity update(EndPointEntity endpoint){
		LOGGER.info("update(" + endpoint + ") - (ENTER)");
	    
		EndPointEntity ret=null;
	    try {
	      ret=em.merge(endpoint);
	      em.flush();
	    } catch (Exception ex) {
	      String msg = "Failed to update endPoint: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	    LOGGER.info("update() - (LEAVE)");
	    return ret;
	}
	
	 /**
	   * Deletes an existing end point and its associate channels and contacts.
	   *
	   * @param endpointId the id of the end point
	   */
	  public void delete(Long endpointId) 
	  {
	    LOGGER.info("delete(EndPoint with " + endpointId +") - (ENTER)");

	    try {
	      EndPointEntity entity = read(endpointId);
	      if (entity != null) {
	        em.remove(entity);
	        em.flush();
	        em.clear();
	      }
	    } catch (Exception ex) {
	      String msg = "Failed to delete entity: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }
	  }
}
