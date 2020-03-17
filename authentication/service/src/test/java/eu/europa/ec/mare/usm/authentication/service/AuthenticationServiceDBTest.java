package eu.europa.ec.mare.usm.authentication.service;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.service.impl.AuthenticationDao;
import eu.europa.ec.mare.usm.authentication.service.impl.AuthenticationServiceBean;
import eu.europa.ec.mare.usm.authentication.service.impl.LDAP;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Unit-test for the AuthenticationService configured to authenticate
 * users against the USM database schema.
 */
@RunWith(Arquillian.class)
public class AuthenticationServiceDBTest extends AbstractAuthenticationServiceTest {
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;

    @EJB
    private PolicyProvider policyProvider;

    @EJB
    private JdbcTestFixture testFixture;

    @Before
    public void before() {
        Properties props = policyProvider.getProperties(POLICY_SUBJECT);
        props.setProperty("ldap.enabled", "false");
        policyProvider.setProperties(POLICY_SUBJECT, props);

        assertFalse("LDAP is enabled", isLDAPEnabled());

        testFixture.update("update user_t set" +
                " STATUS='E'," +
                " LAST_LOGON=null," +
                " LOGON_FAILURE=0, " +
                " LOCKOUT_REASON=null, " +
                " LOCKOUT_TO=null" +
                " where user_name=?", "lock_me_out");
        testFixture.update("update user_t set" +
                        " PASSWORD_EXPIRY=?" +
                        " where user_name='change_me'",
                new Date(System.currentTimeMillis() + ONE_DAY));
    }

    @After
    public void userMapNull() {
        assertTrue("Unexpected not null user map", !authenticateCalled || resp.getUserMap() == null);
    }

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class,
                "AuthenticationServiceDBTest.jar")
                .addPackage(AuthenticationService.class.getPackage())
                .addPackage(AuthenticationServiceBean.class.getPackage())
                .addPackage(AuthenticationDao.class.getPackage())
                .addPackage(LDAP.class.getPackage())
                .addPackage(RequestValidator.class.getPackage())
                .addPackage(ChallengeResponse.class.getPackage())
                .addPackage(SessionInfo.class.getPackage())
                .addPackage(PolicyProvider.class.getPackage())
                .addPackage(AuthenticationRequest.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return jar;
    }

    @Test
    public void testAuthenticateUserMustChangePassword() {
        // Execute
        authenticateUser("change_me", "password");

        // Verify
        assertNotNull("Unexpected null response", resp);
        assertTrue("Unexpected isAuthenticated value", resp.isAuthenticated());
        assertEquals("Unexpected StatusCode", AuthenticationResponse.MUST_CHANGE_PASSWORD, resp.getStatusCode());
    }

    @Test
    public void testAuthenticateUserPasswordExpired() {
        // Execute
        authenticateUser("expired", "password");

        // Verify
        assertNotNull("Unexpected null response", resp);
        assertTrue("Unexpected isAuthenticated value", resp.isAuthenticated());
        assertEquals("Unexpected StatusCode", AuthenticationResponse.PASSWORD_EXPIRED, resp.getStatusCode());
    }

    @Test
    public void testAuthenticateUserTriggerLockOut() {
        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("lock_me_out");
        request.setPassword("wrong password");

        // Execute
        for (int i = 0; i < 10; i++) {
            AuthenticationResponse response = getTestSubject().authenticateUser(request);

            // Verify
            assertNotNull("Unexpected null response", response);
            assertFalse("Unexpected isAuthenticated value", response.isAuthenticated());
        }

        // Verify
        authenticateUser("lock_me_out", "wrong password");

        assertNotNull("Unexpected null response", resp);
        assertFalse("Unexpected isAuthenticated value", resp.isAuthenticated());
        assertEquals("Unexpected StatusCode", AuthenticationResponse.ACCOUNT_LOCKED, resp.getStatusCode());
    }

    @Test
    public void testAuthenticateUserSuccessUserEnabled() {
        // Execute
        authenticateUser("anonymous", "password");

        // Verify
        assertNotNull("Unexpected null response", resp);
        assertTrue("Unexpected Authenticated response: " + resp.isAuthenticated(), resp.isAuthenticated());
        assertEquals("Unexpected StatusCode: " + resp.getStatusCode(), AuthenticationResponse.SUCCESS,
                resp.getStatusCode());
    }

    @Test
    public void testAuthenticateUserFailureUserDisabled() {
        // Execute
        authenticateUser("orphan", "password");

        // Verify
        assertNotNull("Unexpected null response", resp);
        assertFalse("Unexpected Authenticated response: " +
                resp.isAuthenticated(), resp.isAuthenticated());
        assertEquals("Unexpected StatusCode: " + resp.getStatusCode(), AuthenticationResponse.ACCOUNT_DISABLED,
                resp.getStatusCode());
    }

    @Test
    public void testAuthenticateUserFailureUserLocked() {
        // Execute
        authenticateUser("lockout", "password");

        // Verify
        assertNotNull("Unexpected null response", resp);
        assertFalse("Unexpected Authenticated response: " +
                resp.isAuthenticated(), resp.isAuthenticated());
        assertEquals("Unexpected StatusCode: " + resp.getStatusCode(),
                AuthenticationResponse.ACCOUNT_LOCKED, resp.getStatusCode());
    }

}
