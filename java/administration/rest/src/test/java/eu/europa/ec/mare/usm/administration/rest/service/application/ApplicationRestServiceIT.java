package eu.europa.ec.mare.usm.administration.rest.service.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.ApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

public class ApplicationRestServiceIT extends AuthWrapper {
	private static final String APPLICATION_USM = "USM";
	private static final String APPLICATION_QUOTA = "Quota";
	private static final String APPLICATION_UVMS = "Union-VMS";
  private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
	private ApplicationRestClient client = null;
	private final String vms_admin_com;

	/**
	 * Creates a new instance.
	 * 
	 * @throws IOException
	 *             in case the '/test.properties' class-path resource cannot be
	 *             loaded
	 */
	public ApplicationRestServiceIT() throws IOException {
	  super(VMS_ADMIN_COM_USER);
		vms_admin_com = getAuthToken();
	}
	
	

	@Before
	public void setUp() {
		client = new ApplicationRestClient(endPoint);
	}

    /**
     * Tests the findApplications operation.
     */
    @Test
    public void testFindApplications() {
        // Execute
        ServiceRequest<FindApplicationQuery> request = new ServiceRequest<>();
        request.setRequester(vms_admin_com);
        FindApplicationQuery query = new FindApplicationQuery();
        query.setName(APPLICATION_USM);
        query.setPaginator(getDefaultPaginator());
        request.setBody(query);
        ClientResponse response = client.findApplications(ClientResponse.class, request);
        GenericType<PaginationResponse<Application>> gType = new GenericType<PaginationResponse<Application>>() {
        };
        PaginationResponse<Application> sar = response.getEntity(gType);
        // Verify
        assertNotNull("Unexpected null result", response);
        List<Application> applications = sar.getResults();
        assertNotNull("Expected Scope " + APPLICATION_USM + " not found", getApplication(applications, APPLICATION_USM));
    }

	/**
	 * Tests the getApplicationNames operation.
	 */
	@Test
	public void testApplicationNames() {
		// Execute
		ServiceRequest<ApplicationQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		request.setBody(new ApplicationQuery());

		ClientResponse response = client.getApplicationNames(ClientResponse.class, request);
		GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
		};
		ServiceArrayResponse<String> sar = response.getEntity(gType);

		// Verify
		assertNotNull("Unexpected null result", response);
		List<String> appNames = sar.getResults();
		assertEquals("Unexpected 'orgName' value", APPLICATION_USM,
				getAppName(appNames, APPLICATION_USM));
		assertEquals("Unexpected 'orgName' value", APPLICATION_QUOTA,
				getAppName(appNames, APPLICATION_QUOTA));
		assertEquals("Unexpected 'orgName' value", APPLICATION_UVMS,
				getAppName(appNames, APPLICATION_UVMS));
	}

	private String getAppName(List<String> aNames, String expected) {
		for (String name : aNames) {
			if (name.equals(expected)) {
				return expected;
			}
		}

		return null;
	}
	
	
	@Test
	public void testGetApplicationFeatures(){
		
		ServiceRequest<String> request=new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		request.setBody("USM");
		
		ClientResponse response = client.getApplicationFeatures(ClientResponse.class, request);
		GenericType<ServiceArrayResponse<Feature>> gType = new GenericType<ServiceArrayResponse<Feature>>() {
		};
		ServiceArrayResponse<Feature> sar = response.getEntity(gType);

		
		assertNotNull("Unexpected null result", response);
		List<Feature> featureNames = sar.getResults();
		
		assertFalse("List of application features is empty", featureNames.isEmpty());
		
	}

    private Paginator getDefaultPaginator(){
        Paginator paginator = new Paginator();
        paginator.setLimit(8);
        paginator.setOffset(0);
        paginator.setSortColumn("name");
        paginator.setSortDirection("DESC");
        return paginator;
    }

    private String getApplication(List<Application> applications, String expected) {
        for (Application application : applications) {
            if (application.getName().equals(expected)) {
                return application.getName();
            }
        }

        return null;
    }
}
