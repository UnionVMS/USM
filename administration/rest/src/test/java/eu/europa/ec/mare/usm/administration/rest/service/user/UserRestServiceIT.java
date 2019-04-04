package eu.europa.ec.mare.usm.administration.rest.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformation;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformationResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.ComprehensiveUserContext;
import eu.europa.ec.mare.usm.administration.domain.FindUserContextsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.domain.UserContext;
import eu.europa.ec.mare.usm.administration.domain.UserContextResponse;
import eu.europa.ec.mare.usm.administration.domain.Preference;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;
import eu.europa.ec.mare.usm.administration.rest.service.authentication.AuthenticationRestClient;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;

/**
 * Integration Test for REST Web Service implementation of the 
 * View/Manage User services
 */
public class UserRestServiceIT extends AuthWrapper{
  private static final String USER_NAME = "vms_user_fra";
  private static final String ORGANISATION_NAME = "FRA";
  private static final String VMS_ADMIN_COM_USER = "vms_admin_com";
  private UserRestClient manageUserClient = null;
  private AuthenticationRestClient  authenticationClient;
  private final String vms_admin_com;
  private String usmAdmin;

  public UserRestServiceIT()
  throws IOException 
  {
    super(VMS_ADMIN_COM_USER);
    vms_admin_com = getAuthToken();
  }

  @Before
  public void setUp() 
  {
    manageUserClient = new UserRestClient(endPoint);
    authenticationClient = new AuthenticationRestClient(endPoint);
    usmAdmin = login("usm_admin", "password");
  }

  @Test
  public void testFindUsers() 
  {
	  ServiceRequest<FindUsersQuery> request = new ServiceRequest<>();
	  request.setRequester(vms_admin_com);
	  Paginator paginator = new Paginator();
	  paginator.setOffset(0);
	  paginator.setLimit(4);
	  paginator.setSortColumn("user_name");
	  paginator.setSortDirection("ASC");
    FindUsersQuery query = new FindUsersQuery();
    query.setName(USER_NAME);
    query.setOrganisation(ORGANISATION_NAME);
    query.setPaginator(paginator);
    request.setBody(query); 

    // Execute
    ClientResponse response = manageUserClient.findUsers(ClientResponse.class, request);
    GenericType<PaginationResponse<UserAccount>> gType = new GenericType<PaginationResponse<UserAccount>>() {
    };
    PaginationResponse<UserAccount> cur = response.getEntity(gType);

    // Verify
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'userName' value", USER_NAME, 
                 getSearchName(cur.getResults(), USER_NAME));
  }

  @Test
  public void testFindUsersByManager() 
  {
    ServiceRequest<FindUsersQuery> request = new ServiceRequest<>();
    request.setRequester(usmAdmin);
    request.setRoleName("USM-UserManager");
    Paginator paginator = new Paginator();
    paginator.setOffset(0);
    paginator.setLimit(4);
    paginator.setSortColumn("user_name");
    paginator.setSortDirection("ASC");
    FindUsersQuery query = new FindUsersQuery();
    query.setName(USER_NAME);
    query.setOrganisation(ORGANISATION_NAME);
    query.setPaginator(paginator);
    request.setBody(query); 

    // Execute
    ClientResponse response = manageUserClient.findUsers(ClientResponse.class, request);
    GenericType<PaginationResponse<UserAccount>> gType = new GenericType<PaginationResponse<UserAccount>>() {
    };
    PaginationResponse<UserAccount> cur = response.getEntity(gType);

    // Verify
    assertNotNull("Unexpected null result", response);
    assertEquals("Unexpected 'userName' value", USER_NAME, 
                 getSearchName(cur.getResults(), USER_NAME));
  }
  
  @Test
  public void testGetUser() 
  {
	  ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
	  request.setRequester(vms_admin_com);
    GetUserQuery query = new GetUserQuery();
    query.setUserName(USER_NAME);
    request.setBody(query);

    // Execute
    UserAccount result = manageUserClient.getUser(UserAccount.class, request);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected 'userName' value", USER_NAME, result.getUserName());
  }

  @Test
  public void testGetNames() 
  {
	  ServiceRequest<String> request = new ServiceRequest<>();
	  request.setRequester(usmAdmin);
    request.setBody("");

    // Execute
    ClientResponse response = manageUserClient.getUserNames(request);

    // Verify
    assertEquals("Unexpected http response status", 200, response.getStatus());
    GenericType<ServiceArrayResponse<String>> gType = new GenericType<ServiceArrayResponse<String>>() {
    };
    ServiceArrayResponse<String> sar = response.getEntity(gType);
    assertNotNull("Unexpected null service-response", response);
    List<String> result = sar.getResults();
    assertNotNull("Unexpected null result", result);
    assertFalse("Unexpected empty result", result.isEmpty());
  }

  @Test
  public void testCreateUser() 
  {
    // Set-up
    UserAccount user = createUser();
    ServiceRequest<UserAccount> request = new ServiceRequest<>();
    request.setBody(user);
    request.setRequester(usmAdmin);

    // Execute
    ClientResponse response = manageUserClient.createUser(ClientResponse.class, 
                                                request);

    // Verify
    assertEquals("Unexpected Response.Status", 
                  Response.Status.OK.getStatusCode(), 
                  response.getStatus());
  }

  @Test
  public void testUpdateUser() 
  {
    // Set-up
    UserAccount user = createUser();
    ServiceRequest<UserAccount> request=new ServiceRequest<>();
    request.setBody(user);
    request.setRequester(usmAdmin);
    ClientResponse response = manageUserClient.createUser(ClientResponse.class, 
    		request);
    GenericType<UserAccount> gtype=new GenericType<UserAccount>(){};
    
    user=response.getEntity(gtype);
    		
    assertEquals("Unexpected Response.Status", 
                  Response.Status.OK.getStatusCode(), 
                  response.getStatus());

    // Execute
    user.getPerson().setFirstName("firstNameUpdated");
    user.getPerson().setLastName("lastNameUpdated");
    request.setBody(user);
    request.setRequester(usmAdmin);
    
    ClientResponse response2 = manageUserClient.updateUser(ClientResponse.class,
                                                request);

    // Verify
    assertEquals("Unexpected Response.Status", 
                  Response.Status.OK.getStatusCode(), 
                  response2.getStatus());
  }

  
  /**
   * Tests the changePassword operation
   */
  @Test
  public void testChangePasswordByAdministrator() 
  {
    // Set-up
    UserAccount testCuserAccount = createUser();
    ServiceRequest<UserAccount> setup=new ServiceRequest<>();
    setup.setBody(testCuserAccount);
    setup.setRequester(usmAdmin);
    ClientResponse response = manageUserClient.createUser(ClientResponse.class, 
                                                setup);
    assertEquals("Unexpected Response.Status", 
                  Response.Status.OK.getStatusCode(), 
                  response.getStatus());

    // Execute
    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
    request.setRequester(usmAdmin);
    request.setBody(new ChangePassword());
    request.getBody().setUserName(testCuserAccount.getUserName());
    request.getBody().setNewPassword("p@3$w0rdD");
    manageUserClient.changePassword(request);
    
    // Verify: test user logs-in with changed password
    login(testCuserAccount.getUserName(), request.getBody().getNewPassword());
  }
  
  /**
   * Tests the changePassword operation
   */
  @Test
  public void testChangePasswordByEndUser() 
  {
    // Set-up: as adminitrator create test user and set initial password
    String initialPassword = "p@3$w0rdD";

    // 1: adminitrator creates test user
    UserAccount testCuserAccount = createUser();
    ServiceRequest<UserAccount> setup = new ServiceRequest<>();
    setup.setBody(testCuserAccount);
    setup.setRequester(usmAdmin);
    ClientResponse response = manageUserClient.createUser(ClientResponse.class,
                                                setup);
    assertEquals("Unexpected Response.Status",
            Response.Status.OK.getStatusCode(),
            response.getStatus());
    
    // 2: adminitrator sets the test user initial password
    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
    request.setRequester(usmAdmin);
    request.setBody(new ChangePassword());
    request.getBody().setUserName(testCuserAccount.getUserName());
    request.getBody().setNewPassword(initialPassword);
    manageUserClient.changePassword(request);

    // Execute: test user logs in and changes his own password
    String testUser = login(testCuserAccount.getUserName(), initialPassword);
    String updatedPassword = "P@3$w0rdD";
    request.setRequester(testUser);
    request.setBody(new ChangePassword());
    request.getBody().setUserName(testCuserAccount.getUserName());
    request.getBody().setCurrentPassword(initialPassword);
    request.getBody().setNewPassword(updatedPassword);
    manageUserClient.changePassword(request);
    
    // Verify: test user logs-in with changed password
    login(testCuserAccount.getUserName(), updatedPassword);
  }

  
	@Test
	public void testGetUserContexts() 
  {
		ServiceRequest<FindUserContextsQuery> request = new ServiceRequest<>();
		request.setRequester(usmAdmin);
		FindUserContextsQuery query = new FindUserContextsQuery();
		query.setUserName(USER_NAME);
		request.setBody(query);

		// Execute
		ClientResponse response = manageUserClient.getUserContexts(ClientResponse.class, request);
		
		GenericType<UserContextResponse> gType = new GenericType<UserContextResponse>() {
	    };
	
		// Verify
		String expectedRoleName = "User";
		assertNotNull("Unexpected null response", response);
    UserContextResponse cur = response.getEntity(gType);
		assertNotNull("Unexpected null UserContextResponse", cur);
		assertNotNull("Unexpected null UserContextResponse.results", cur.getResults());
		assertNotNull("Expected 'User' role not found", getUserContext(cur.getResults(),expectedRoleName));
	}

  
  private UserAccount createUser() 
  {
    UserAccount ret = new UserAccount();

    ret.setUserName("testRestUser" + System.currentTimeMillis());
    ret.setStatus("E");
    Organisation org=new Organisation();
    ret.setOrganisation(org);
    org.setName("DG-MARE");
    
    
    Person person=new Person();
    ret.setPerson(person);
    person.setFirstName("Test-Rest");
    person.setLastName("User");
    person.setEmail(ret.getUserName() + "@email.com");
    
    ret.setActiveFrom(new Date(System.currentTimeMillis() - 3600000));
    ret.setActiveTo(new Date(System.currentTimeMillis() + 36000000));

    return ret;
  }
  
  
  private String login(String userName, String password) 
  {
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUserName(userName);
    request.setPassword(password);
    
    AuthenticationJwtResponse response = authenticationClient.authenticateUser(request);
    if (!response.isAuthenticated()) {
      fail("User '" + userName +"' failed to authenticate");
    }
    
    return response.getJWToken();
  }


  private String getSearchName(List<UserAccount> cUsers, String expected) {
    for (UserAccount cUser : cUsers) {
      if (cUser.getUserName().equals(expected)) {
        return expected;
      } else if (cUser.getPerson().getFirstName().equals(expected)) {
        return expected;
      } else if (cUser.getPerson().getLastName().equals(expected)) {
        return expected;
      }
    }

    return null;
  }
  
  private ComprehensiveUserContext getUserContext(List<ComprehensiveUserContext> cUserContexts, String expected) {
	  if (cUserContexts!=null&&!cUserContexts.isEmpty()){
		    for (ComprehensiveUserContext cUserContext : cUserContexts) {
		      if (cUserContext!=null&&cUserContext.getRole()!=null&&cUserContext.getRole().equals(expected)) {
		        return cUserContext;
		      }
		    }
	  	}
	    return null;
	  }

  @Test
  public void testCreateUpdateDeleteUserContext()
  {
	  // Set-up
      ServiceRequest<UserContext> userContextRequest = createRequest("updateUser");

      FindUserContextsQuery query = new FindUserContextsQuery();
      query.setUserName(userContextRequest.getBody().getUserName());
      ServiceRequest<FindUserContextsQuery> sRequest = new ServiceRequest<>();
      sRequest.setRequester(vms_admin_com);
      sRequest.setBody(query);
          
      // Execute
      UserContextResponse response = null;
      try {      
        response= manageUserClient.getUserContexts(UserContextResponse.class,sRequest);
      } catch (UniformInterfaceException e) {
        // A lack of contests will trigger a 404 response that may result in an exception
      }
      String expected = "Administrator";
      Long userContextId = null;

      if (response==null||getUserContext(response.getResults(), expected) == null) {
          UserContext result = manageUserClient.createUserContext(UserContext.class,userContextRequest);
          assertNotNull(result);
          userContextId = result.getUserContextId();
      } else {
          userContextId = getUserContext(response.getResults(), expected).getUserContextId();
      }
	    // Execute
	    UserContext userContext = userContextRequest.getBody();
	    userContext.setRoleId(Long.valueOf(2));
	    userContext.setScopeId(Long.valueOf(2));
	    userContext.setUserContextId(userContextId);
	    userContextRequest.setBody(userContext);

	    userContext = manageUserClient.updateUserContext(UserContext.class,userContextRequest);
	    
	    assertNotNull(userContext);
	    assertTrue("Scope was updated ", userContext.getScopeId().equals(2L));
	    
	    //delete the user context as well
	    ServiceRequest<String> deleteRequest=new ServiceRequest<>();
	    deleteRequest.setBody(userContextId.toString());
	    deleteRequest.setRequester(usmAdmin);
	    manageUserClient.deleteUserContext(ClientResponse.class,
                                         userContextRequest.getBody().getUserName(),
                                         deleteRequest);
	    
	    //test the deletion result
      try { 
        response = manageUserClient.getUserContexts(UserContextResponse.class,sRequest);
        assertNull("After deletion the record was found",getUserContextId(response.getResults(), userContextId));
      } catch (UniformInterfaceException e) {
        // A lack of contests will trigger a 404 response that may result in an exception
      }
  }
  
   private ServiceRequest<UserContext> createRequest(String operation) 
   {
	    ServiceRequest<UserContext> request = new ServiceRequest<>();
	    UserContext userContext = new UserContext();
	    userContext.setUserName("guest");
	
	    userContext.setUserContextId(null);
	    userContext.setRoleId(Long.valueOf(2));
	    userContext.setScopeId(Long.valueOf(1));
	
	    request.setBody(userContext);
	    request.setRequester(usmAdmin);
	    return request;
	}
   
   private Long getUserContextId(List<ComprehensiveUserContext> cUserContexts,
			Long expected) {
		for (ComprehensiveUserContext userContext : cUserContexts) {
			if (userContext!=null&& userContext.getUserContextId().equals(expected)) {
				return userContext.getUserContextId();
			}
		}

		return null;
	}
   
   @Test
   public void testCopyUserContext(){
	 //set-up
	 		FindUserContextsQuery query = new FindUserContextsQuery();
	 		query.setUserName("vms_admin_com");
	 		ServiceRequest<FindUserContextsQuery> sRequest = new ServiceRequest<>();
	 		sRequest.setRequester(vms_admin_com);
	 		sRequest.setBody(query);
	 		
	 		//from user contexts
	 		UserContextResponse responseUsmUser =  manageUserClient.getUserContexts(UserContextResponse.class,sRequest); 
	 				

	 		//toUser contexts
	 		query.setUserName("vms_user_com");
	 		UserContextResponse responseGuestInitial = manageUserClient.getUserContexts(UserContextResponse.class,sRequest); 
	 		
	 		ServiceRequest<UserContextResponse> requestCpy=new ServiceRequest<>();
	 		requestCpy.setRequester(usmAdmin);
	 		requestCpy.setBody(responseUsmUser);
	 		
	 		
	 		ClientResponse resp=manageUserClient.copyUserProfiles(ClientResponse.class,requestCpy, "vms_user_com");
	 		
	 		assertEquals("The operation did not succeeded!",resp.getStatus(), Response.Status.OK.getStatusCode());
	 		
	 		UserContextResponse responseGuestFinal=manageUserClient.getUserContexts(UserContextResponse.class,sRequest); 
	 		
	 		assertTrue("Was the profile copied?", checkUserContexts(responseUsmUser.getResults(),responseGuestFinal.getResults()));
	 	
	 		ServiceRequest<String> contextRequest=new ServiceRequest<>();
	 		contextRequest.setRequester(usmAdmin);
	 		//tear down
	 		
	 		//delete copied contexts 
	 		for (ComprehensiveUserContext element:responseGuestFinal.getResults()){
	 				contextRequest.setBody(element.getUserContextId().toString());
	 				manageUserClient.deleteUserContext(ClientResponse.class,"vms_user_com",contextRequest);
	 		}
	 		
      try {
        responseGuestFinal=manageUserClient.getUserContexts(UserContextResponse.class,sRequest); 
        assertTrue("Some contexts not deleted", responseGuestFinal.getResults().isEmpty());
      } catch (UniformInterfaceException e) {
        // A lack of contests will trigger a 404 response that may result in an exception
      }
      
	 		//add original contexts
	 		ServiceRequest<UserContext> addedRequest=new ServiceRequest<>();
	 		addedRequest.setRequester(usmAdmin);
	 		UserContext uc=new UserContext();
	 		uc.setUserName("vms_user_com");
	 		addedRequest.setBody(uc);
	 		for (ComprehensiveUserContext element:responseGuestInitial.getResults()){
	 			uc.setRoleId(element.getRoleId());
	 			uc.setScopeId(element.getScopeId());
	 			manageUserClient.createUserContext(ClientResponse.class,addedRequest );
	 		}
	 		
	 		responseGuestFinal=manageUserClient.getUserContexts(UserContextResponse.class,sRequest); 
	 		assertTrue("Some contexts could not be added back to the original state", responseGuestFinal.getResults().size()>0);
	 	}
	 	
	 	private boolean checkUserContexts(List<ComprehensiveUserContext> fromList, List<ComprehensiveUserContext> toList){
	 		for (ComprehensiveUserContext element:fromList){
	 			if (!toList.contains(element)){
	 				return false;
	 			}
	 		}
	 		return true;
	 	}
	 	
    @Test
    public void testGetChallenges() {
        ServiceRequest<String> request = new ServiceRequest<String>();
        request.setRequester(vms_admin_com);
        String userName = "vms_admin_com";

        // Execute
        ClientResponse response = manageUserClient.getChallenges(ClientResponse.class, userName, request);

        GenericType<ChallengeInformationResponse> gType = new GenericType<ChallengeInformationResponse>() {
        };
;
        // Verify
        String expectedRoleName = "User";
        assertNotNull("Unexpected null response", response);
        ChallengeInformationResponse challengeInformationResponse = response.getEntity(gType);
        assertNotNull("Unexpected null UserContextResponse", challengeInformationResponse);
        assertNotNull("Unexpected null UserContextResponse.results", challengeInformationResponse.getResults());

    }
    
    @Test
    public void testSetChallenges() {
        ServiceRequest<ChallengeInformationResponse> request = new ServiceRequest<ChallengeInformationResponse>();
        request.setRequester(vms_admin_com);
        String userName = "vms_admin_com";
        ChallengeInformationResponse challengeInformationRequest = new ChallengeInformationResponse();
        challengeInformationRequest.setUserPassword("password");
        List<ChallengeInformation> results = new ArrayList<ChallengeInformation>();        
        ChallengeInformation challengeInformation = new ChallengeInformation();
        //challengeInformation.setChallengeId(100014l);
        challengeInformation.setChallenge("Name of first pet updated!");
        challengeInformation.setResponse("Tartampion updated!");
        results.add(challengeInformation);
        challengeInformationRequest.setResults(results);
        request.setBody(challengeInformationRequest);

        // Execute
        ClientResponse response = manageUserClient.setChallenges(ClientResponse.class, userName, request);

        GenericType<ChallengeInformationResponse> gType = new GenericType<ChallengeInformationResponse>() {
        };
;
        // Verify
        String expectedRoleName = "User";
        assertNotNull("Unexpected null response", response);
        ChallengeInformationResponse challengeInformationResponse = response.getEntity(gType);
        assertNotNull("Unexpected null UserContextResponse", challengeInformationResponse);
        assertNotNull("Unexpected null UserContextResponse.results", challengeInformationResponse.getResults());

    }   
    
    @Test
	public void testGetUserPreferences() 
  {
		ServiceRequest<FindUserPreferenceQuery> request = new ServiceRequest<>();
		request.setRequester(usmAdmin);
		FindUserPreferenceQuery query = new FindUserPreferenceQuery();
		query.setUserName("vms_super_fra");
		query.setGroupName("Union-VMS");
		request.setBody(query);

		// Execute
		ClientResponse response = manageUserClient.getUserPreferences(ClientResponse.class, request);
		
		GenericType<UserPreferenceResponse> gType = new GenericType<UserPreferenceResponse>() {
	    };
	
		// Verify
		String expectedOptionValue = "fr_FR";
		assertNotNull("Unexpected null response", response);
		UserPreferenceResponse upr = response.getEntity(gType);
		assertNotNull("Unexpected null UserPreferenceResponse", upr);
		assertNotNull("Unexpected null UserPreferenceResponse.results", upr.getResults());
		assertNotNull("Expected 'User' preference not found", getUserPreference(upr.getResults(),expectedOptionValue));
	}
    
    private Preference getUserPreference(List<Preference> cUserPreferences, String expected) {
  	  if (cUserPreferences!=null&&!cUserPreferences.isEmpty()){
  		    for (Preference cUserPreference : cUserPreferences) {
  		      if (cUserPreference!=null&&cUserPreference.getOptionValue()!=null&&expected.equals(new String(cUserPreference.getOptionValue()))) {
  		        return cUserPreference;
  		      }
  		    }
  	  	}
  	    return null;
  	  }
	 	
}
