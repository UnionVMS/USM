package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PolicyServiceTest extends DeploymentFactory {

    private static final String POL_AUTHENTICATION = "Authentication";
    private static final String POL_PASSWORD = "Password";

    @EJB
    private PolicyService testSubject;

    @Test
    public void testGetSubjects() {
        // Setup
        ServiceRequest<NoBody> req = new ServiceRequest<>();
        req.setRequester("usm_admin");

        // Execute
        List<String> resp = testSubject.getSubjects(req);

        // Verify
        String expected = POL_AUTHENTICATION;
        assertNotNull("Unexpected null response", resp);
        assertNotNull("Expected Subject " + expected + " not found", getSubject(resp, expected));
    }

    @Test
    public void testUpdatePolicy() {
        // Setup
        ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
        findReq.setRequester("usm_admin");
        FindPoliciesQuery query = new FindPoliciesQuery();
        query.setName("password.minLength");
        query.setSubject(POL_PASSWORD);
        findReq.setBody(query);

        List<Policy> findRes = testSubject.findPolicies(findReq);
        assertNotNull("Unexpected null response", findRes);
        assertFalse("Unexpected empty response", findRes.isEmpty());
        assertEquals("Expected 1 result", 1, findRes.size());
        Policy toBeUpdatedPolicy = findRes.get(0);

        ServiceRequest<Policy> req = new ServiceRequest<>();
        req.setRequester("usm_admin");
        req.setPassword("password");
        toBeUpdatedPolicy.setValue("24");
        req.setBody(toBeUpdatedPolicy);

        Policy updateRes = testSubject.updatePolicy(req);
        assertNotNull("Unexpected null response", updateRes);
        assertEquals("password.minLength updated to 24", "24", updateRes.getValue());

        findRes = testSubject.findPolicies(findReq);
        assertNotNull("Unexpected null response", findRes);
        assertEquals("Expected 1 result", 1, findRes.size());
        Policy retrievedUpdatedPolicy = findRes.get(0);
        assertTrue("password.minLenght updated to 24",
                retrievedUpdatedPolicy.getName().equals("password.minLength")
                        && retrievedUpdatedPolicy.getValue().equals("24"));

        toBeUpdatedPolicy.setValue("8");
        req.setBody(toBeUpdatedPolicy);

        updateRes = testSubject.updatePolicy(req);
        assertNotNull("Unexpected null response", updateRes);
        assertEquals("password.minLength updated to 8", "8", updateRes.getValue());
    }

    private String getSubject(List<String> subjects, String expected) {
        for (String subject : subjects) {
            if (subject.equals(expected)) {
                return subject;
            }
        }
        return null;
    }
}
