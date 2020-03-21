package eu.europa.ec.mare.usm.administration.service.role.impl;

import eu.europa.ec.mare.usm.information.entity.RoleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * JPA based data access of Role data.
 */
public class RoleJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public RoleJpaDao() {
    }

    /**
     * Creates a role.
     *
     * @param role the role to be created
     * @return The created role entity
     */
    public RoleEntity create(RoleEntity role) {
        LOGGER.debug("create(" + role + ") - (ENTER)");

        try {
            em.persist(role);
            em.flush();
        } catch (Exception ex) {
            handleException("create", ex);
        }

        LOGGER.debug("create() - (LEAVE)");
        return role;
    }

    /**
     * Updates an existing role.
     *
     * @param role the role to be updated
     */
    public void update(RoleEntity role) {
        LOGGER.debug("update(" + role + ") - (ENTER)");

        try {
            em.merge(role);
            em.flush();
        } catch (Exception ex) {
            handleException("update", ex);
        }

        LOGGER.debug("update() - (LEAVE)");
    }

    /**
     * Deletes an existing role and its associate permissions.
     *
     * @param roleId the id of the role
     */
    public void delete(Long roleId) {
        LOGGER.debug("delete(" + roleId + ") - (ENTER)");

        try {
            RoleEntity entity = read(roleId);
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
     * Reads the Role with the provided identifier
     *
     * @param roleId the id of the role
     * @return the matching Role if it exists, null otherwise
     */
    public RoleEntity read(Long roleId) {
        LOGGER.debug("read() - (ENTER)");
        RoleEntity ret = null;

        try {
            TypedQuery<RoleEntity> q = em.createNamedQuery("RoleEntity.findByRoleId", RoleEntity.class);
            q.setParameter("roleId", roleId);
            ret = q.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.info("Role with id " + roleId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE)");
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Failed to " + operation + " role: " + ex.getMessage();

        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
