package eu.europa.ec.mare.usm.administration.service.userPreference.impl;

import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class PreferenceJpaDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public PreferenceJpaDao() {
    }

    /**
     * Retrieves the preferences for the context with the provided identifier
     *
     * @param contextId the user context identifier
     * @return the possibly-empty list of preferences
     */
    public List<PreferenceEntity> read(Long contextId) {
        LOGGER.info("read(" + contextId + ") - (ENTER)");

        TypedQuery<PreferenceEntity> q = em.createNamedQuery("PreferenceEntity.findByContextId",
                PreferenceEntity.class);

        q.setParameter("contextId", contextId);
        List<PreferenceEntity> ret = q.getResultList();

        LOGGER.info("read() - (LEAVE)");
        return ret;
    }
}
