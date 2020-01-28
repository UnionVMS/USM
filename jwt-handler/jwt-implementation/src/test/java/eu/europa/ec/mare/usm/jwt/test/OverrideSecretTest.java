package eu.europa.ec.mare.usm.jwt.test;

import eu.europa.ec.mare.usm.jwt.DefaultJwtTokenHandler;
import eu.europa.ec.mare.usm.jwt.JndiUtil;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Unit-test for the ApplicationService
 */
@RunWith(Arquillian.class)
public class OverrideSecretTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverrideSecretTest.class);
    private static final String USER_NAME = "usm_user";
    private static final String RANDOM_SIG_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ1c20vYXV0aGVudGljYXRpb24iLCJpc3MiOiJ1c20iLCJzdWIiOiJhdXRoZW50aWNhdGlvbiIsImlhdCI6MTQ2MTA3NzUxMSwiZXhwIjoxNDYxMDc5MzExLCJ1c2VyTmFtZSI6InVzbV91c2VyIn0.QIn18uc09ajddT6ydLqMPO-P3IdmEa9L8e4s8Zck_YQ";
    private static final String USM_SECRET_SIG_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ1c20vYXV0aGVudGljYXRpb24iLCJpc3MiOiJ1c20iLCJzdWIiOiJhdXRoZW50aWNhdGlvbiIsImlhdCI6MTU1NTMyMzA2NCwiZXhwIjo0NzA4OTIzMDY0LCJ1c2VyTmFtZSI6InNvbWVmYWtldXNlciJ9.fCek4KaVf8vYmBicyLANUnQ6Ruqk20_Wa80efwi4dQc";

	@EJB
	private JwtTokenHandler testSubject;

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "ArquillianTest.jar")
                .addAsResource("jwtsecret.properties", "jwt.properties")
                .addClass(JwtTokenHandler.class)
                .addClass(DefaultJwtTokenHandler.class)
                .addClass(JndiUtil.class)
                .addPackages(true, "io.jsonwebtoken")
                .addPackages(true, "com.fasterxml.jackson")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return jar;
    }

    /**
     * Creates a new instance
     */
    public OverrideSecretTest() {
    }

    /**
     * Tests the getApplicationNames method
     */
    @Test
    public void testCreateToken() {
        String token = testSubject.createToken(USER_NAME);

        assertNotNull("Unexpected null response", token);
    }

    @Test
    public void testParseToken() {
        String token = testSubject.createToken(USER_NAME);
        String parsed = testSubject.parseToken(token);

        assertEquals(USER_NAME, parsed);
    }

    @Test
    public void testParseUSMToken() {
        String parsed = testSubject.parseToken(USM_SECRET_SIG_TOKEN);
        assertEquals("somefakeuser", parsed);
    }

    @Test
    public void testTamperedParseToken() {
        String parsed = testSubject.parseToken(RANDOM_SIG_TOKEN);

        assertThat(parsed, is(not(USER_NAME)));
        assertNull(parsed);
    }

    @Test
    public void testOverrideSecretToken() {
        String parsed = testSubject.parseToken(RANDOM_SIG_TOKEN);

        assertThat(parsed, is(not(USER_NAME)));
		assertNull(parsed);
    }
}
