package eu.europa.ec.mare.usm.administration.service.policy.impl;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.information.entity.PolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * JPA based data-access-object for the retrieval and storage of Policy
 * definition/configuration properties.
 */
@Stateless
public class PolicyJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;


    /**
     * Reads the Policy definition for the provided subject
     *
     * @param subject the policy subject
     * @return the possibly-empty list of Policy entities
     */
    public List<PolicyEntity> readPolicy(String subject) {
        LOGGER.debug("readPolicy(" + subject + ") - (ENTER)");

        List<PolicyEntity> ret = null;
        try {
            TypedQuery<PolicyEntity> q = em.createNamedQuery("PolicyEntity.findBySubject", PolicyEntity.class);
            q.setParameter("subject", subject);
            ret = q.getResultList();
        } catch (Exception ex) {
            String msg = "Failed to read policy: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        LOGGER.debug("readPolicy() - (LEAVE): " + ret.size());
        return ret;
    }

    /**
     * Updates the provided Policy definition
     *
     * @param entityList the policy definition
     */
    public void updatePolicy(List<PolicyEntity> entityList) {
        LOGGER.debug("updatePolicy(" + entityList + ") - (ENTER)");

        try {
            if (entityList != null && !entityList.isEmpty()) {
                for (PolicyEntity entity : entityList) {
                    LOGGER.info("updating: " + entity);
                    em.merge(entity);
                    em.flush();
                    LOGGER.info("updated: " + entity.getName());
                }
                em.flush();
            }
        } catch (Exception ex) {
            String msg = "Failed to update policy: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        LOGGER.debug("updatePolicy() - (LEAVE)");
    }

    public PolicyEntity updatePolicyProperty(PolicyEntity e) {
        LOGGER.debug("updatePolicyProperty(" + e + ") - (ENTER)");

        LOGGER.info("updating: " + e);
        em.merge(e);
        em.flush();
        LOGGER.info("updated: " + e.getName());
        LOGGER.debug("updatePolicyProperty() - (LEAVE)");
        return e;
    }

    public List<PolicyEntity> findPolicies(FindPoliciesQuery request) {
        LOGGER.debug("findPolicies(" + request + ") - (ENTER)");

        String subject = request.getSubject();
        String name = request.getName();

        List<PolicyEntity> ret = null;
        try {
            StringBuilder jpql = new StringBuilder("SELECT p FROM PolicyEntity p WHERE 1=1 ");
            if (subject != null) {
                jpql.append(" AND p.subject = :subject ");
            }
            if (name != null) {
                jpql.append(" AND lower(p.name) like :name ");
            }
            TypedQuery<PolicyEntity> query = em.createQuery(jpql.toString(), PolicyEntity.class);
            if (subject != null) {
                query.setParameter("subject", subject);
            }
            if (name != null) {
                query.setParameter("name", "%" + name.toLowerCase() + "%");
            }
            ret = query.getResultList();
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("findPolicies() - (LEAVE)");
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Failed to " + operation + " feature: " + ex.getMessage();

        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
