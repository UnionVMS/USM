package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import eu.europa.ec.mare.usm.information.entity.EndPointContactEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class EndPointContactJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndPointContactJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public EndPointContactJpaDao() {
    }

    public EndPointContactEntity read(Long endPointContactId) {
        LOGGER.debug(" read(" + endPointContactId + ") - (ENTER)");
        EndPointContactEntity ret = null;

        try {
            TypedQuery<EndPointContactEntity> q =
                    em.createNamedQuery("EndPointContactEntity.findById", EndPointContactEntity.class);
            q.setParameter("endPointContactId", endPointContactId);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("Endpoint " + endPointContactId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug(" read() - (LEAVE)");
        return ret;
    }

    public List<EndPointContactEntity> findContactByEndPointId(Long endPointId) {
        LOGGER.debug(" read(" + endPointId + ") - (ENTER)");
        List<EndPointContactEntity> ret = null;

        try {
            TypedQuery<EndPointContactEntity> q =
                    em.createNamedQuery("EndPointContactEntity.findByEndPointId", EndPointContactEntity.class);
            q.setParameter("endPointId", endPointId);
            ret = q.getResultList();
        } catch (NoResultException exc) {
            LOGGER.debug("Endpoint " + endPointId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug(" read() - (LEAVE)");
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Error during " + operation + " organisation : "
                + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

    public EndPointContactEntity create(EndPointContactEntity epcontact) {
        LOGGER.debug(" create(" + epcontact + ") - (ENTER)");
        try {
            em.persist(epcontact);
            em.flush();
        } catch (Exception ex) {
            String msg = "Failed to create endPointContact: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        LOGGER.debug(" create() - (LEAVE)");
        return epcontact;
    }

    public void delete(EndPointContactEntity epcontact) {
        LOGGER.debug(" delete(" + epcontact + ") - (ENTER)");

        try {
            em.remove(epcontact);
            em.flush();
        } catch (Exception ex) {
            String msg = "Failed to create endPointContact: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        LOGGER.debug(" delete() - (LEAVE)");

    }
}
