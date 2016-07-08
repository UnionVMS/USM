/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.rest.service.role;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import eu.europa.ec.mare.usm.administration.domain.ComprehensiveRole;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindPermissionsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindRolesQuery;
import eu.europa.ec.mare.usm.administration.domain.GetRoleQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.Role;
import eu.europa.ec.mare.usm.administration.domain.RoleQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;
import eu.europa.ec.mare.usm.administration.rest.service.application.ApplicationRestClient;

/**
 * Integration test for the Role REST service.
 */
public class RoleRestServiceIT extends AuthWrapper {
	private static final String USER_BROWSER = "USM-UserBrowser";
	private static final String ADMINISTRATOR = "Administrator";
	private static final String SUPER_USER = "Super User";
	private static final String URL = "http://localhost:8080/usm-administarion/rest/";
	private static final String ROLE_NAME = "USM-UserBrowser";
	private static final String ROLE_STATUS = "E";
	private static final String ROLE_DESCRIPTION = "View user information";
	private static final String ROLE_APPLICATION = "USM";
  private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
  private static final String USM_ADMIN = "usm_admin";
	private RoleRestClient client = null;
	private ApplicationRestClient appClient=null;
	private final String vms_admin_com;
	private final String usm_admin;

	public RoleRestServiceIT() throws IOException 
  {
    super(VMS_ADMIN_COM_USER);
    vms_admin_com = getAuthToken();
    usm_admin = authenticate(USM_ADMIN);
	}

	@Before
	public void setUp() {
		client = new RoleRestClient(endPoint);
		appClient=new ApplicationRestClient(endPoint);
	}

	/**
	 * Tests the getRoleNames operation.
	 */
	@Test
	public void testRoleNames() {
		// Execute
		ServiceRequest<RoleQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		request.setBody(new RoleQuery());

		ClientResponse response = client.getRoleNames(ClientResponse.class,
				request);
		GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
		};
		ServiceArrayResponse<String> sar = response.getEntity(gType);

		// Verify
		assertNotNull("Unexpected null result", response);
		List<String> roleNames = sar.getResults();
		assertNotNull("Expected Role " + USER_BROWSER + " not found",
				getRoleName(roleNames, USER_BROWSER));
		assertNotNull("Expected Role " + ADMINISTRATOR + "not found",
				getRoleName(roleNames, ADMINISTRATOR));
		assertNotNull("Expected Role " + SUPER_USER + "not found",
				getRoleName(roleNames, SUPER_USER));
	}

	/**
	 * Tests the findRoleNames operation.
	 */
	@Test
	public void testFindRoles() 
  {
		// Execute
		ServiceRequest<FindRolesQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		FindRolesQuery query = new FindRolesQuery();
		query.setPaginator(getDefaultPaginator());
    query.getPaginator().setLimit(50);
    query.setApplicationName("USM");
		request.setBody(query);
		ClientResponse response = client.findRoles(ClientResponse.class,
				request);
		GenericType<PaginationResponse<ComprehensiveRole>> gType = new GenericType<PaginationResponse<ComprehensiveRole>>() {
		};
		PaginationResponse<ComprehensiveRole> sar = response.getEntity(gType);
		// Verify
		assertNotNull("Unexpected null result", response);
		List<ComprehensiveRole> roles = sar.getResults();
		assertNotNull("Expected Role " + USER_BROWSER + " not found",
				getRole(roles, USER_BROWSER));
	}

	/**
	 * Tests the getRole operation.
	 */
	@Test
	public void testGetRole() 
  {
	  // Set-up
	  Long roleId = findRole();
    
		// Execute
		ServiceRequest<GetRoleQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		GetRoleQuery getRoleQuery = new GetRoleQuery();
		getRoleQuery.setRoleId(roleId);
		request.setBody(getRoleQuery);

		ClientResponse response = client.getRole(ClientResponse.class,
				request);
		GenericType<Role> gType = new GenericType<Role>() {
		};
		Role role = response.getEntity(gType);
    
		// Verify
		assertNotNull("Unexpected null result", response);
		assertNotNull("Unexpected null result.Features", role.getFeatures());
		assertEquals("Unexpected 'roleName' value", ROLE_NAME, role.getName());
		assertEquals("Unexpected 'roleDescription' value", ROLE_DESCRIPTION, role.getDescription());
		assertEquals("Unexpected 'roleStatus' value", ROLE_STATUS, role.getStatus());
	}
	
  public Long findRole() 
  {
		ServiceRequest<FindRolesQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		FindRolesQuery query = new FindRolesQuery();
		query.setPaginator(getDefaultPaginator());
		request.setBody(query);
    request.getBody().setRoleName(ROLE_NAME);
    request.getBody().setApplicationName(ROLE_APPLICATION);
    request.getBody().setStatus(ROLE_STATUS);
    
		ClientResponse response = client.findRoles(ClientResponse.class,
				request);
		GenericType<PaginationResponse<ComprehensiveRole>> gType = new GenericType<PaginationResponse<ComprehensiveRole>>() {
		};
		PaginationResponse<ComprehensiveRole> sar = response.getEntity(gType);
    
		// Verify
		assertNotNull("Unexpected null result", response);
		List<ComprehensiveRole> roles = sar.getResults();
		assertEquals("Unexpected result size", 1, roles.size());
    return roles.get(0).getRoleId();
	}
  
  
	private String getRoleName(List<String> rNames, String expected) {
		for (String name : rNames) {
			if (name.equals(expected)) {
				return name;
			}
		}

		return null;
	}

	private String getRole(List<ComprehensiveRole> roles, String expected) {
		for (ComprehensiveRole role : roles) {
			if (role.getName().equalsIgnoreCase(expected)) {
				return role.getName();
			}
		}

		return null;
	}

	@Test
	public void createRole()
  {
    ServiceRequest<ComprehensiveRole> request = createRequest();

    int permissionsAdded = request.getBody().getFeatures() != null ? request.getBody().getFeatures().size() : 0;

    ClientResponse response = client.createRole(ClientResponse.class,
                                                request);

    assertEquals("Unexpected Response.Status",
            Response.Status.OK.getStatusCode(),
            response.getStatus());

    ComprehensiveRole role = response.getEntity(ComprehensiveRole.class);
    assertNotNull("Unexpected null role", role);
    assertNotNull("Unexpected null permnissions", role.getFeatures());

    assertTrue("Unexpected set of permissions", 
              (role.getFeatures().size() > 0) && (role.getFeatures().size() == permissionsAdded));
		
	}
	
	@Test
	public void updateRole()
  {
		ServiceRequest<ComprehensiveRole> request=createRequest();
		
		ClientResponse response = client.createRole(ClientResponse.class, 
                request);

		assertEquals("Unexpected Response.Status", 
                  Response.Status.OK.getStatusCode(), 
                  response.getStatus());

		ComprehensiveRole sar = response.getEntity(ComprehensiveRole.class);
		sar.setStatus("D");
		request.setBody(sar); 
		
		List<Long> list=sar.getFeatures();
		List<Long> features=null;
		int finalLength=0;
		if ((list!=null)&&(list.size()>0)){
			finalLength=list.size()/2;
			features=list.subList(0, list.size()/2);
		}
		sar.setFeatures(features);
		
		//responseUpdate
		response = client.updateRole(ClientResponse.class,request);

		assertEquals("Unexpected Response.Status",  
                 Response.Status.OK.getStatusCode(), 
                 response.getStatus());
		
		GenericType<Role> roleType=new GenericType<Role>(){};
		
		Role role=response.getEntity(roleType);
		
		assertNotNull("Unexpected returned role is null",role);
		assertTrue("Unexpected Status", role.getStatus().equals("D"));
		assertEquals("Unexpected permissions", role.getFeatures().size(),finalLength );
	}
	
	@Test
	public void deleteRole()
  {
		//prepare the role
    ServiceRequest<ComprehensiveRole> request=createRequest();
		
		ClientResponse response = client.createRole(ClientResponse.class, 
                                                request);

		assertEquals("Unexpected Response.Status", 
		Response.Status.OK.getStatusCode(), 
		response.getStatus());

		GenericType<ComprehensiveRole> gType = new GenericType<ComprehensiveRole>() {
		};
		ComprehensiveRole sar = response.getEntity(gType);
		
		//delete the role
		ServiceRequest<String> deleteRequest=new ServiceRequest<>();
		deleteRequest.setBody(sar.getRoleId().toString());
		deleteRequest.setRequester(request.getRequester());
		
		 response = client.deleteRole(ClientResponse.class, 
                deleteRequest);
		 
		 assertEquals("Unexpected Response.Status",Response.Status.OK.getStatusCode(),
				 response.getStatus());
		 
	}
	
	
	 private ServiceRequest<ComprehensiveRole> createRequest()
   {
     ComprehensiveRole requestBody = new ComprehensiveRole();
     requestBody.setName("testRole" + System.currentTimeMillis());
     requestBody.setStatus("E");

     ServiceRequest<ComprehensiveRole> request = new ServiceRequest<>();
     request.setRequester(usm_admin);
     request.setBody(requestBody);

     ServiceRequest<String> featureReq = new ServiceRequest<>();
     featureReq.setBody("USM");
     featureReq.setRequester(request.getRequester());

     ClientResponse featureResponse = appClient.getApplicationFeatures(ClientResponse.class, featureReq);
     GenericType<ServiceArrayResponse<Feature>> gType1 = new GenericType<ServiceArrayResponse<Feature>>() {
     };
     ServiceArrayResponse<Feature> features = featureResponse.getEntity(gType1);

     List<Feature> featureList = features.getResults();
     List<Long> permissions = new ArrayList<>();
     //execute
     if (featureList != null && !featureList.isEmpty()) {
       for (Feature element : featureList) {
         permissions.add(element.getFeatureId());
       }
     }
     request.getBody().setFeatures(permissions);
     return request;
	 }
	 
	 @Test
	 public void testGetFeatureGroupNames(){
		 ServiceRequest<String> group=new ServiceRequest<String>();
		 group.setRequester(vms_admin_com);
		 group.setBody("");
		 
		 ClientResponse response=client.getFeatureGroupNames(ClientResponse.class,group);
		 GenericType<PaginationResponse<String>> gtype=new GenericType<PaginationResponse<String>>(){};
		 
		 PaginationResponse<String> groupNames=response.getEntity(gtype);
		 assertNotNull("Returned list is not null",groupNames);
		 assertNotNull("Return list of group has elements ",groupNames.getResults().size()>0);
	 }
	 
	 @Test
	 public void testGetPermissions(){
		 
		 ServiceRequest<FindPermissionsQuery> request=new ServiceRequest<>();
		 request.setRequester(vms_admin_com);
		 FindPermissionsQuery query=new FindPermissionsQuery();
		 query.setApplication("USM");
		 query.setGroup(null);
		 
		 ClientResponse retrievedGroups=client.findPermissions(ClientResponse.class, request);
		 GenericType<ServiceArrayResponse<Feature>> gtype=new GenericType<ServiceArrayResponse<Feature>>(){};
		 
		 ServiceArrayResponse<Feature> features=retrievedGroups.getEntity(gtype);
		 assertNotNull("Response is not null",features);
		 assertTrue("The list is bigger than 0", features.getResults().size()>0);
	 }
	 
	private Paginator getDefaultPaginator() {
		Paginator paginator = new Paginator();
		paginator.setLimit(8);
		paginator.setOffset(0);
		paginator.setSortColumn("name");
		paginator.setSortDirection("DESC");
		return paginator;
	}

	/**
	 * Tests the getRoles operation.
	 */
	@Test
	public void testRoles() {
		// Execute
		ServiceRequest<RoleQuery> request = new ServiceRequest<>();
		request.setRequester(vms_admin_com);
		request.setBody(new RoleQuery());

		ClientResponse response = client.getRoles(ClientResponse.class,
				request);
		GenericType<ServiceArrayResponse<ComprehensiveRole>> gType = new GenericType<ServiceArrayResponse<ComprehensiveRole>>() {
		};
		ServiceArrayResponse<ComprehensiveRole> sar = response
				.getEntity(gType);

		// Verify
		assertNotNull("Unexpected null result", response);
		List<ComprehensiveRole> scopeNames = sar.getResults();
		assertNotNull("Expected Role " + USER_BROWSER + " not found",
				getRole(scopeNames, USER_BROWSER));
		assertNotNull("Expected Role " + ADMINISTRATOR + "not found",
				getRole(scopeNames, ADMINISTRATOR));
		assertNotNull("Expected Role " + SUPER_USER + "not found",
				getRole(scopeNames, SUPER_USER));
	}

}