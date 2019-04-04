package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.OrganisationEntity;

/**
 * JPA based data access of Organisation data.
 */
public class OrganisationJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    /**
     * Creates a new instance
     */
    public OrganisationJpaDao() {
    }

    /**
     * Creates (or persists) the provided Organisation.
     *
     * @param entity the Organisation to be persisted
     * @return The created Organisation entity
     */
    public OrganisationEntity create(OrganisationEntity entity) {
        LOGGER.info("create(" + entity + ") - (ENTER)");
        try {
            em.persist(entity);
            em.flush();
        } catch (Exception ex) {
            handleException("create", ex);
        }

        LOGGER.info("create() - (LEAVE)");
        return entity;
    }

    /**
     * Reads the Organisation with the provided (internal) unique identifier
     *
     * @param organisationId the Organisation (internal) unique identifier
     * @return the matching Organisation if it exists, null otherwise
     */
    public OrganisationEntity read(Long organisationId) {
        LOGGER.info("read() - (ENTER)");

        OrganisationEntity ret = null;

        try {
            ret = em.find(OrganisationEntity.class, organisationId);


            TypedQuery<OrganisationEntity> q = em.createNamedQuery("OrganisationEntity.findByOrganisationId",
                    OrganisationEntity.class);

            q.setParameter("organisationId", organisationId);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("Organistion " + organisationId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("read() - (LEAVE)");
        return ret;
    }

    /**
     * Reads the Organisation with the provided name
     *
     * @param organisationName the Organisation name
     * @return the matching Organisation if it exists, null otherwise
     */
    public OrganisationEntity read(String organisationName) {
        LOGGER.info("read() - (ENTER)");
        OrganisationEntity ret = null;

        try {
            TypedQuery<OrganisationEntity> q = em.createNamedQuery("OrganisationEntity.findByName",
                    OrganisationEntity.class);

            q.setParameter("name", organisationName);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("Organistion " + organisationName + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("read() - (LEAVE)");
        return ret;
    }

    /**
     * Updates the provided Organisation
     *
     * @param entity the Organisation to be updated
     * @return the updated Organisation
     */
    public OrganisationEntity update(OrganisationEntity entity) {
        LOGGER.info("update() - (ENTER)");
        OrganisationEntity ret = null;
        try {
            ret = em.merge(entity);
            em.flush();
        } catch (Exception ex) {
            handleException("update", ex);
        }

        LOGGER.info("update() - (LEAVE)");
        return ret;
    }

    /**
     * Delete the Organisation with the provided (internal) unique identifier
     *
     * @param organisationId the Organisation (internal) unique identifier
     */
    public void delete(Long organisationId) {
        LOGGER.info("delete() - (ENTER)");

        try {
            OrganisationEntity ret = em.find(OrganisationEntity.class,
                    organisationId);
            em.remove(ret);
            em.flush();
        } catch (NoResultException exc) {
            LOGGER.debug("Organistion " + organisationId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.info("delete() - (LEAVE)");
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Error during " + operation +
                " organisation : " + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
