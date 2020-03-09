package eu.europa.ec.mare.usm.administration.rest.service.scope;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.DateParser;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ScopeResourceIT extends BuildAdministrationDeployment {

    private static final String GRC_QUOTAS = "GRC Quotas";
    private static final String FRA_QUOTAS = "FRA Quotas";
    private static final String SOME_REPORTS = "Some Reports";
    private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testFindScopes() {
        Scope scope = findScope(GRC_QUOTAS);
        assertEquals(GRC_QUOTAS, scope.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetScope() {
        Long scopeId = findScope(GRC_QUOTAS).getScopeId();
        Scope scope = fetchScope(scopeId);
        assertEquals(GRC_QUOTAS, scope.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createScope() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Scope createScope = createScopeRequest();

        Response response = restClient.createScope(auth.getJwtoken(), createScope);
        assertEquals(OK.getStatusCode(), response.getStatus());

        Scope scope = response.readEntity(Scope.class);

        assertNotNull(scope);
        assertNotNull(scope.getScopeId());
        assertTrue(createScope.getName().equalsIgnoreCase(scope.getName()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateScope() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Scope createScope = createScopeRequest();

        Response response = restClient.createScope(auth.getJwtoken(), createScope);
        assertEquals(OK.getStatusCode(), response.getStatus());

        createScope = response.readEntity(Scope.class);
        createScope.setStatus("D");
        createScope.setDataFrom(DateParser.parseDate("activeFrom", "2015-06-01T00:00:00.000+0000"));
        createScope.setDataTo(DateParser.parseDate("activeTo", "2016-12-31T00:00:00.000+0000"));
        addDatasets(createScope);

        response = restClient.updateScope(auth.getJwtoken(), createScope);
        Scope scopeUpdated = response.readEntity(Scope.class);

        assertNotNull("Scope was not updated", scopeUpdated);
        assertEquals("D", scopeUpdated.getStatus());
        assertFalse(scopeUpdated.getDataSets().isEmpty());

        int finalSize = 0;

        if (scopeUpdated.getDataSets() != null && !scopeUpdated.getDataSets().isEmpty()) {
            finalSize = scopeUpdated.getDataSets().size() / 2;
            List<DataSet> returned = scopeUpdated.getDataSets().subList(0, finalSize);
            scopeUpdated.setDataSets(returned);
        }

        assertNotNull(scopeUpdated.getDataSets());
        assertEquals(finalSize, scopeUpdated.getDataSets().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteScope() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Scope scope = createScopeRequest();

        Response response = restClient.createScope(auth.getJwtoken(), scope);
        assertEquals(OK.getStatusCode(), response.getStatus());

        scope = response.readEntity(Scope.class);

        response = restClient.deleteScope(auth.getJwtoken(), String.valueOf(scope.getScopeId()));
        assertEquals(OK.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAllScopes() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        FindScopesQuery query = new FindScopesQuery();
        query.setPaginator(getDefaultPaginator());

        Response response = restClient.findScopes(auth.getJwtoken(), query);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<ComprehensiveScope> sar = response.readEntity(new GenericType<>() {});
        assertNotNull("Unexpected null result", sar);

        List<ComprehensiveScope> scopeNames = sar.getResults();
        assertNotNull("Expected Scope " + FRA_QUOTAS + " not found", getScopeName(scopeNames, FRA_QUOTAS));
        assertNotNull("Expected Scope " + GRC_QUOTAS + "not found", getScopeName(scopeNames, GRC_QUOTAS));
        assertNotNull("Expected Scope " + SOME_REPORTS + "not found", getScopeName(scopeNames, SOME_REPORTS));
    }

    private Scope findScope(String scopeName) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        FindScopesQuery query = new FindScopesQuery();
        query.setScopeName(scopeName);
        query.setPaginator(getDefaultPaginator());

        Response response = restClient.findScopes(auth.getJwtoken(), query);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PaginationResponse<Scope> sar = response.readEntity(new GenericType<>() {});
        List<Scope> scopes = sar.getResults();
        assertEquals(1, scopes.size());
        return scopes.get(0);
    }

    private Scope createScopeRequest() {
        Scope scope = new Scope();
        scope.setName("testCreateScope " + System.currentTimeMillis());
        scope.setStatus("E");
        scope.setActiveFrom(DateParser.parseDate("activeFrom", "2015-01-01T00:00:00.000+0000"));
        scope.setActiveTo(DateParser.parseDate("activeTo", "2015-12-31T00:00:00.000+0000"));
        return scope;
    }

    private void addDatasets(Scope scope) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Response response = restClient.findDatasets(auth.getJwtoken(), "Quota", "all");

        if (response.getStatus() != 204) {
            ServiceArrayResponse<DataSet> retrievedDataSets = response.readEntity(new GenericType<>() {
            });
            scope.setDataSets(retrievedDataSets.getResults());
        }
    }

    private Paginator getDefaultPaginator() {
        Paginator paginator = new Paginator();
        paginator.setLimit(20);
        paginator.setOffset(0);
        paginator.setSortColumn("name");
        paginator.setSortDirection("DESC");
        return paginator;
    }

    private Scope fetchScope(Long scopeId) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(VMS_ADMIN_COM_USER, PASSWORD);

        Response response = restClient.getScope(auth.getJwtoken(), String.valueOf(scopeId));
        assertEquals(OK.getStatusCode(), response.getStatus());

        Scope scope = response.readEntity(Scope.class);
        assertNotNull(scope);
        return scope;
    }

    private String getScopeName(List<ComprehensiveScope> sNames, String expected) {
        return sNames.stream()
                .filter(scope -> scope.getName().equals(expected))
                .findAny()
                .map(ComprehensiveScope::getName)
                .orElse(null);
    }
}
