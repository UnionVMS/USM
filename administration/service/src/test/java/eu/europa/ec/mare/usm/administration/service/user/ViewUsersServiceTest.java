package eu.europa.ec.mare.usm.administration.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit-test for the InformationService 
 */
@RunWith(Arquillian.class)
public class ViewUsersServiceTest extends DeploymentFactory {
  @EJB
  ViewUsersService testSubject;

  /**
   * Creates a new instance
   */
  public ViewUsersServiceTest() {
  }
  
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
  public void testProvidingNameOrgFindUsers() 
 {

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
		assertEquals("Unexpected 'name' value", expectedFirstName,
				getFirstName(results, expectedFirstName));
		assertEquals("Unexpected 'organisation' value", expectedOrganisation,
				getOrganisation(results, expectedOrganisation));
	}
  
  private String getUserName(List<UserAccount> cUsers, String expected){ 
	  for(UserAccount user: cUsers){
		  if(user.getUserName().equals(expected)) return expected;
	  }
	  
	  return null;
  }
  
  private String getFirstName(List<UserAccount> cUsers, String expected){ 
	  for(UserAccount user: cUsers){
		  if(user.getPerson().getFirstName().equals(expected)) return expected;
	  }
	  
	  return null;
  }
  
  private String getNation(List<UserAccount> cUsers, String expected){ 
	  for(UserAccount user: cUsers){
		  if(user.getOrganisation().getNation().equals(expected)) return expected;
	  }
	  
	  return null;
  }
  
  private String getOrganisation(List<UserAccount> cUsers, String expected){ 
	  for(UserAccount user: cUsers){
		  if(user.getOrganisation().getName().equals(expected)) return expected;
	  }
	  
	  return null;
  }
  
  /**
   * Tests the get user method
   */
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

  /**
   * Tests the getUsersNames method
   */
  @Test
  public void testGetUsersNames() 
  {
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

