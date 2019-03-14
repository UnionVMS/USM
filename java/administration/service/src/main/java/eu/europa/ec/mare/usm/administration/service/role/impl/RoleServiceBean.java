package eu.europa.ec.mare.usm.administration.service.role.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.mare.audit.logger.AuditLogger;
import eu.europa.ec.mare.audit.logger.AuditLoggerFactory;
import eu.europa.ec.mare.audit.logger.AuditRecord;
import eu.europa.ec.mare.usm.administration.domain.AuditObjectTypeEnum;
import eu.europa.ec.mare.usm.administration.domain.AuditOperationEnum;
import eu.europa.ec.mare.usm.administration.domain.AuditRecordFactory;
import eu.europa.ec.mare.usm.administration.domain.ComprehensiveRole;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindPermissionsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindRolesQuery;
import eu.europa.ec.mare.usm.administration.domain.GetRoleQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Role;
import eu.europa.ec.mare.usm.administration.domain.RoleQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMApplication;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

import eu.europa.ec.mare.usm.administration.service.role.RoleService;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJpaDao;
import eu.europa.ec.mare.usm.information.entity.FeatureEntity;
import eu.europa.ec.mare.usm.information.entity.RoleEntity;
import eu.europa.ec.mare.usm.information.entity.UserContextEntity;

/**
 * Stateless session bean implementation of the RoleService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RoleServiceBean implements RoleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceBean.class);

  @Inject
  private RoleJdbcDao roleJdbcDao;
  @Inject
  private RoleJpaDao roleJpaDao;
  @Inject
  private FeatureJpaDao featureJpaDao;
  @Inject
  private FeatureJdbcDao featureJdbcDao;
  @Inject
  private UserJpaDao userJpaDao;
  @Inject
  private RoleValidator validator;
  @Inject
  private RoleConverter converter;
  

  
  private final AuditLogger auditLogger;

  /**
   * Creates a new instance
   */
  public RoleServiceBean() 
  {
    auditLogger = AuditLoggerFactory.getAuditLogger();
  }

  @Override
  public List<String> getRoleNames(ServiceRequest<RoleQuery> request) 
  {
    LOGGER.info("getRoleNames(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);
    List<String> names = roleJdbcDao.getRoleNames();

    LOGGER.info("getRoleNames() - (LEAVE)");
    return names;
  }

  @Override
  public Role getRole(ServiceRequest<GetRoleQuery> request) 
  {
    LOGGER.info("getRole(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);
    RoleEntity result = roleJpaDao.read(request.getBody().getRoleId());
    Role role = converter.convert(result);
    if (role != null) {
      List<UserContextEntity> activeUsers = userJpaDao.findActiveUsers(request.getBody().getRoleId());
      role.setActiveUsers(activeUsers == null ? 0 : activeUsers.size());
    }
    
    LOGGER.info("getRole() - (LEAVE)");
    return role;
  }

  @Override
  public PaginationResponse<ComprehensiveRole> findRoles(ServiceRequest<FindRolesQuery> request) 
  {
    LOGGER.info("findRoles(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);

    PaginationResponse<ComprehensiveRole> ret = roleJdbcDao.findRoles(request.getBody());

    LOGGER.info("findRoles() - (LEAVE)");
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ComprehensiveRole createRole(ServiceRequest<ComprehensiveRole> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("createRole( " + request + " ) - (ENTER)");

    validator.assertValid(request, USMFeature.manageRoles, true);

    if (roleJdbcDao.roleExists(request.getBody().getName())) {
      throw new IllegalArgumentException("Role already exists.");
    }
    RoleEntity entity = new RoleEntity();
    converter.updateEntity(entity, request.getBody());
    entity.setCreatedBy(request.getRequester());
    entity.setCreatedOn(new Date());
    entity = roleJpaDao.create(entity);
    
    ComprehensiveRole ret = converter.convertComprehensively(entity);
    

		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.ROLE.getValue(), request.getRequester(),
				request.getBody().getName(), request.getBody().getDescription());
		auditLogger.logEvent(auditRecord);


    LOGGER.info("createRole() - (LEAVE)");
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void updateRole(ServiceRequest<ComprehensiveRole> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("updateRole( " + request + " ) - (ENTER)");

    validator.assertValid(request, USMFeature.manageRoles, false);

    RoleEntity entity = roleJpaDao.read(request.getBody().getRoleId());
    if (entity == null) {
      throw new IllegalArgumentException("Role does not exist");
    }
    
    converter.updateEntity(entity, request.getBody());
    entity.setModifiedBy(request.getRequester());
    entity.setModifiedOn(new Date());
    
    roleJpaDao.update(entity);
    

		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.ROLE.getValue(), request.getRequester(),
				request.getBody().getName(), request.getBody().getDescription());
		auditLogger.logEvent(auditRecord);
	

    LOGGER.info("updateRole() - (LEAVE)");
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void deleteRole(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("deleteRole( " + request + " ) - (ENTER)");

    validator.assertValid(request, USMFeature.manageRoles, "roleId");
    roleJpaDao.delete(request.getBody());
    
    AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.DELETE.getValue(), AuditObjectTypeEnum.ROLE.getValue(), request.getRequester(),
				Long.toString(request.getBody()), Long.toString(request.getBody()));
		auditLogger.logEvent(auditRecord);
	

    LOGGER.info("deleteRole() - (LEAVE)");
  }

  @Override
  public List<Feature> findFeaturesByApplication(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("findFeaturesByApplication( " + request + " ) - (ENTER)");
    
    List<FeatureEntity> lst = featureJpaDao.getFeaturesByApplication(request.getBody());

    List<Feature> ret = new ArrayList<>();
    for (FeatureEntity entity : lst) {
      ret.add(converter.convertWithoutRoles(entity));
    }

    LOGGER.info("findFeaturesByApplication() - (LEAVE)");
    return ret;
  }

  @Override
  public List<String> getGroupNames(ServiceRequest<String> request) 
  {
    LOGGER.info("getGroupNames(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);

    List<String> ret = featureJdbcDao.getGroupNames();

    LOGGER.info("getGroupNames() - (LEAVE)");
    return ret;
  }

  @Override
  public List<Feature> findPermissions(ServiceRequest<FindPermissionsQuery> request) 
  {
    LOGGER.info("findPermissions(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);

    List<FeatureEntity> response = featureJpaDao.findFeatures(request.getBody());
    
    List<Feature> ret = new ArrayList<>();
    for (FeatureEntity feature : response) {
      ret.add(converter.convertWithRoles(feature));
    }

    LOGGER.info("findPermissions() - (LEAVE)");
    return ret;
  }

  @Override
  public List<ComprehensiveRole> getRoles(ServiceRequest<RoleQuery> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("getRoles(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewRoles);
    featureSet.add(USMFeature.manageRoles);

    validator.assertValid(request, "query", featureSet);
    List<ComprehensiveRole> ret = roleJdbcDao.getRoles();

    LOGGER.info("getRoles() - (LEAVE)");
    return ret;
  }

}
