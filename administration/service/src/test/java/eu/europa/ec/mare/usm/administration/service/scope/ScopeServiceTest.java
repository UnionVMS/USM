package eu.europa.ec.mare.usm.administration.service.scope;

import eu.europa.ec.mare.usm.administration.common.DateParser;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ScopeServiceTest extends DeploymentFactory {

    @EJB
    private ScopeService testSubject;

    @Test
    public void testFindScopeProvidingName() {
        // Setup
        FindScopesQuery query = new FindScopesQuery();
        query.setScopeName("GRC Quotas");
        query.setPaginator(getDefaultPaginator());
        ServiceRequest<FindScopesQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        String expected = "GRC Quotas";

        // Execute
        PaginationResponse<Scope> response = testSubject.findScopes(sRequest);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'scopeName' value", expected, getScopeName(response.getResults(), expected));
    }

    @Test
    public void testGetScopeProvidingId() {
        // Setup
        FindScopesQuery fsquery = new FindScopesQuery();
        fsquery.setScopeName("GRC Quotas");
        fsquery.setPaginator(getDefaultPaginator());
        ServiceRequest<FindScopesQuery> fsRequest = new ServiceRequest<>();
        fsRequest.setRequester("vms_admin_com");
        fsRequest.setBody(fsquery);
        PaginationResponse<Scope> sResponse = testSubject.findScopes(fsRequest);

        List<Scope> scopes = sResponse.getResults();
        Scope grc = scopes.get(0);

        GetScopeQuery query = new GetScopeQuery();
        query.setScopeId(grc.getScopeId());
        ServiceRequest<GetScopeQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        String expected = "GRC Quotas";

        // Execute
        Scope response = testSubject.getScope(sRequest);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'scopeName' value", expected, response.getName());
    }

    @Test
    public void testCreateScope() {
        ServiceRequest<Scope> request = createServiceRequest();
        Scope createdScope = testSubject.createScope(request);

        assertNotNull("scope was created ", createdScope);
        assertNotNull("the identifier was generated", createdScope.getScopeId());
        assertTrue("The name is the given one", request.getBody().getName().equalsIgnoreCase(createdScope.getName()));
    }

    @Test
    public void testCreateScopeInvalidActiveDates() {
        ServiceRequest<Scope> request = createServiceRequest();

        Date fr = new Date(System.currentTimeMillis() + 1399350);
        Date to = new Date(System.currentTimeMillis() - 1399350);
        request.getBody().setActiveFrom(fr);
        request.getBody().setActiveTo(to);

        try {
            testSubject.createScope(request);
            fail("Scope created with invalid (active) dates");
        } catch (Exception e) {
            System.out.println("Caught expected " + e.getCause().getMessage());
        }
    }

    @Test
    public void testCreateScopeInvalidDataDates() {
        ServiceRequest<Scope> request = createServiceRequest();

        Date fr = new Date(System.currentTimeMillis() + 1399350);
        Date to = new Date(System.currentTimeMillis() - 1399350);
        request.getBody().setDataFrom(fr);
        request.getBody().setDataTo(to);

        try {
            testSubject.createScope(request);
            fail("Scope created with invalid (data) dates");
        } catch (Exception e) {
            System.out.println("Caught expected " + e.getCause().getMessage());
        }
    }

    @Test
    public void testUpdateScope() {
        ServiceRequest<Scope> request = createServiceRequest();
        Scope createdScope = testSubject.createScope(request);
        assertNotNull("Scope was created ", createdScope);
        createdScope.setStatus("D");

        createdScope.setDataFrom(DateParser.parseDate("activeFrom", "2015-06-01T00:00:00.000+0000"));
        createdScope.setDataTo(DateParser.parseDate("activeTo", "2016-12-31T00:00:00.000+0000"));

        //first add of the datasets
        addDatasets(createdScope);

        request.setBody(createdScope);
        Scope updated = testSubject.updateScope(request);

        assertNotNull("Updated object was returned", updated);
        assertTrue("Status was updated", updated.getStatus().equalsIgnoreCase("D"));
        assertTrue("Status has datasets", updated.getDataSets().size() > 0);

        int finalSize = 0;

        if (updated.getDataSets() != null && !updated.getDataSets().isEmpty()) {
            finalSize = updated.getDataSets().size() / 2;
            List<DataSet> returned = updated.getDataSets().subList(0, finalSize);

            updated.setDataSets(returned);
        }
        request.setBody(updated);
        updated = testSubject.updateScope(request);
		assertEquals("Status has datasets", updated.getDataSets().size(), finalSize);
    }

    @Test
    public void testUpdateScopeInvalidDataDates() {
        ServiceRequest<Scope> request = createServiceRequest();
        Scope createdScope = testSubject.createScope(request);
        assertNotNull("Unexpected null Scope", createdScope);
        request.setBody(createdScope);

        Date fr = new Date(System.currentTimeMillis() + 1399350);
        Date to = new Date(System.currentTimeMillis() - 1399350);
        request.getBody().setDataFrom(fr);
        request.getBody().setDataTo(to);

        try {
            testSubject.updateScope(request);
            fail("Scope updated with invalid (data) dates");
        } catch (Exception e) {
            System.out.println("Caught expected " + e.getCause().getMessage());
        }
    }

    @Test
    public void testUpdateScopeInvalidActiveDates() {
        ServiceRequest<Scope> request = createServiceRequest();
        Scope createdScope = testSubject.createScope(request);
        assertNotNull("Unexpected null Scope", createdScope);
        request.setBody(createdScope);

        Date fr = new Date(System.currentTimeMillis() + 1399350);
        Date to = new Date(System.currentTimeMillis() - 1399350);
        request.getBody().setActiveFrom(fr);
        request.getBody().setActiveTo(to);

        try {
            testSubject.updateScope(request);
            fail("Scope updated with invalid (active) dates");
        } catch (Exception e) {
            System.out.println("Caught expected " + e.getCause().getMessage());
        }
    }

    @Test
    public void testDeleteScope() {
        ServiceRequest<Scope> request = createServiceRequest();
        Scope createdScope = testSubject.createScope(request);
        assertNotNull("scope was created ", createdScope);

        ServiceRequest<Long> deleteRequest = new ServiceRequest<>();
        deleteRequest.setRequester("usm_admin");
        deleteRequest.setBody(createdScope.getScopeId());
        testSubject.deleteScope(deleteRequest);

        FindScopesQuery query = new FindScopesQuery();
        query.setScopeName(request.getBody().getName());
        query.setPaginator(getDefaultPaginator());
        ServiceRequest<FindScopesQuery> scope = new ServiceRequest<>();
        scope.setRequester("vms_admin_com");
        scope.setBody(query);

        PaginationResponse<Scope> results = testSubject.findScopes(scope);
        List<Scope> scopes = results.getResults();
        assertTrue("Scope was deleted ", scopes == null || scopes.isEmpty());
    }

    @Test
    public void testGetScopes() {
        // Setup
        ServiceRequest<ScopeQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(new ScopeQuery());
        String fra_expected = "FRA Quotas";
        String grc_expected = "GRC Quotas";
        String rep_expected = "Some Reports";

        // Execute
        List<ComprehensiveScope> response = testSubject.getScopes(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Scope " + fra_expected + " not found", getName(response, fra_expected));
        assertNotNull("Expected Scope " + grc_expected + " not found", getName(response, grc_expected));
        assertNotNull("Expected Scope " + rep_expected + " not found", getName(response, rep_expected));
    }

    @Test
    public void testDeleteTestScopes() {
        // Setup
        ServiceRequest<ScopeQuery> setup = new ServiceRequest<>();
        setup.setRequester("vms_admin_com");
        setup.setBody(new ScopeQuery());
        List<ComprehensiveScope> response = testSubject.getScopes(setup);

        // Execute
        for (ComprehensiveScope item : response) {
            if (item.getName().startsWith("test")) {
                ServiceRequest<Long> request = new ServiceRequest<>();
                request.setRequester("usm_admin");
                request.setBody(item.getScopeId());
                try {
                    testSubject.deleteScope(request);
                } catch (Exception exc) {
                    // NOP
                }
            }
        }
    }

    private String getName(List<ComprehensiveScope> scopeNames, String expected) {
        for (ComprehensiveScope response : scopeNames) {
            if (response.getName().equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private ServiceRequest<Scope> createServiceRequest() {
        ServiceRequest<Scope> request = new ServiceRequest<>();
        request.setRequester("usm_admin");
        Scope newScope = new Scope();
        newScope.setName("testCreateScope " + System.currentTimeMillis());
        newScope.setStatus("E");
        newScope.setActiveFrom(DateParser.parseDate("activeFrom", "2015-01-01T00:00:00.000+0000"));
        newScope.setActiveTo(DateParser.parseDate("activeTo", "2015-12-31T00:00:00.000+0000"));

        request.setBody(newScope);

        return request;
    }

    private void addDatasets(Scope scope) {
        ServiceRequest<FindDataSetQuery> datasetReq = new ServiceRequest<>();
        datasetReq.setRequester("vms_admin_com");

        FindDataSetQuery bodyReq = new FindDataSetQuery();

        bodyReq.setApplicationName("Quota");
        bodyReq.setCategory(null);
        datasetReq.setBody(bodyReq);

        List<DataSet> datasetRet = testSubject.findDataSet(datasetReq);

        //if no datasets are found prevent creating useless variables
        if (datasetRet != null && !datasetRet.isEmpty()) {
            scope.setDataSets(datasetRet);
        }
    }

    private String getScopeName(List<Scope> scopes, String expected) {
        for (Scope scope : scopes) {
            if (scope.getName().equals(expected)) {
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

}
