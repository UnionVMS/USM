package eu.europa.ec.mare.usm.administration.service.role;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RoleServiceTest extends DeploymentFactory {

    @EJB
    private RoleService testSubject;

    @Test
    public void testFindRolesProvidingName() {
        // Setup
        FindRolesQuery query = new FindRolesQuery();
        query.setRoleName("USM-UserMa");
        query.setPaginator(getDefaultPaginator());
        ServiceRequest<FindRolesQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        String expected = "USM-UserManager";

        // Execute
        PaginationResponse<ComprehensiveRole> response = testSubject.findRoles(sRequest);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'roleName' value", expected, getCompRoleName(response.getResults(), expected));
    }

    @Test
    public void testGetRoleNames() {
        // Setup
        ServiceRequest<RoleQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(new RoleQuery());
        String expected = "USM-UserBrowser";

        // Execute
        List<String> response = testSubject.getRoleNames(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Role " + expected + " not found", getRoleName(response, expected));
    }

    @Test
    public void testGetRole() {
        // Setup
        FindRolesQuery query = new FindRolesQuery();
        query.setRoleName("USM-UserManager");
        query.setApplicationName("USM");
        query.setPaginator(getDefaultPaginator());
        ServiceRequest<FindRolesQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        PaginationResponse<ComprehensiveRole> response = testSubject.findRoles(sRequest);
        List<ComprehensiveRole> cRolesList = response.getResults();
        assertNotNull("Unexpected null response", response);
        assertTrue("Unexpected response size", cRolesList.size() > 0);
        Long roleId = cRolesList.get(0).getRoleId();

        // Setup
        GetRoleQuery query2 = new GetRoleQuery();
        query2.setRoleId(roleId);
        ServiceRequest<GetRoleQuery> sRequest2 = new ServiceRequest<>();
        sRequest2.setRequester("vms_admin_com");
        sRequest2.setBody(query2);

        // Execute
        Role response2 = testSubject.getRole(sRequest2);

        // Verify
        String expected = "USM-UserManager";
        assertNotNull("Unexpected null result", response2);
        assertEquals("Unexpected 'RoleName' value", expected, response2.getName());
    }

    @Test
    public void testCreateRole() {
        //setup
        ServiceRequest<ComprehensiveRole> request = createRequest();

        //execute
        ComprehensiveRole newRole = testSubject.createRole(request);

        //verify
        assertNotNull("Role Id is null", newRole.getRoleId());
        ServiceRequest<GetRoleQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(new GetRoleQuery(newRole.getRoleId()));
        Role retrievedRole = testSubject.getRole(sRequest);

        assertNotNull("Role with id " + newRole.getRoleId() + " does not exist", retrievedRole);
        assertEquals("Name of the role is correct", request.getBody().getName(), retrievedRole.getName());
    }

    @Test
    public void testUpdateRole() {
        //setup
        ServiceRequest<ComprehensiveRole> request = createRequest();
        ComprehensiveRole newRole = testSubject.createRole(request);


		assertNotNull("Successful role creation", newRole.getRoleId());

        newRole.setStatus("D");

        request.setBody(newRole);
        //Execute
        testSubject.updateRole(request);

        //verify
        ServiceRequest<GetRoleQuery> sRequest = new ServiceRequest<>();

        sRequest.setBody(new GetRoleQuery(newRole.getRoleId()));
        sRequest.setRequester("vms_admin_com");
        Role updatedRole = testSubject.getRole(sRequest);

        assertTrue("Status is updated ", updatedRole.getStatus().equalsIgnoreCase(newRole.getStatus()));
    }

    @Test
    public void testDeleteRole() {
        //setup
        ServiceRequest<ComprehensiveRole> request = createRequest();
        ComprehensiveRole newRole = testSubject.createRole(request);
		assertNotNull("Successful role creation", newRole.getRoleId());

        //Execute
        ServiceRequest<Long> deletedRole = new ServiceRequest<>();
        deletedRole.setBody(newRole.getRoleId());
        deletedRole.setRequester("usm_admin");
        testSubject.deleteRole(deletedRole);

        //verify
        ServiceRequest<GetRoleQuery> sRequest = new ServiceRequest<>();
        sRequest.setBody(new GetRoleQuery(newRole.getRoleId()));
        sRequest.setRequester("vms_admin_com");
        Role updatedRole = testSubject.getRole(sRequest);

		assertNull("Role deleted successfully", updatedRole);
    }

    @Test
    public void testDeleteTestRoles() {
        // Setup
        ServiceRequest<RoleQuery> setup = new ServiceRequest<>();
        setup.setRequester("vms_admin_com");
        setup.setBody(new RoleQuery());
        List<ComprehensiveRole> response = testSubject.getRoles(setup);

        // Execute
        for (ComprehensiveRole item : response) {
            if (item.getName().startsWith("test")) {
                ServiceRequest<Long> request = new ServiceRequest<>();
                request.setRequester("usm_admin");
                request.setBody(item.getRoleId());
                try {
                    testSubject.deleteRole(request);
                } catch (Exception exc) {
                    // NOP
                }
            }
        }
    }

    private ServiceRequest<ComprehensiveRole> createRequest() {
        ComprehensiveRole requestBody = new ComprehensiveRole();
        requestBody.setName("testRole" + System.currentTimeMillis());
        requestBody.setStatus("E");

        ServiceRequest<ComprehensiveRole> request = new ServiceRequest<>();
        request.setRequester("usm_admin");
        request.setBody(requestBody);

        return request;
    }

    private String getRoleName(List<String> names, String expected) {
        for (String roleName : names) {
            if (roleName.equals(expected)) {
                return roleName;
            }
        }
        return null;
    }

    private String getCompRoleName(List<ComprehensiveRole> cRoles, String expected) {
        for (ComprehensiveRole cRole : cRoles) {
            if (cRole.getName().equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private Paginator getDefaultPaginator() {
        Paginator paginator = new Paginator();
        paginator.setLimit(8);
        paginator.setOffset(0);
        paginator.setSortColumn("name");
        paginator.setSortDirection("DESC");
        return paginator;
    }

    @Test
    public void testGetRoles() {
        // Setup
        ServiceRequest<RoleQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(new RoleQuery());
        String user_expected = "User";
        String userManager_expected = "USM-UserManager";
        String userBrowser_expected = "USM-UserBrowser";

        // Execute
        List<ComprehensiveRole> response = testSubject.getRoles(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Role " + user_expected + " not found",
                getCompRoleName(response, user_expected));
        assertNotNull("Expected Role " + userManager_expected + " not found",
                getCompRoleName(response, userManager_expected));
        assertNotNull("Expected Role " + userBrowser_expected + " not found",
                getCompRoleName(response, userBrowser_expected));
    }

}
