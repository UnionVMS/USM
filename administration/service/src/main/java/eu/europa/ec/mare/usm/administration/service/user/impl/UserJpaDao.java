package eu.europa.ec.mare.usm.administration.service.user.impl;

import eu.europa.ec.mare.usm.information.entity.PasswordHistEntity;
import eu.europa.ec.mare.usm.information.entity.UserContextEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

/**
 * JPA based data access of user data.
 */
public class UserJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public UserJpaDao() {
    }

    /**
     * Creates a user.
     *
     * @param user the user to be created
     * @return the updated user
     */
    public UserEntity create(UserEntity user) {
        LOGGER.debug("create(" + user + ") - (ENTER)");

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

        LOGGER.debug("create() - (LEAVE)");
        return user;
    }

    /**
     * Updates an existing user.
     *
     * @param user the user to be updated
     * @return the updated user
     */
    public UserEntity update(UserEntity user) {
        LOGGER.debug("update(" + user + ") - (ENTER)");

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

        LOGGER.debug("update() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a user by its unique userName
     *
     * @param userName the name of the user to be found
     * @return null if nothing was found otherwise the existing user
     */
    public UserEntity read(String userName) {
        LOGGER.debug("read(" + userName + ") - (ENTER)");

        UserEntity ret = null;

        try {
            TypedQuery<UserEntity> q = em.createNamedQuery("UserEntity.findByUserName", UserEntity.class);

            q.setParameter("userName", userName);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("No entity was found with userName " + userName);
        }

        LOGGER.debug("read() - (LEAVE)");
        return ret;
    }

    /**
     * Gets the password history for the user with the provided name.
     *
     * @param userName the user name
     * @return the possibly-empty password history
     */
    public List<PasswordHistEntity> getPasswordHistory(String userName) {
        LOGGER.debug("getPasswordHistory(" + userName + ") - (ENTER)");

        Query q = em.createNamedQuery("PasswordHistEntity.findByUserName");

        q.setParameter("userName", userName);
        List<PasswordHistEntity> ret = q.getResultList();

        LOGGER.debug("getPasswordHistory() - (LEAVE)");
        return ret;
    }

    public List<UserContextEntity> findActiveUsers(Long roleId) {
        LOGGER.debug("findActiveUsers(" + roleId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserContextEntity.findByStatusActiveAndRoleId");
        q.setParameter("roleId", roleId);

        List<UserContextEntity> ret = q.getResultList();

        LOGGER.debug("findActiveUsers() - (LEAVE)");
        return ret;
    }

    public List<UserContextEntity> findActiveUsersForScope(Long scopeId) {
        LOGGER.debug("findActiveUsersForScope(" + scopeId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserContextEntity.findByStatusActiveAndScopeId");
        q.setParameter("scopeId", scopeId);

        List<UserContextEntity> ret = q.getResultList();

        LOGGER.debug("findActiveUsersForScope() - (LEAVE)");
        return ret;
    }

    public List<UserEntity> getUsersByOrganisationId(Long organisationId) {
        LOGGER.debug("getUsersByOrganisation(" + organisationId + ") - (ENTER)");

        Query q = em.createNamedQuery("UserEntity.findByOrganisationId");
        q.setParameter("organisationId", organisationId);

        List<UserEntity> ret = q.getResultList();

        LOGGER.debug("getUsersByOrganisation() - (LEAVE)");
        return ret;
    }
}
