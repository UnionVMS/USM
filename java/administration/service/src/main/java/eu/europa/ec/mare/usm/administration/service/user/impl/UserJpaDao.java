package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.PasswordHistEntity;
import eu.europa.ec.mare.usm.information.entity.UserContextEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;

/**
 * JPA based data access of user data.
 */
public class UserJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    /**
     * Creates a new instance.
     */
    public UserJpaDao() {
    }

    /**
     * Creates a user.
     *
     * @param user the user to be created
     * @return the updated user
     */
    public UserEntity create(UserEntity user) {
        LOGGER.info("create(" + user + ") - (ENTER)");

        try {
            if (user.getPerson() != null) {
                em.persist(user.getPerson());
            }
            em.persist(user);
            em.flush();
        } catch (Exception ex) {
            String msg = "Failed to create user: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        LOGGER.info("create() - (LEAVE)");
        return user;
    }

    /**
     * Updates an existing user.
     *
     * @param user the user to be updated
     * @return the updated user
     */
    public UserEntity update(UserEntity user) {
        LOGGER.info("update(" + user + ") - (ENTER)");

        UserEntity ret = null;
        try {
            if (user.getPerson() != null) {
                if (user.getPerson().getPersonId() == null) {
                    em.persist(user.getPerson());
                } else {
                    em.merge(user.getPerson());
                }
            }
            ret = em.merge(user);
            em.flush();
        } catch (Exception ex) {
            String msg = "Failed to update user: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        LOGGER.info("update() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a user by its unique userName
     *
     * @param userName the name of the user to be found
     * @return null if nothing was found otherwise the existing user
     */
    public UserEntity read(String userName) {
        LOGGER.info("read(" + userName + ") - (ENTER)");

        UserEntity ret = null;

        try {
            TypedQuery<UserEntity> q = em.createNamedQuery("UserEntity.findByUserName",
                    UserEntity.class);

            q.setParameter("userName", userName);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("No entity was found with userName " + userName);
        }

        LOGGER.info("read() - (LEAVE)");
        return ret;
    }

    /**
     * Gets the password history for the user with the provided name.
     *
     * @param userName the user name
     * @return the possibly-empty password history
     */
    public List<PasswordHistEntity> getPasswordHistory(String userName) {
        LOGGER.info("getPasswordHistory(" + userName + ") - (ENTER)");

        Query q = em.createNamedQuery("PasswordHistEntity.findByUserName");

        q.setParameter("userName", userName);
        List<PasswordHistEntity> ret = q.getResultList();

        LOGGER.info("getPasswordHistory() - (LEAVE)");
        return ret;
    }

    public List<UserContextEntity> findActiveUsers(Long roleId) {
        LOGGER.info("findActiveUsers(" + roleId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserContextEntity.findByStatusActiveAndRoleId");
        q.setParameter("roleId", roleId);

        List<UserContextEntity> ret = q.getResultList();

        LOGGER.info("findActiveUsers() - (LEAVE)");
        return ret;
    }

    public List<UserContextEntity> findActiveUsersForScope(Long scopeId) {
        LOGGER.info("findActiveUsersForScope(" + scopeId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserContextEntity.findByStatusActiveAndScopeId");
        q.setParameter("scopeId", scopeId);

        List<UserContextEntity> ret = q.getResultList();

        LOGGER.info("findActiveUsersForScope() - (LEAVE)");
        return ret;
    }

    public List<UserEntity> getUsersByOrganisationId(Long organisationId) {
        LOGGER.info("getUsersByOrganisation(" + organisationId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserEntity.findByOrganisationId");
        q.setParameter("organisationId", organisationId);

        List<UserEntity> ret = q.getResultList();

        LOGGER.info("getUsersByOrganisation() - (LEAVE)");
        return ret;
    }
}
