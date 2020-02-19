package eu.europa.ec.mare.usm.administration.rest.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.rest.service.AuthWrapper;
import eu.europa.ec.mare.usm.administration.rest.service.authentication.AuthenticationRestClient;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;

/**
 * Integration test for the exotic UserProfile REST service.
 */
public class UserProfileRestServiceIT extends AuthWrapper {
  private UserRestClient manageUserClient = null;
  private UserProfileRestClient profileClient = null;
  private AuthenticationRestClient  authenticationClient;
  private String usmAdmin;
  

  /**
   * Creates a new instance.
   * 
   * @throws IOException if class-path resource "/test.properties" is not 
   * readable
   */
  public UserProfileRestServiceIT()
  throws IOException 
  {}

  @Before
  public void setUp() 
  {
    manageUserClient = new UserRestClient(endPoint);
    profileClient = new UserProfileRestClient(endPoint);
    authenticationClient = new AuthenticationRestClient(endPoint);
    usmAdmin = login("usm_admin", "password");
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
    profileClient.changePassword(request);
    
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
    
    // 2: administrator sets the test user initial password
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
    profileClient.changePassword(request);
    
    // Verify: test user logs-in with changed password
    login(testCuserAccount.getUserName(), updatedPassword);
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
    
    return response.getJwtoken();
  }

}
