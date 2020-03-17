package eu.europa.ec.mare.usm.administration.service.user;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ViewUsersServiceTest extends DeploymentFactory {

	@EJB
    private ViewUsersService testSubject;

    /**
     * Tests the find users method providing user name and nation as
     * search parameters
     */
    @Test
    public void testProvidingNameAndNationFindUsers() {
        // Setup
        Paginator paginator = new Paginator();
        paginator.setLimit(3);
        paginator.setOffset(0);
        paginator.setSortColumn("user_name");
        paginator.setSortDirection(" desc");
        FindUsersQuery query = new FindUsersQuery();
        query.setName("vms_user_com");
        query.setNation("EEC");
        query.setPaginator(paginator);

        ServiceRequest<FindUsersQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(query);

        String nameExpected = "vms_user_com";
        String nationExpected = "EEC";

        // Execute
        PaginationResponse<UserAccount> response = testSubject.findUsers(request);
        List<UserAccount> results = response.getResults();

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'name' value", nameExpected, getUserName(results, nameExpected));
        assertEquals("Unexpected 'nation' value", nationExpected, getNation(results, nationExpected));
    }

    /**
     * Tests the find users method providing first name and organization as
     * search parameters
     */
    @Test
    public void testProvidingNameOrgFindUsers() {
        // Setup
        Paginator paginator = new Paginator();
        paginator.setLimit(100);
        paginator.setOffset(0);
        paginator.setSortColumn("user_name");
        paginator.setSortDirection("ASC");
        FindUsersQuery query = new FindUsersQuery();
        query.setName("vms_admin_fra");
        query.setOrganisation("FRA");
        query.setPaginator(paginator);

        ServiceRequest<FindUsersQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(query);

        String expectedFirstName = "vms_admin_fra";
        String expectedOrganisation = "FRA";

        // Execute
        PaginationResponse<UserAccount> response = testSubject.findUsers(request);
        List<UserAccount> results = response.getResults();

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'name' value", expectedFirstName, getFirstName(results, expectedFirstName));
        assertEquals("Unexpected 'organisation' value",
				expectedOrganisation, getOrganisation(results, expectedOrganisation));
    }

    private String getUserName(List<UserAccount> cUsers, String expected) {
    	return cUsers.stream()
				.filter(u -> u.getUserName().equals(expected))
				.findAny()
				.map(UserAccount::getUserName)
				.orElse(null);
    }

    private String getFirstName(List<UserAccount> cUsers, String expected) {
		return cUsers.stream()
				.filter(u -> u.getPerson().getFirstName().equals(expected))
				.findAny()
				.map(u -> u.getPerson().getFirstName())
				.orElse(null);
    }

    private String getNation(List<UserAccount> cUsers, String expected) {
		return cUsers.stream()
				.filter(u -> u.getOrganisation().getNation().equals(expected))
				.findAny()
				.map(u -> u.getOrganisation().getNation())
				.orElse(null);
    }

    private String getOrganisation(List<UserAccount> cUsers, String expected) {
		return cUsers.stream()
				.filter(u -> u.getOrganisation().getName().equals(expected))
				.findAny()
				.map(u -> u.getOrganisation().getName())
				.orElse(null);
    }

    @Test
    public void testAuthorisedGetUser() {
        // Setup
        GetUserQuery query = new GetUserQuery();
        query.setUserName("vms_user_com");

        ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(query);

        String expected = "vms_user_com";

        // Execute
        UserAccount response = testSubject.getUser(request);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'UserName' value", expected, response.getUserName());
    }

    @Test
    public void testGetUsersNames() {
        // Setup
        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester("usm_user");
        request.setBody("");

        // Execute
        List<String> response = testSubject.getUsersNames(request);

        // Verify
        String expected = "usm_user";
        assertNotNull("Unexpected null result", response);
        assertFalse("Unexpected empty result", response.isEmpty());
        assertTrue("Expected userName not found", response.contains(expected));
    }

}
