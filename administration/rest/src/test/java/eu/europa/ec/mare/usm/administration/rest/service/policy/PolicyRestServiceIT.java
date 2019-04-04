package eu.europa.ec.mare.usm.administration.rest.service.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

public class PolicyRestServiceIT extends AuthWrapper {
	private static final String POL_PASSWORD = "Password";
	private static final String POL_ACCOUNT = "Account";
	private static final String POL_FEATURE = "Feature";
	private static final String POL_ADMINISTRATION = "Administration";
	private static final String POL_AUTHENTICATION = "Authentication";
  private static final String USM_ADMIN = "usm_admin";
	private static final String URL = "http://localhost:8080/usm-administration/rest/";
	private PolicyRestClient client = null;
	private final String usm_admin;
	
	public PolicyRestServiceIT() throws IOException {

    super(USM_ADMIN);
    usm_admin = getAuthToken();
	}
	
	@Before
	public void setUp() {
		client = new PolicyRestClient(endPoint);
	}	
	
	/**
	 * Tests the updatePolicy operation
	 */
	@Test
	public void testUpdatePolicy() {
		ServiceRequest<Policy> req = new ServiceRequest<>();
		req.setRequester(usm_admin);
		
		ServiceRequest<FindPoliciesQuery> findReq = new ServiceRequest<>();
		findReq.setRequester(usm_admin);
		FindPoliciesQuery q = new FindPoliciesQuery();
		q.setName("password.minLength");
		q.setSubject(POL_PASSWORD);
		findReq.setBody(q);
		
		ClientResponse findResp = client.findPolicies(ClientResponse.class, findReq); 
		GenericType<List<Policy>> gtype = new GenericType<List<Policy>>(){};
		List<Policy> policiesFound = findResp.getEntity(gtype);
		assertNotNull("Unexpected null response", policiesFound);
		assertTrue("Expected 1 result", policiesFound.size() == 1);
		assertTrue("password.minLength updated to 24", policiesFound.get(0).getValue().equals("8"));
		
		Policy uPolicy = policiesFound.get(0);
		uPolicy.setValue("24");
		req.setBody(uPolicy);
		
		ClientResponse resp = client.updatePolicy(ClientResponse.class, req);
		assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), resp.getStatus());
		
		Policy d = resp.getEntity(Policy.class);
		assertTrue("password.minLength updated to 24", d.getValue().equals("24"));

		findResp = client.findPolicies(ClientResponse.class, findReq); 
		gtype = new GenericType<List<Policy>>(){};
		policiesFound = findResp.getEntity(gtype);
		assertNotNull("Unexpected null response", policiesFound);
		assertTrue("Expected 1 result", policiesFound.size() == 1);
		assertTrue("password.minLength updated to 24", policiesFound.get(0).getValue().equals("24"));
		
		uPolicy = policiesFound.get(0);
		uPolicy.setValue("8");
		req.setBody(uPolicy);
		
		resp = client.updatePolicy(ClientResponse.class, req);
		assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), resp.getStatus());
		
		d = resp.getEntity(Policy.class);
		assertTrue("password.minLength updated to 8", d.getValue().equals("8"));

	}
	
	/**
	 * Tests the getSubjects operation.
	 */
	@Test
	public void testSubjects() {
		// Execute
		ServiceRequest<NoBody> request = new ServiceRequest<>();
		request.setRequester(usm_admin);

		ClientResponse response = client.getSubjects(ClientResponse.class, request);
		GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
		};
		ServiceArrayResponse<String> sar = response.getEntity(gType);

		// Verify
		assertNotNull("Unexpected null result", sar);
		List<String> subjects = sar.getResults();
		assertNotNull("Expected Subject " + POL_PASSWORD + " not found",
				getSubject(subjects, POL_PASSWORD));
		assertNotNull("Expected Subject " + POL_ACCOUNT + "not found",
				getSubject(subjects, POL_ACCOUNT));
		assertNotNull("Expected Subject " + POL_FEATURE + "not found",
				getSubject(subjects, POL_FEATURE));
		assertNotNull("Expected Subject " + POL_ADMINISTRATION + "not found",
				getSubject(subjects, POL_ADMINISTRATION));
		assertNotNull("Expected Subject " + POL_AUTHENTICATION + "not found",
				getSubject(subjects, POL_AUTHENTICATION));
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
