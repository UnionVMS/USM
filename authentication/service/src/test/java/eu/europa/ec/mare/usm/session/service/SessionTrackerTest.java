package eu.europa.ec.mare.usm.session.service;

import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.session.service.impl.SessionDao;
import eu.europa.ec.mare.usm.session.service.impl.SessionTrackerBean;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Unit test for the (EJB) implementation of the SessionTracker.
 */
@RunWith(Arquillian.class)
public class SessionTrackerTest {
    private static final String POLICY_SUBJECT = "Account";
    @EJB
    private PolicyProvider policyProvider;

    @EJB
    private SessionTracker testSubject;

    private String userSite = null;

    @Before
    public void setUp() throws IOException {
        userSite = InetAddress.getLocalHost().getHostAddress();
        InputStream is = getClass().getResourceAsStream("/MaxSessionsEnabled.properties");
        Properties props = new Properties();
        props.load(is);
        policyProvider.setProperties(POLICY_SUBJECT, props);
    }

    @After
    public void tearDown() {
        policyProvider.reset();
    }

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class,
                "SessionTrackerTest.jar")
                .addPackage(SessionTracker.class.getPackage())
                .addPackage(SessionTrackerBean.class.getPackage())
                .addPackage(SessionDao.class.getPackage())
                .addPackage(ChallengeResponse.class.getPackage())
                .addPackage(SessionInfo.class.getPackage())
                .addPackage(RequestValidator.class.getPackage())
                .addPackage(PolicyProvider.class.getPackage())
                .addAsResource("MaxSessionsEnabled.properties")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        return jar;
    }

    @Test
    public void testStartSessionOneSite() {
        // Execute
        SessionInfo sessionId = createSessionInfo("testStartSession");

        String result = testSubject.startSession(sessionId);

        // Verify
        assertNotNull("Unexpected null result", result);
    }

    @Test
    public void testStartSessionWithExpiry()
            throws InterruptedException {
        // Set-up
        SessionInfo session1 = createSessionInfo("testStartSessionWithExpiry");
        String result1 = testSubject.startSession(session1);
        assertNotNull("Unexpected null result", result1);

        // Execute
        Thread.sleep(1000 + 1000);

        SessionInfo session2 = createSessionInfo("testStartSessionWithExpiry");
        String result2 = testSubject.startSession(session2);
        assertNotNull("Unexpected null result", result2);
    }

    @Test
    public void testStartSessionAnySite() {
        // Execute
        SessionInfo session1 = createSessionInfo("testStartSessionAnySite",
                "site1");
        String result1 = testSubject.startSession(session1);
        assertNotNull("Unexpected null result", result1);
        SessionInfo session2 = createSessionInfo("testStartSessionAnySite",
                "site2");
        String result2 = testSubject.startSession(session2);
        assertNotNull("Unexpected null result", result2);
    }

    @Test
    public void testStartSessionExceedOneSite() {
        // Set-up
        Properties props = policyProvider.getProperties(POLICY_SUBJECT);
        String maxSessionOneSite = props.getProperty("account.maxSessionOneSite");
        props.setProperty("account.maxSessionOneSite", "1");
        policyProvider.setProperties(POLICY_SUBJECT, props);

        SessionInfo session1 = createSessionInfo("testStartSessionExceedOneSite");
        String result = testSubject.startSession(session1);
        assertNotNull("Unexpected null result", result);

        // Execute
        SessionInfo session2 = createSessionInfo("testStartSessionExceedOneSite");
        String ret = testSubject.startSession(session2);
        assertNull(ret);

        props.setProperty("account.maxSessionOneSite", maxSessionOneSite);
        policyProvider.setProperties(POLICY_SUBJECT, props);
    }

    @Test
    public void testStartSessionExceedAnySite() {
        // Set-up
        Properties props = policyProvider.getProperties(POLICY_SUBJECT);
        String maxSessionsAnySite = props.getProperty("account.maxSessionAnySite");
        props.setProperty("account.maxSessionAnySite", "2");
        policyProvider.setProperties(POLICY_SUBJECT, props);

        SessionInfo session1 = createSessionInfo("testStartSessionExceedAnySite", "site1");
        String result1 = testSubject.startSession(session1);
        assertNotNull("Unexpected null result", result1);
        SessionInfo session2 = createSessionInfo("testStartSessionExceedAnySite", "site2");
        String result2 = testSubject.startSession(session2);
        assertNotNull("Unexpected null result", result2);

        // Execute
        SessionInfo session3 = createSessionInfo("testStartSessionExceedAnySite", "site3");

        String ret = testSubject.startSession(session3);
        assertNull(ret);

        //clean-up
        props.setProperty("account.maxSessionAnySite", maxSessionsAnySite);
        policyProvider.setProperties(POLICY_SUBJECT, props);
    }

    @Test
    public void testGetSession() {
        // Set-up
        SessionInfo sessionInfo = createSessionInfo("testGetSession");
        String uniqueId = testSubject.startSession(sessionInfo);

        // Execute
        SessionInfo result = testSubject.getSession(uniqueId);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected userName value", sessionInfo.getUserName(), result.getUserName());
        assertEquals("Unexpected userSite value", sessionInfo.getUserSite(), result.getUserSite());
    }

    @Test
    public void testEndSession() {
        // Set-up
        SessionInfo sessionInfo = createSessionInfo("testEndSession");
        String uniqueId = testSubject.startSession(sessionInfo);

        // Execute
        testSubject.endSession(uniqueId);
    }

    private SessionInfo createSessionInfo(String userName) {
        return createSessionInfo(userName, userSite);
    }

    private SessionInfo createSessionInfo(String userName, String userSite) {
        SessionInfo ret = new SessionInfo();
        ret.setUserName(userName);
        ret.setUserSite(userSite);

        return ret;
    }

}
