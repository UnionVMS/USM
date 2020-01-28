package eu.europa.ec.mare.usm.jwt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.mare.usm.jwt.DefaultJwtTokenHandler;
import eu.europa.ec.mare.usm.jwt.JndiUtil;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;

/**
 * Unit-test for the ApplicationService
 */
@RunWith(Arquillian.class)
public class JwtTokenHandlerTest {
    private static final String USER_NAME = "usm_user";
    private static final String RANDOM_SIG_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ1c20vYXV0aGVudGljYXRpb24iLCJpc3MiOiJ1c20iLCJzdWIiOiJhdXRoZW50aWNhdGlvbiIsImlhdCI6MTQ2MTA3NzUxMSwiZXhwIjoxNDYxMDc5MzExLCJ1c2VyTmFtZSI6InVzbV91c2VyIn0.QIn18uc09ajddT6ydLqMPO-P3IdmEa9L8e4s8Zck_YQ";

    private DefaultJwtTokenHandler testSubject;

    @Deployment(name = "withProperties", order = 2)
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "ArquillianTest.war")
                .addAsResource("jwt.properties")
                .addClass(JwtTokenHandler.class)
                .addClass(JndiUtil.class)
                .addClass(DefaultJwtTokenHandler.class)
                .addPackages(true, "io.jsonwebtoken")
                .addPackages(true, "com.fasterxml.jackson")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }
    
    @Deployment(name = "withoutProperties", order = 1)
    public static WebArchive createDeploymentWithoutProperties() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "ArquillianTestWithoutProperties.war")
                .addClass(JwtTokenHandler.class)
                .addClass(JndiUtil.class)
                .addClass(DefaultJwtTokenHandler.class)
                .addPackages(true, "io.jsonwebtoken")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Before
    public void clearProperties() throws NamingException {
        InitialContext ic = new InitialContext();
        ic.unbind("USM/secretKey");
        System.clearProperty("USM.secretKey");
        testSubject = new DefaultJwtTokenHandler();
        testSubject.init();
    }

    /**
     * Tests the getApplicationNames method
     */
    @Test
    @OperateOnDeployment("withProperties")
    public void testCreateToken() {
        String token = testSubject.createToken(USER_NAME);
        assertNotNull("Unexpected null response", token);
    }

    @Test
    @OperateOnDeployment("withProperties")
    public void testParseToken() {
        String token = testSubject.createToken(USER_NAME);
        String parsed = testSubject.parseToken(token);
        assertEquals(USER_NAME, parsed);
    }

    @Test
    @OperateOnDeployment("withProperties")
    public void testtamperedParseToken() {
        String parsed = testSubject.parseToken(RANDOM_SIG_TOKEN);
        assertThat(parsed, is(not(USER_NAME)));
        assertNull(parsed);

    }

    @Test
    @OperateOnDeployment("withoutProperties")
    public void testGenerateKeyAndJndiBind() {
        String user = "Test";
        String token = testSubject.createToken(user);

        DefaultJwtTokenHandler jwtHandler = new DefaultJwtTokenHandler();
        jwtHandler.init();

        String parsedUsername = jwtHandler.parseToken(token);
        assertThat(parsedUsername, CoreMatchers.is(user));
    }

    @Test
    @OperateOnDeployment("withoutProperties")
    public void testParseKeyWithJndiMismatch() throws NamingException {
        String user = "Test";
        String token = testSubject.createToken(user);

        InitialContext ic = new InitialContext();
        ic.rebind("USM/secretKey", "apa");

        DefaultJwtTokenHandler jwtHandler = new DefaultJwtTokenHandler();
        jwtHandler.init();

        String parsedUsername = jwtHandler.parseToken(token);
        assertThat(parsedUsername, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    @OperateOnDeployment("withProperties")
    public void testParseFeatures() {
        List<Integer> features = Arrays.asList(1,2);
        String username = "Test user";
        String token = testSubject.createToken(username, features);

        assertThat(testSubject.parseToken(token), CoreMatchers.is(username));

        List<Integer> parsedFeatures = testSubject.parseTokenFeatures(token);
        assertThat(parsedFeatures.size(), CoreMatchers.is(features.size()));
    }
}
