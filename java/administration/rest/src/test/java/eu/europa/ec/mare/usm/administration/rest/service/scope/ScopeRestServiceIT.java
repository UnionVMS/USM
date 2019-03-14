package eu.europa.ec.mare.usm.administration.rest.service.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

import eu.europa.ec.mare.usm.administration.domain.DataSet;
import eu.europa.ec.mare.usm.administration.domain.FindDataSetQuery;
import eu.europa.ec.mare.usm.administration.domain.FindScopesQuery;
import eu.europa.ec.mare.usm.administration.domain.GetScopeQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.RoleQuery;
import eu.europa.ec.mare.usm.administration.domain.Scope;
import eu.europa.ec.mare.usm.administration.domain.ComprehensiveScope;
import eu.europa.ec.mare.usm.administration.domain.ScopeQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.DateParser;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;

/**
 * Integration test for the Role REST service.
 */
public class ScopeRestServiceIT extends AuthWrapper {
	private static final String URL = "http://localhost:8080/usm-administarion/rest/";
	private static final String GRC_QUOTAS = "GRC Quotas";
	private static final String FRA_QUOTAS = "FRA Quotas";
	private static final String SOME_REPORTS = "Some Reports";
  private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
  private static final String USM_ADMIN = "usm_admin";
	private ScopeRestClient client = null;
	private final String vms_admin_com;
	private final String usm_admin;

	public ScopeRestServiceIT() throws IOException 
  {
    super(VMS_ADMIN_COM_USER);
    vms_admin_com = getAuthToken();
    usm_admin = authenticate(USM_ADMIN);
	}

	@Before
	public void setUp() {
		client = new ScopeRestClient(endPoint);
	
	}


	/**
	 * Tests the findScopes operation.
	 */
	@Test
	public void testFindScopes() 
  {
		// Execute
		ServiceRequest<FindScopesQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		FindScopesQuery query = new FindScopesQuery();
		query.setScopeName(GRC_QUOTAS);
		query.setPaginator(getDefaultPaginator());
		request.setBody(query);
		ClientResponse response = client.findScopes(ClientResponse.class,request);
		GenericType<PaginationResponse<Scope>> gType = new GenericType<PaginationResponse<Scope>>() {
		};
		PaginationResponse<Scope> sar = response.getEntity(gType);
		// Verify
		assertNotNull("Unexpected null result", response);
		List<Scope> scopes = sar.getResults();
		assertNotNull("Expected Scope " + GRC_QUOTAS + " not found",
				getScope(scopes, GRC_QUOTAS));
	}

	/**
	 * Tests the getScope operation.
	 */
	@Test
	public void testGetScope() 
  {
	    // Set-up
	    Long scopeId = findScope(GRC_QUOTAS);
		// Execute
		Scope scope = fetchScope(vms_admin_com, scopeId);
		// Verify
		assertEquals("Unexpected 'scopeName' value", GRC_QUOTAS, scope.getName());
	}
	
  public Long findScope(String scopeName) 
  {
	    // Set-up
		ServiceRequest<FindScopesQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		FindScopesQuery query = new FindScopesQuery();
		query.setScopeName(scopeName);
		query.setPaginator(getDefaultPaginator());
		request.setBody(query);
    
		// Execute
		ClientResponse response = client.findScopes(ClientResponse.class,
				request);
		GenericType<PaginationResponse<Scope>> gType = new GenericType<PaginationResponse<Scope>>() {
		};
		PaginationResponse<Scope> sar = response.getEntity(gType);
		// Verify
		assertNotNull("Unexpected null result", response);
		List<Scope> scopes = sar.getResults();
		assertEquals("Unexpected result size", 1, scopes.size());
		return scopes.get(0).getScopeId();
	}

	private String getScope(List<Scope> scopes, String expected) {
		for (Scope scope : scopes) {
			if (scope.getName().equals(expected)) {
				return scope.getName();
			}
		}

		return null;
	}

	private ServiceRequest<Scope> createServiceRequest(){
		ServiceRequest<Scope> request=new ServiceRequest<Scope>();
		request.setRequester(usm_admin);
		Scope newScope=new Scope();
		newScope.setName("testCreateScope "+ System.currentTimeMillis());
		newScope.setStatus("E");
		newScope.setActiveFrom(DateParser.parseDate("activeFrom","2015-01-01T00:00:00.000+0000"));
		newScope.setActiveTo(DateParser.parseDate("activeTo","2015-12-31T00:00:00.000+0000"));
		
		request.setBody(newScope);
		
		return request;
	}
	
	@Test
	public void createScope(){
		ServiceRequest<Scope> createScope=createServiceRequest();
		
		ClientResponse response=client.createScope(ClientResponse.class, createScope);
		
		GenericType<Scope> gtype=new GenericType<Scope>(){};
		Scope scope=response.getEntity(gtype);
		
		assertNotNull("Scope was not created", scope);
		assertNotNull("The identifier wasn't generated", scope.getScopeId());
		assertTrue("The name does not match the given one", createScope.getBody().getName().equalsIgnoreCase(scope.getName()));
	}
	
	@Test
	public void updateScope(){
		ServiceRequest<Scope> createScope=createServiceRequest();
		
		ClientResponse response=client.createScope(ClientResponse.class, createScope);
		
		GenericType<Scope> gtype=new GenericType<Scope>(){};
		Scope scope=response.getEntity(gtype);
		
		assertNotNull("Scope was not created ", scope);
		
		scope.setStatus("D");
		scope.setDataFrom(DateParser.parseDate("activeFrom","2015-06-01T00:00:00.000+0000"));
		scope.setDataTo(DateParser.parseDate("activeTo","2016-12-31T00:00:00.000+0000"));
		addDatasets(scope);
		createScope.setBody(scope);

		response=client.updateScope(ClientResponse.class, createScope);
		Scope scopeUpdated=response.getEntity(gtype);
		
		assertNotNull("Scope was not updated",scopeUpdated);
		assertTrue("Status was not updated", scopeUpdated.getStatus().equalsIgnoreCase("D"));
		assertTrue("List of datasets is empty",scopeUpdated.getDataSets().size()>0);
		
		int finalSize=0;
		
		if(scopeUpdated.getDataSets()!=null&&!scopeUpdated.getDataSets().isEmpty()){
			finalSize=scopeUpdated.getDataSets().size()/2;
			List<DataSet> returned=scopeUpdated.getDataSets().subList(0,finalSize);
			
			scopeUpdated.setDataSets(returned);
		}
		createScope.setBody(scopeUpdated);
		response=client.updateScope(ClientResponse.class,createScope);
		assertTrue("Datasets size does not match the final size", response.getEntity(gtype).getDataSets().size()==finalSize);
	}
	
	@Test
	public void deleteScope(){
		ServiceRequest<Scope> createScope=createServiceRequest();
		ClientResponse response=client.createScope(ClientResponse.class, createScope);
		GenericType<Scope> gtype=new GenericType<Scope>(){};
		Scope scope=response.getEntity(gtype);
		
		assertNotNull("Scope was not created ", scope);
		
		ServiceRequest<String> deletedScope=new ServiceRequest<String>();
		deletedScope.setRequester(usm_admin);
		deletedScope.setBody(scope.getScopeId().toString());
		
		client.deleteScope(ClientResponse.class, deletedScope);

		Scope sc;
		// Execute
		try{
			sc = fetchScope(vms_admin_com, scope.getScopeId());
		} catch(UniformInterfaceException e){
			sc = null;
		}
		
		// Verify
		assertNull("Unexpected not null scope", sc);
		
	}
	
	private Paginator getDefaultPaginator(){
		Paginator paginator = new Paginator();
		paginator.setLimit(8);
		paginator.setOffset(0);
		paginator.setSortColumn("name");
		paginator.setSortDirection("DESC");
		return paginator;
	}
	
	private void addDatasets(Scope scope){
		ServiceRequest<FindDataSetQuery> datasetReq=new ServiceRequest<FindDataSetQuery>();
		datasetReq.setRequester(vms_admin_com);
		FindDataSetQuery bodyReq=new FindDataSetQuery();
		
		bodyReq.setApplicationName("Quota");
		bodyReq.setCategory("all");
		datasetReq.setBody(bodyReq);
		
		ClientResponse datasetRet=client.findDatasets(ClientResponse.class, datasetReq);
		
		//if no datasets are found prevent creating useless variables
		if (datasetRet.getStatus()!=204){
			GenericType<ServiceArrayResponse<DataSet>> gtype=new GenericType<ServiceArrayResponse<DataSet>>(){};
			ServiceArrayResponse<DataSet> retrievedDataSets=datasetRet.getEntity(gtype);
			scope.setDataSets(retrievedDataSets.getResults());
		}
		
	}
	
	private Scope fetchScope(String requester, Long scopeId){
		
		ServiceRequest<GetScopeQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		GetScopeQuery getScopeQuery = new GetScopeQuery();
		getScopeQuery.setScopeId(scopeId);
		request.setBody(getScopeQuery);

		ClientResponse response = client.getScope(ClientResponse.class,request);
		GenericType<Scope> gType = new GenericType<Scope>() {
		};
		
		assertNotNull("Unexpected null result", response);
		return response.getEntity(gType);
		
	}
	
	/**
	 * Tests the getScopeNames operation.
	 */
	@Test
	public void testScopes() {
		// Execute
		ServiceRequest<ScopeQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		request.setBody(new ScopeQuery());

		ClientResponse response = client.getScopes(ClientResponse.class,
				request);
		GenericType<ServiceArrayResponse<ComprehensiveScope>> gType = new GenericType<ServiceArrayResponse<ComprehensiveScope>>() {
		};
		ServiceArrayResponse<ComprehensiveScope> sar = response.getEntity(gType);

		// Verify
		assertNotNull("Unexpected null result", response);
		List<ComprehensiveScope> scopeNames = sar.getResults();
		assertNotNull("Expected Scope " + FRA_QUOTAS + " not found",
				getScopeName(scopeNames, FRA_QUOTAS));
		assertNotNull("Expected Scope " + GRC_QUOTAS + "not found",
				getScopeName(scopeNames, GRC_QUOTAS));
		assertNotNull("Expected Scope " + SOME_REPORTS + "not found",
				getScopeName(scopeNames, SOME_REPORTS));
	}
	
	private String getScopeName(List<ComprehensiveScope> sNames, String expected) {
		for (ComprehensiveScope name : sNames) {
			if (name.getName().equals(expected)) {
				return expected;
			}
		}

		return null;
	}
}
