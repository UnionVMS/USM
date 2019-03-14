package eu.europa.ec.mare.usm.administration.service.policy.impl;

import java.util.ArrayList;
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
import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMApplication;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.policy.PolicyService;
import eu.europa.ec.mare.usm.information.entity.PolicyEntity;

/**
 * Stateless session bean implementation of the ViewPoliciesService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PolicyServiceBean implements PolicyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PolicyServiceBean.class.getName());

  @Inject
  private PolicyJpaDao policyJpaDao;

  @Inject
  private PolicyJdbcDao policyJdbcDao;

  @Inject
  private PolicyValidator validator;

  @EJB
  private DefinitionService definitionService;
  
  private final AuditLogger auditLogger;

  /**
   * Creates a new instance
   */
  public PolicyServiceBean() 
  {
    auditLogger = AuditLoggerFactory.getAuditLogger();
  }

  @Override
  public Policy updatePolicy(ServiceRequest<Policy> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("updatePolicy(" + request + ") - (ENTER)");

    validator.assertValidPolicyProperty(request);

    String subject = request.getBody().getSubject();
    String name = request.getBody().getName();
    String value = request.getBody().getValue();

    List<PolicyEntity> policyEntities = policyJpaDao.readPolicy(subject);

    for (PolicyEntity policyEntity : policyEntities) {
      if (policyEntity.getName().equals(name)
              && policyEntity.getSubject().equals(subject)) {

        policyEntity.setValue(value);
        policyJpaDao.updatePolicyProperty(policyEntity);

        definitionService.evictDefinition(subject);
        

    		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
    				AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.POLICY.getValue(), request.getRequester(),
    				name, request.getBody().getDescription());
    		auditLogger.logEvent(auditRecord);
    	

        return convertToDomain(policyEntity);
      }
    }

    LOGGER.info("updatePolicy() - (LEAVE)");
    return null;
  }

  @Override
  public List<Policy> findPolicies(ServiceRequest<FindPoliciesQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("findPolicies(" + request + ") - (ENTER)");

    validator.assertValid(request, USMFeature.configurePolicies, "query");

    List<PolicyEntity> policyEntities = policyJpaDao.findPolicies(request.getBody());

    List<Policy> ret = new ArrayList<>();
    for (PolicyEntity e : policyEntities) {
      ret.add(convertToDomain(e));
    }

    LOGGER.info("findPolicies() - (LEAVE)");
    return ret;
  }

  @Override
  public List<String> getSubjects(ServiceRequest<NoBody> request) 
  {
    LOGGER.info("getSubjects(" + request + ") - (ENTER)");

    validator.assertValid(request, USMFeature.configurePolicies);

    List<String> ret = policyJdbcDao.getSubjects();

    LOGGER.info("getSubjects() - (LEAVE)");
    return ret;
  }

  private Policy convertToDomain(PolicyEntity src) 
  {
    Policy ret = new Policy();
    
    ret.setPolicyId(src.getPolicyId());
    ret.setName(src.getName());
    ret.setDescription(src.getDescription());
    ret.setSubject(src.getSubject());
    ret.setValue(src.getValue());
    
    return ret;
  }

}
