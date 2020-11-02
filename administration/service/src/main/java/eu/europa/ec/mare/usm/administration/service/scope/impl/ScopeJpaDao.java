package eu.europa.ec.mare.usm.administration.service.scope.impl;

import eu.europa.ec.mare.usm.administration.domain.FindDataSetQuery;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.ScopeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA based data access of Scope data.
 */
@Stateless
public class ScopeJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public ScopeJpaDao() {
    }

    /**
     * Creates a scope.
     *
     * @param scope the scope to be created
     * @return The created scope entity
     */
    public ScopeEntity create(ScopeEntity scope) {
        LOGGER.debug("create(" + scope + ") - (ENTER)");

        try {
            em.persist(scope);
            em.flush();
        } catch (Exception ex) {
            handleException("create", ex);
        }

        LOGGER.debug("create() - (LEAVE)");
        return scope;
    }

    /**
     * Updates an existing scope.
     *
     * @param scope the scope to be updated
     * @return the updated entity
     */
    public ScopeEntity update(ScopeEntity scope) {
        LOGGER.debug("update(" + scope + ") - (ENTER)");

        ScopeEntity ret = null;
        try {
            ret = em.merge(scope);
            em.flush();
        } catch (Exception ex) {
            handleException("update", ex);
        }

        LOGGER.debug("update() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing scope.
     *
     * @param scopeId the id of the scope to be deleted
     */
    public void delete(Long scopeId) {
        LOGGER.debug("delete(" + scopeId + ") - (ENTER)");

        try {
            ScopeEntity entity = read(scopeId);
            if (entity != null) {
                em.remove(entity);
                em.flush();
                em.clear();
            }
        } catch (Exception ex) {
            handleException("delete", ex);
        }

        LOGGER.debug("delete() - (LEAVE)");
    }

    /**
     * Reads the Scope with the provided identifier
     *
     * @param scopeId the id of the scope
     * @return the matching Scope if it exists, null otherwise
     */
    public ScopeEntity read(Long scopeId) {
        LOGGER.debug("read(" + scopeId + ") - (ENTER)");
        ScopeEntity ret = null;

        try {
            TypedQuery<ScopeEntity> q = em.createNamedQuery("ScopeEntity.findByScopeId", ScopeEntity.class);
            q.setParameter("scopeId", scopeId);
            ret = q.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.info("Scope with id " + scopeId + " not found", ex);
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE)");
        return ret;
    }

    public List<DatasetEntity> findDatasets(FindDataSetQuery request) {
        LOGGER.debug("findDatasets(" + request + ") - (ENTER)");

        String application = request.getApplicationName();
        String category = request.getCategory();

        List<DatasetEntity> ret = null;
        try {
            StringBuilder jpql = new StringBuilder("SELECT d FROM DatasetEntity d WHERE 1=1 ");
            if (application != null) {
                jpql.append(" AND d.application.name = :application ");
            }
            if (category != null) {
                jpql.append(" AND d.category = :category ");
            }
            TypedQuery<DatasetEntity> query = em.createQuery(jpql.toString(), DatasetEntity.class);
            if (application != null) {
                query.setParameter("application", application);
            }
            if (category != null) {
                query.setParameter("category", category);
            }
            ret = query.getResultList();
        } catch (Exception ex) {
            handleException("findDatasets of", ex);
        }

        LOGGER.debug("findDatasets() - (LEAVE)");
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Failed to " + operation + " scope: " + ex.getMessage();

        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }
}
