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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Unit-test for the AuthenticationService configured to authenticate
 * users against and LDAP server.
 */
@RunWith(Arquillian.class)
public class AuthenticationServiceLDAPTest extends AbstractAuthenticationServiceTest {

    private static final String PASS = "password";
    private static final String PASS_INCORRECT = "password_incorect";
    private static final String SHOULD_NOT_SUFFIX = "-should-not-be";
    private static final String FIRST_NAME_SHOULD_NOT = "this-first-name" + SHOULD_NOT_SUFFIX;
    private static final String LAST_NAME_SHOULD_NOT = "this-last-name" + SHOULD_NOT_SUFFIX;

    @EJB
    private JdbcTestFixture testFixture;

    @EJB
    private PolicyProvider policyProvider;

    @Before
    public void before() throws IOException {
        InputStream is = getClass().getResourceAsStream("/ApacheDS.properties");
        Properties props = new Properties();
        props.load(is);
        policyProvider.setProperties(POLICY_SUBJECT, props);

        assertTrue("LDAP is not enabled", isLDAPEnabled());
    }

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class,
                "AuthenticationServiceLDAPTest.jar")
                .addPackage(AuthenticationService.class.getPackage())
                .addPackage(AuthenticationServiceBean.class.getPackage())
                .addPackage(AuthenticationDao.class.getPackage())
                .addPackage(LDAP.class.getPackage())
                .addPackage(RequestValidator.class.getPackage())
                .addPackage(PolicyProvider.class.getPackage())
                .addPackage(ChallengeResponse.class.getPackage())
                .addPackage(SessionInfo.class.getPackage())
                .addPackage(AuthenticationRequest.class.getPackage())
                .addAsResource("ApacheDS.properties")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        return jar;
    }

    /**
     * Invokes the authenticateUser method for user enabled and correct password so that have success and
     * create person in DB, the first time, or sync person in DB the second time when person is already create
     * by data in LDAP.
     */
    @Test
    public void testAuthenticateUserSuccessUserEnabledBothCreateSyncSenario() {
        // No person DB at all at first
        String[] params = {"ldap", "Enabled" };

        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("ldap_enabled");
        request.setPassword(PASS);

        // Execute 1st time no person at all in DB
        authenticateUserSuccessUserEnabled(params, request);

        // Set-up different person attributes from the one in LDAP in previous step persisted
        setUpPerson(params);

        // Execute 2nd time sync person in DB with LDAP
        authenticateUserSuccessUserEnabled(params, request);

        // Clear state in DB created by our scenario
        reInitUserPersonDB(request.getUserName(), params);
    }

    /**
     * Invokes the authenticateUser method for user enabled but with incorrect password so that have no success
     * and sync with LDAP.
     */
    @Test
    public void testAuthenticateUserFailUserEnabled() {
        // Set-up
        String[] params = {"ldap", "Enabled" };

        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("ldap_enabled");
        request.setPassword(PASS);

        // 1st step authenticate with correct password so that person created in DB with LDAP
        authenticateUserSuccessUserEnabled(params, request);

        // Set-up different person attributes from the one in LDAP
        setUpPerson(params);

        // 2nd step try to login with incorrect pass so that attempt to fail
        request.setPassword(PASS_INCORRECT);

        // Execute
        AuthenticationResponse response = getTestSubject().authenticateUser(request);

        // Verify user authenticated without success
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response: " +
                response.isAuthenticated(), response.isAuthenticated());
        assertEquals("Unexpected StatusCode response: " + response.getStatusCode(),
                AuthenticationResponse.INVALID_CREDENTIALS, response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", response.getUserMap());

        // Verify user's person's in DB are not in sync with LDAP
        params[0] = FIRST_NAME_SHOULD_NOT;
        params[1] = LAST_NAME_SHOULD_NOT;
        verifyPerson(params, response);

        // Clear state in DB created by our scenario
        reInitUserPersonDB(request.getUserName(), params);

        // 3nd step try to login with incorrect pass and with no person in DB
        response = getTestSubject().authenticateUser(request);

        // Verify user authenticated without success
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response: " +
                response.isAuthenticated(), response.isAuthenticated());
        assertEquals("Unexpected StatusCode response: " + response.getStatusCode(),
                AuthenticationResponse.INVALID_CREDENTIALS, response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected LDAP attributes is response", response.getUserMap());

        // Verify no person in DB
        verifyNoPerson(request.getUserName());
    }

    /**
     * Invokes the authenticateUser method.
     */
    @Test
    public void testAuthenticateUserFailureUserDisabled() {
        // Set-up

        testFixture.update("update user_t set" +
                " STATUS='E'," +
                " LAST_LOGON=null," +
                " LOGON_FAILURE=0, " +
                " LOCKOUT_REASON=null, " +
                " LOCKOUT_TO=null" +
                " where user_name=?", "lock_me_out");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("ldap_disabled");
        request.setPassword(PASS);

        // Execute
        AuthenticationResponse response = getTestSubject().authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response: " +
                response.isAuthenticated(), response.isAuthenticated());
        assertEquals("Unexpected StatusCode response: " + response.getStatusCode(),
                AuthenticationResponse.ACCOUNT_DISABLED,
                response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertTrue("Unexpected not empty user map", response.getUserMap() == null);
    }

    /**
     * Invokes the authenticateUser method.
     */
    @Test
    public void testAuthenticateUserFailureUserLocked() {
        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName("ldap_locked");
        request.setPassword(PASS);

        // Execute
        AuthenticationResponse response = getTestSubject().authenticateUser(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertFalse("Unexpected Authenticated response: " +
                response.isAuthenticated(), response.isAuthenticated());
        assertEquals("Unexpected StatusCode response: " + response.getStatusCode(),
                AuthenticationResponse.ACCOUNT_LOCKED,
                response.getStatusCode());

        // Verify user's LDAP attributes not present in response
        assertNull("Unexpected not empty user map", response.getUserMap());
    }

    private void setUpPerson(String[] params) {
        testFixture.update("update person_t set phone_number='this-phone-number-should-not-be' " +
                " where first_name=? and last_name=?", params);
        testFixture.update("update person_t set mobile_number='this-mobile-number-should-not-be' " +
                " where first_name=? and last_name=?", params);
        testFixture.update("update person_t set fax_number='this-fax-number-should-not-be' " +
                " where first_name=? and last_name=?", params);
        testFixture.update("update person_t set e_mail='this-email-should-not-be@ldap-sync.eu' " +
                " where first_name=? and last_name=?", params);
        testFixture.update("update person_t set first_name='" + FIRST_NAME_SHOULD_NOT + "' " +
                " where first_name=? and last_name=?", params);
        params[0] = FIRST_NAME_SHOULD_NOT;
        testFixture.update("update person_t set last_name='" + LAST_NAME_SHOULD_NOT + "' " +
                " where first_name=? and last_name=?", params);
        params[1] = LAST_NAME_SHOULD_NOT;
    }

    private void verifyLDAPAttributes(AuthenticationResponse r) {
        assertNotNull("Unexpected empty user map", r.getUserMap());
        assertNotNull("Unexpected empty user sn", r.getUserMap().get("sn"));
        assertNotNull("Unexpected empty user givenName", r.getUserMap().get("givenName"));
        assertNotNull("Unexpected empty user mail", r.getUserMap().get("mail"));
        assertEquals("Unexpected user givenName", r.getUserMap().get("givenName"), "ldap");
        assertEquals("Unexpected user mail", r.getUserMap().get("mail"), "LdapEnabled@mail.org");
    }

    private void verifyPerson(String[] p, AuthenticationResponse r) {
        String email = testFixture.select("select e_mail from person_t where first_name=? and last_name=?", p);
        String firstName = testFixture.select("select first_name from person_t where first_name=? and last_name=?", p);
        String lastName = testFixture.select("select last_name from person_t where first_name=? and last_name=?", p);
        String phone = testFixture.select("select phone_number from person_t where first_name=? and last_name=?", p);
        String mobile = testFixture.select("select mobile_number from person_t where first_name=? and last_name=?", p);
        String fax = testFixture.select("select fax_number from person_t where first_name=? and last_name=?", p);
        if (r.getUserMap() != null) {
            assertEquals("Unexpected db person user mail", email, r.getUserMap().get("mail"));
            assertEquals("Unexpected db person first name", firstName, r.getUserMap().get("givenName"));
            assertEquals("Unexpected db person last name", lastName, r.getUserMap().get("sn"));
            assertEquals("Unexpected db person phone", phone, r.getUserMap().get("telephoneNumber"));
            assertEquals("Unexpected db person mobile", mobile, r.getUserMap().get("mobile"));
            assertEquals("Unexpected db person fax", fax, r.getUserMap().get("facsimileTelephoneNumber"));
        } else {
            assertTrue("Unexpected db person user mail", email.contains(SHOULD_NOT_SUFFIX));
            assertTrue("Unexpected db person first name", firstName.contains(SHOULD_NOT_SUFFIX));
            assertTrue("Unexpected db person last name", lastName.contains(SHOULD_NOT_SUFFIX));
            assertTrue("Unexpected db person phone", phone.contains(SHOULD_NOT_SUFFIX));
            assertTrue("Unexpected db person mobile", mobile.contains(SHOULD_NOT_SUFFIX));
            assertTrue("Unexpected db person fax", fax.contains(SHOULD_NOT_SUFFIX));
        }
    }

    private void verifyNoPerson(String userName) {
        Integer personId = testFixture.selectID("select person_id from user_t where user_name = ? ", userName);
        assertEquals("Unexpected person assignment to user", 0, (int) personId);
    }

    private void reInitUserPersonDB(String userName, String[] p) {
        testFixture.update("update user_t set person_id = null where user_name = ?", userName);
        testFixture.delete("delete from person_t where first_name = ? and last_name = ?", p);
    }

    private void authenticateUserSuccessUserEnabled(String[] params, AuthenticationRequest request) {
        // Execute
        AuthenticationResponse response = getTestSubject().authenticateUser(request);

        // Verify user authenticated with success
        assertNotNull("Unexpected null response", response);
        assertTrue("Unexpected Authenticated response: " +
                response.isAuthenticated(), response.isAuthenticated());
        assertEquals("Unexpected StatusCode response: " + response.getStatusCode(),
                AuthenticationResponse.SUCCESS, response.getStatusCode());

        // Verify user's LDAP attributes present in response
        verifyLDAPAttributes(response);

        // Verify user's person's attributes in DB are in sync with LDAP
        params[0] = "ldap";
        params[1] = "Enabled";
        verifyPerson(params, response);
    }
}
