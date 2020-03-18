package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class DefinitionServiceTest extends DeploymentFactory {
    private static final String PASSWORD_SUBJECT = "Password";
    private static final String MIN_LENGTH_PROPERTY = "password.minLnegth";
    private static final String MIN_SPECIAL_PROPERTY = "password.minSpecial";
    private static final String MIN_DIGITS_PROPERTY = "password.minDigits";

    @EJB
    private DefinitionService testSubject;

    @Test
    public void testGetDefinition() {
        String subject = PASSWORD_SUBJECT;

        // Execute
        PolicyDefinition result = testSubject.getDefinition(subject);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected subject", subject, result.getSubject());
        assertNotNull("Unexpected null properties", result.getProperties());
    }

    @Test
    public void testSetDefinition() {
        // Set-up
        String subject = PASSWORD_SUBJECT;
        PolicyDefinition setup = testSubject.getDefinition(subject);

        // Execute
        ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
        request.setRequester("usm_admin");
        request.setBody(setup);
        request.getBody().getProperties().put(MIN_DIGITS_PROPERTY, "2");
        request.getBody().getProperties().put(MIN_SPECIAL_PROPERTY, "1");
        testSubject.setDefinition(request);

        // Verify
        PolicyDefinition result = testSubject.getDefinition(subject);
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected subject", subject, result.getSubject());
        assertNotNull("Unexpected null properties", result.getProperties());

        assertEquals("Unexpected property value",
                request.getBody().getProperties().getProperty(MIN_DIGITS_PROPERTY),
                result.getProperties().getProperty(MIN_DIGITS_PROPERTY));
        assertEquals("Unexpected property value",
                request.getBody().getProperties().getProperty(MIN_SPECIAL_PROPERTY),
                result.getProperties().getProperty(MIN_SPECIAL_PROPERTY));
        assertEquals("Unexpected property value",
                request.getBody().getProperties().getProperty(MIN_LENGTH_PROPERTY),
                result.getProperties().getProperty(MIN_LENGTH_PROPERTY));
    }
}
