package eu.europa.ec.mare.usm.administration.service.person.impl;

import eu.europa.ec.mare.usm.information.entity.PendingDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA based Data access (object) for Pending Contact Details
 */
@Stateless
public class PendingDetailsJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PendingDetailsJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public PendingDetailsJpaDao() {
    }

    /**
     * Reads the list of all Pending Contact Details.
     *
     * @return the possibly-empty list of all Pending Contact Details
     */
    public List<PendingDetailsEntity> findAll() throws RuntimeException {
        LOGGER.debug("findAll() - (ENTER)");
        List<PendingDetailsEntity> ret = null;

        try {
            TypedQuery<PendingDetailsEntity> q = em.createNamedQuery(
                    "PendingDetailsEntity.findAll", PendingDetailsEntity.class);

            ret = q.getResultList();
        } catch (Exception ex) {
            handleException("find", ex);
        }

        LOGGER.debug("findAll() - (LEAVE)");
        return ret;
    }

    /**
     * Creates the provided Pending Contact Details.
     *
     * @param entity the Pending Contact Details entity to be persisted
     * @return the primary-key assigned to the Pending Contact Details
     */
    public Long create(PendingDetailsEntity entity) throws RuntimeException {
        LOGGER.debug("create() - (ENTER)");

        Long ret = null;

        try {
            em.persist(entity);
            em.flush();
            em.clear();

            ret = entity.getPendingDetailsId();
        } catch (Exception ex) {
            handleException("create", ex);
        }

        LOGGER.debug("create() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Reads the Pending Contact Details for the user with the provided user name
     *
     * @param userName the user name
     * @return the Pending Contact Details if they exist, null otherwise
     */
    public PendingDetailsEntity read(String userName) throws RuntimeException {
        LOGGER.debug("read() - (ENTER)");

        PendingDetailsEntity ret = null;

        try {
            TypedQuery<PendingDetailsEntity> q = em.createNamedQuery(
                    "PendingDetailsEntity.findByUserName", PendingDetailsEntity.class);

            q.setParameter("userName", userName);

            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("PendingDetails not found for: " + userName);
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Updates the provided Pending Contact Details.
     *
     * @param entity the Pending Contact Details entity to be updated
     */
    public void update(PendingDetailsEntity entity) throws RuntimeException {
        LOGGER.debug("update() - (ENTER)");

        try {
            em.merge(entity);
            em.flush();
            em.clear();

        } catch (Exception ex) {
            handleException("update", ex);
        }

        LOGGER.debug("update() - (LEAVE)");
    }

    /**
     * Deletes the Pending Contact Details for the user with the
     * provided user name
     *
     * @param userName the user name
     * @return the deleted contact details if they existed, null otherwise
     */
    public PendingDetailsEntity delete(String userName) throws RuntimeException {
        LOGGER.debug("delete(" + userName + ") - (ENTER)");

        PendingDetailsEntity ret = read(userName);
        if (ret != null) {
            em.remove(ret);
            em.flush();
            em.clear();
        }

        LOGGER.debug("delete() - (LEAVE)");
        return ret;
    }

    private void handleException(String operation, Exception ex) throws RuntimeException {
        String msg = "Error during " + operation + " pending contact details : " + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
