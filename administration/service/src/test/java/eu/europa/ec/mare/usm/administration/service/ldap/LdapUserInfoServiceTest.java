package eu.europa.ec.mare.usm.administration.service.ldap;

import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.LdapUser;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class LdapUserInfoServiceTest extends DeploymentFactory {
    private static final String POLICY_SUBJECT = "Administration";
    private static final String USM_ADMIN = "usm_admin";

    @EJB
    private LdapUserInfoService testSubject;

    @EJB
    private DefinitionService policyService;

    private PolicyDefinition savedDefinition;

    @Before
    public void setUp() throws IOException {
        savedDefinition = policyService.getDefinition(POLICY_SUBJECT);

        InputStream is = getClass().getResourceAsStream("/ApacheDS.properties");
        Properties props = new Properties();
        props.load(is);

        ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
        request.setRequester(USM_ADMIN);
        request.setBody(new PolicyDefinition());
        request.getBody().setSubject(POLICY_SUBJECT);
        request.getBody().setProperties(props);
        policyService.setDefinition(request);

        assertTrue("LDAP is not enabled", testSubject.isEnabled());
    }

    @After
    public void tearDown() {
        ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
        request.setRequester(USM_ADMIN);
        request.setBody(savedDefinition);

        policyService.setDefinition(request);
    }

    @Test
    public void testIsEnabled() {
        boolean result = testSubject.isEnabled();
        assertTrue("LDAP is not enabled", result);
    }

    @Test
    @Ignore
    public void testGetLdapUserInfo() {
        ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
        request.setRequester(USM_ADMIN);
        request.setBody(new GetUserQuery());
        request.getBody().setUserName("ldap_enabled");

        LdapUser result = testSubject.getLdapUserInfo(request);
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected userName", request.getBody().getUserName(),
                result.getUserName());
        assertEquals("Unexpected e-mail", "LdapEnabled@mail.org", result.getEmail());
        assertEquals("Unexpected last name", "Enabled", result.getLastName());
        assertEquals("Unexpected first name", "ldap", result.getFirstName());
    }

    @Test
    public void testGetLdapUserInfoNotFound() {
        ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
        request.setRequester(USM_ADMIN);
        request.setBody(new GetUserQuery());
        request.getBody().setUserName("a_user_that_does_not_exist");

        LdapUser result = testSubject.getLdapUserInfo(request);
        assertNull("Unexpected non-null result", result);
    }
}
