package eu.europa.ec.mare.usm.administration.rest.service.role;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RoleResourceIT extends BuildAdministrationDeployment {

    private static final String USER_BROWSER = "USM-UserBrowser";
    private static final String ADMINISTRATOR = "Administrator";
    private static final String SUPER_USER = "Super User";
    private static final String ROLE_NAME = "USM-UserBrowser";
    private static final String ROLE_STATUS = "E";
    private static final String ROLE_DESCRIPTION = "View user information";
    private static final String ROLE_APPLICATION = "USM";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
	private static final String PASSWORD = "password";

	@EJB
	private AdministrationRestClient restClient;

    @Test
	@OperateOnDeployment("normal")
    public void testRoleNames() {
		AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

		Response response = restClient.getRoleNames(auth.getJwtoken());
		assertEquals(OK.getStatusCode(), response.getStatus());

		ServiceArrayResponse<String> sar = response.readEntity(new GenericType<>(){});
        List<String> roleNames = sar.getResults();

        assertNotNull("Expected Role " + USER_BROWSER + " not found", getRoleName(roleNames, USER_BROWSER));
        assertNotNull("Expected Role " + ADMINISTRATOR + "not found", getRoleName(roleNames, ADMINISTRATOR));
        assertNotNull("Expected Role " + SUPER_USER + "not found", getRoleName(roleNames, SUPER_USER));
    }

    @Test
	@OperateOnDeployment("normal")
    public void testFindRoles() {
        List<ComprehensiveRole> roles = findRoles();
        assertNotNull("Expected Role " + USER_BROWSER + " not found", getRole(roles, USER_BROWSER));
    }

    @Test
	@OperateOnDeployment("normal")
    public void testGetRole() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);
        Long roleId = findRole(ROLE_NAME);

        Response response = restClient.getRole(auth.getJwtoken(), String.valueOf(roleId));
        assertEquals(OK.getStatusCode(), response.getStatus());

        Role role = response.readEntity(Role.class);

        assertNotNull("Unexpected null result", response);
        assertNotNull("Unexpected null result.Features", role.getFeatures());
        assertEquals("Unexpected 'roleName' value", ROLE_NAME, role.getName());
        assertEquals("Unexpected 'roleDescription' value", ROLE_DESCRIPTION, role.getDescription());
        assertEquals("Unexpected 'roleStatus' value", ROLE_STATUS, role.getStatus());
    }

    @Test
	@OperateOnDeployment("normal")
    public void createRole() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        ComprehensiveRole request = createRoleRequest(auth);

        Response response = restClient.createRole(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ComprehensiveRole role = response.readEntity(ComprehensiveRole.class);

        assertNotNull("Unexpected null role", role);
        assertNotNull("Unexpected null permnissions", role.getFeatures());

        int permissionsAdded = request.getFeatures() != null ? request.getFeatures().size() : 0;

        assertTrue("Unexpected set of permissions",
                (role.getFeatures().size() > 0) && (role.getFeatures().size() == permissionsAdded));
    }

    @Test
	@OperateOnDeployment("normal")
    public void updateRole() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        ComprehensiveRole request = createRoleRequest(auth);

        Response response = restClient.createRole(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ComprehensiveRole sar = response.readEntity(ComprehensiveRole.class);
        sar.setStatus("D");

        List<Long> list = sar.getFeatures();
        List<Long> features = null;
        int finalLength = 0;
        if ((list != null) && (list.size() > 0)) {
            finalLength = list.size() / 2;
            features = list.subList(0, list.size() / 2);
        }
        sar.setFeatures(features);

        //responseUpdate
        response = restClient.updateRole(auth.getJwtoken(), sar);
        assertEquals(OK.getStatusCode(), response.getStatus());

        Role role = response.readEntity(Role.class);

        assertNotNull(role);
        assertEquals("D", role.getStatus());
        assertEquals(role.getFeatures().size(), finalLength);
    }

    @Test
	@OperateOnDeployment("normal")
    public void deleteRole() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        ComprehensiveRole request = createRoleRequest(auth);

        Response response = restClient.createRole(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ComprehensiveRole sar = response.readEntity(ComprehensiveRole.class);
        assertNotNull(sar);

        response = restClient.deleteRole(auth.getJwtoken(), String.valueOf(sar.getRoleId()));
        assertEquals(OK.getStatusCode(), response.getStatus());
    }

    @Test
	@OperateOnDeployment("normal")
    public void testGetFeatureGroupNames() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.getFeatureGroupNames(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<String> groupNames = response.readEntity(new GenericType<>(){});
        assertNotNull(groupNames);
        assertTrue(groupNames.getResults().size() > 0);
    }

    @Test
	@OperateOnDeployment("normal")
    public void testGetPermissions() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.findPermissions(auth.getJwtoken(), ROLE_APPLICATION, null);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<Feature> features = response.readEntity(new GenericType<>(){});

        assertNotNull(features);
        assertFalse(features.getResults().isEmpty());
    }

    @Test
	@OperateOnDeployment("normal")
    public void testRoles() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.getRoles(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<ComprehensiveRole> sar = response.readEntity(new GenericType<>(){});
        assertNotNull(sar);

        List<ComprehensiveRole> scopeNames = sar.getResults();

        assertNotNull("Expected Role " + USER_BROWSER + " not found", getRole(scopeNames, USER_BROWSER));
        assertNotNull("Expected Role " + ADMINISTRATOR + "not found", getRole(scopeNames, ADMINISTRATOR));
        assertNotNull("Expected Role " + SUPER_USER + "not found", getRole(scopeNames, SUPER_USER));
    }

    /** Helper Methods */

    private List<ComprehensiveRole> findRoles() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        FindRolesQuery query = new FindRolesQuery();
        query.setPaginator(getDefaultPaginator());
        query.getPaginator().setLimit(50);
        query.setApplicationName(ROLE_APPLICATION);

        Response response = restClient.findRoles(auth.getJwtoken(), query);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<ComprehensiveRole> sar = response.readEntity(new GenericType<>(){});
        assertNotNull(sar);
        return sar.getResults();
    }

    public Long findRole(String roleName) {
        List<ComprehensiveRole> roles = findRoles();
        return roles.stream()
                .filter(r -> r.getName().equals(roleName))
                .findAny()
                .map(ComprehensiveRole::getRoleId)
                .orElse(null);
    }

    private String getRoleName(List<String> rNames, String expected) {
        return rNames.stream()
                .filter(expected::equals)
                .findAny()
                .orElse(null);
    }

    private String getRole(List<ComprehensiveRole> roles, String expected) {
        return roles.stream()
                .filter(role -> role.getName().equalsIgnoreCase(expected))
                .findAny()
                .map(ComprehensiveRole::getName)
                .orElse(null);
    }

    private Paginator getDefaultPaginator() {
        Paginator paginator = new Paginator();
        paginator.setLimit(8);
        paginator.setOffset(0);
        paginator.setSortColumn("name");
        paginator.setSortDirection("DESC");
        return paginator;
    }

    private ComprehensiveRole createRoleRequest(AuthenticationJwtResponse auth) {
        ComprehensiveRole role = new ComprehensiveRole();
        role.setName("testRole" + System.currentTimeMillis());
        role.setStatus("E");

        Response response = restClient.getApplicationFeatures(auth.getJwtoken(), ROLE_APPLICATION);
        ServiceArrayResponse<Feature> features = response.readEntity(new GenericType<>(){});

        List<Feature> featureList = features.getResults();
        List<Long> permissions = new ArrayList<>();

        if (featureList != null && !featureList.isEmpty()) {
            for (Feature element : featureList) {
                permissions.add(element.getFeatureId());
            }
        }
        role.setFeatures(permissions);
        return role;
    }
}
