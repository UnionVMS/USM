package eu.europa.ec.mare.usm.administration.service.ldap.impl;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.LdapUser;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.ldap.LdapUserInfoService;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

/**
 * Stateless session bean implementation of the LdapUserInfoService.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LdapUserInfoServiceBean implements LdapUserInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserInfoServiceBean.class);
    private static final String POLICY_SUBJECT = "Administration";

    @EJB
    private DefinitionService policy;

    @Inject
    private LDAPValidator validator;

    public LdapUserInfoServiceBean() {
    }

    @Override
    public boolean isEnabled() {
        LOGGER.debug("isEnabled() - (ENTER)");

        Properties props = getProperties();

        boolean ret = Boolean.parseBoolean(props.getProperty("ldap.enabled", "false"));

        LOGGER.debug("isEnabled() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LdapUser getLdapUserInfo(ServiceRequest<GetUserQuery> request) {
        LOGGER.debug("getLdapUserInfo(" + request + ") - (ENTER)");

        validator.assertValid(request);

        LdapUser ret = null;
        if (isEnabled()) {
            LDAP ldap = new LDAP(getProperties());
            String userName = request.getBody().getUserName();
            Map<String, String> userMap = ldap.getUserDetails(userName);

            if (userMap != null && !userMap.isEmpty()) {
                ret = new LdapUser();
                ret.setUserName(request.getBody().getUserName());
                ret.setFirstName(userMap.get("firstName"));
                ret.setLastName(userMap.get("lastName"));
                ret.setPhoneNumber(userMap.get("telephoneNumber"));
                ret.setMobileNumber(userMap.get("mobileNumber"));
                ret.setFaxNumber(userMap.get("faxNumber"));
                ret.setEmail(userMap.get("email"));
            }
        }

        LOGGER.debug("getLdapUserInfo() - (LEAVE): " + ret);
        return ret;
    }

    private Properties getProperties() {
        Properties ret = null;

        PolicyDefinition pf = policy.getDefinition(POLICY_SUBJECT);
        if (pf != null) {
            ret = pf.getProperties();
        } else {
            ret = new Properties();
        }
        return ret;
    }
}
