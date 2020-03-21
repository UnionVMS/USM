package eu.europa.ec.mare.usm.administration.service.userContext.impl;

import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogModelMapper;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.AuditProducer;
import eu.europa.ec.mare.usm.administration.service.role.impl.RoleJpaDao;
import eu.europa.ec.mare.usm.administration.service.scope.impl.ScopeJpaDao;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJpaDao;
import eu.europa.ec.mare.usm.administration.service.userContext.UserContextService;
import eu.europa.ec.mare.usm.administration.service.userPreference.impl.PreferenceJpaDao;
import eu.europa.ec.mare.usm.information.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;

/**
 * Stateless session bean implementation of the UserContextService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserContextServiceBean implements UserContextService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextServiceBean.class.getName());

    @Inject
    private UserContextJdbcDao userContextJdbcDao;

    @Inject
    private UserContextJpaDao userContextJpaDao;

    @Inject
    private UserJpaDao userJpaDao;

    @Inject
    private PreferenceJpaDao prefJpaDao;

    @Inject
    private UserContextValidator validator;

    @Inject
    private RoleJpaDao roleJpaDao;

    @Inject
    private ScopeJpaDao scopeJpaDao;

    @Inject
    private AuditProducer auditProducer;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public UserContextResponse getUserContexts(ServiceRequest<FindUserContextsQuery> request) {
        LOGGER.debug("getUserContexts(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewUsers);
        featureSet.add(USMFeature.manageUsers);

        validator.assertValid(request, "query", featureSet);

        UserContextResponse ret = userContextJdbcDao.findUserContexts(request.getBody().getUserName());

        LOGGER.debug("getUserContexts() - (LEAVE)");
        return ret;
    }

    @Override
    public UserContext createUserContext(ServiceRequest<UserContext> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("createUserContext( " + request + " ) - (ENTER)");

        validator.assertValid(request, USMFeature.manageUsers, true);

        //check if the Uk is not violated

        if (userContextJdbcDao.userContextExists(request.getBody().getUserName(),
                request.getBody().getRoleId(), request.getBody().getScopeId())) {
            throw new IllegalArgumentException("The context already exists!");
        }
        UserContextEntity entity = new UserContextEntity();
        copy(entity, request.getBody());
        entity = userContextJpaDao.create(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.CONTEXT.getValue() + " " +
                        request.getBody().getUserName(), request.getBody().getUserName(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("createUserContext() - (LEAVE)");
        return convert(entity);
    }

    @Override
    public UserContext updateUserContext(ServiceRequest<UserContext> request) {
        LOGGER.debug("updateUserContext(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageUsers, false);

        UserContextEntity entity = userContextJpaDao.read(request.getBody().getUserContextId());
        if (entity == null) {
            throw new IllegalArgumentException("UserContext Does not exist");
        }

        copy(entity, request.getBody());
        UserContextEntity updatedUserContext = userContextJpaDao.update(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.CONTEXT.getValue() + " " +
                        request.getBody().getUserName(), request.getBody().getUserName(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateUserContext() - (LEAVE)");
        return convert(updatedUserContext);
    }

    @Override
    public void deleteUserContext(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("deleteUserContext( " + request + " ) - (ENTER)");

        validator.assertValid(request, USMFeature.manageUsers, "role");

        userContextJpaDao.delete(Long.valueOf(request.getBody()));

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.DELETE.getValue(), AuditObjectTypeEnum.CONTEXT.getValue() + " " +
                        request.getBody(), request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteUserContext() - (LEAVE)");
    }

    @Override
    public void copyUserProfiles(ServiceRequest<UserContextResponse> request, String toUserName)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("copyUserProfiles(" + request + ", " + toUserName + ") - (ENTER)");

        validator.assertValidCopy(request, USMFeature.copyUserProfile, toUserName);

        UserEntity destinationUser = userJpaDao.read(toUserName);
        if (destinationUser == null) {
            throw new IllegalArgumentException("User " + toUserName + " does not exist");
        }

        List<ComprehensiveUserContext> obsolete = userContextJdbcDao.findUserContexts(toUserName)
                .getResults();
        for (ComprehensiveUserContext ctx : obsolete) {
            userContextJpaDao.delete(ctx.getUserContextId());
        }

        List<ComprehensiveUserContext> copied = request.getBody().getResults();
        for (ComprehensiveUserContext element : copied) {
            UserContextEntity entity = convert(element);
            entity.setUser(destinationUser);
            List<PreferenceEntity> pl = prefJpaDao.read(element.getUserContextId());
            if (pl != null && !pl.isEmpty()) {
                for (PreferenceEntity item : pl) {
                    PreferenceEntity pe = new PreferenceEntity();
                    pe.setOption(item.getOption());
                    pe.setOptionValue(item.getOptionValue());
                    item.setUserContext(entity);
                }
                entity.setPreferenceList(pl);
            }
            userContextJpaDao.create(entity);
        }

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.COPY.getValue(), AuditObjectTypeEnum.CONTEXT.getValue() + " " +
                        toUserName, toUserName, request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("copyUserProfiles() - (LEAVE)");
    }

    private UserContextEntity convert(ComprehensiveUserContext src) {
        UserContextEntity ret = null;

        if (src != null) {
            ret = new UserContextEntity();

            RoleEntity roleEntity = roleJpaDao.read(src.getRoleId());
            ret.setRole(roleEntity);

            if (src.getScopeId() != null) {
                ScopeEntity scopeEntity = scopeJpaDao.read(src.getScopeId());
                ret.setScope(scopeEntity);
            }
        }
        return ret;
    }

    private void copy(UserContextEntity dest, UserContext src) {
        if (dest != null && src != null) {
            dest.setUserContextId(src.getUserContextId());
            dest.setUser(userJpaDao.read(src.getUserName()));

            if (src.getRoleId() != null) {
                dest.setRole(new RoleEntity());
                dest.getRole().setRoleId(src.getRoleId());
            } else {
                dest.setRole(null);
            }

            if (src.getScopeId() != null) {
                dest.setScope(new ScopeEntity());
                dest.getScope().setScopeId(src.getScopeId());
            } else {
                dest.setScope(null);
            }
        }
    }

    private UserContext convert(UserContextEntity src) {
        UserContext ret = new UserContext();

        ret.setUserContextId(src.getUserContextId());
        ret.setUserName(src.getUser().getUserName());
        ret.setRoleId(src.getRole().getRoleId());
        if (src.getScope() != null && src.getScope().getScopeId() != null) {
            ret.setScopeId(src.getScope().getScopeId());
        } else {
            ret.setScopeId(null);
        }

        return ret;
    }
}
