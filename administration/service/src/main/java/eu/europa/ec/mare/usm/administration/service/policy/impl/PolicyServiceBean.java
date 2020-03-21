package eu.europa.ec.mare.usm.administration.service.policy.impl;

import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogModelMapper;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.AuditProducer;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.policy.PolicyService;
import eu.europa.ec.mare.usm.information.entity.PolicyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    @Inject
    private AuditProducer auditProducer;

    @Override
    public Policy updatePolicy(ServiceRequest<Policy> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("updatePolicy(" + request + ") - (ENTER)");

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

                String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                        AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.POLICY.getValue() + " " +
                                name, request.getBody().getDescription(), request.getRequester());
                auditProducer.sendModuleMessage(auditLog);

                return convertToDomain(policyEntity);
            }
        }

        LOGGER.debug("updatePolicy() - (LEAVE)");
        return null;
    }

    @Override
    public List<Policy> findPolicies(ServiceRequest<FindPoliciesQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findPolicies(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.configurePolicies, "query");

        List<PolicyEntity> policyEntities = policyJpaDao.findPolicies(request.getBody());

        List<Policy> ret = new ArrayList<>();
        for (PolicyEntity e : policyEntities) {
            ret.add(convertToDomain(e));
        }

        LOGGER.debug("findPolicies() - (LEAVE)");
        return ret;
    }

    @Override
    public List<String> getSubjects(ServiceRequest<NoBody> request) {
        LOGGER.debug("getSubjects(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.configurePolicies);

        List<String> ret = policyJdbcDao.getSubjects();

        LOGGER.debug("getSubjects() - (LEAVE)");
        return ret;
    }

    private Policy convertToDomain(PolicyEntity src) {
        Policy ret = new Policy();

        ret.setPolicyId(src.getPolicyId());
        ret.setName(src.getName());
        ret.setDescription(src.getDescription());
        ret.setSubject(src.getSubject());
        ret.setValue(src.getValue());

        return ret;
    }

}
