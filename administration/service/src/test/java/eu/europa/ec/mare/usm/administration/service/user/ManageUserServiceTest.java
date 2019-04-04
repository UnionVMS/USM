package eu.europa.ec.mare.usm.administration.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.mail.MessagingException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.mare.usm.administration.common.DateParser;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformation;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformationResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.NotificationQuery;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ResetPasswordQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import java.util.Date;

/**
 * Unit test for the EJB implementation of the ManageUserService
 */
@RunWith(Arquillian.class)
public class ManageUserServiceTest extends DeploymentFactory {
  private static final String USM_ADMIN = "usm_admin";
  private static final String POLICY_SUBJECT = "Password";

  @EJB
  ManageUserService testSubject;

  @EJB
  ViewUsersService viewUser;

  @EJB
  private DefinitionService policyService;
  
  private PolicyDefinition savedDefinition;

  public ManageUserServiceTest() {
  }

  @Before
  public void setUp() 
  throws IOException 
  {
    savedDefinition = policyService.getDefinition(POLICY_SUBJECT);

    InputStream is = getClass().getResourceAsStream("/password.properties");
    Properties props = new Properties();
    props.load(is);
    
    ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(new PolicyDefinition());
    request.getBody().setSubject(POLICY_SUBJECT);
    request.getBody().setProperties(props);
    policyService.setDefinition(request);
  }
  
  @After
  public void tearDown() 
  {
    ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(savedDefinition);

    policyService.setDefinition(request);
  }

  
  @Test
  public void testCreateUser() 
  {
    // Setup
    ServiceRequest<UserAccount> userRequest = createRequest("createUser");

    // Execute
    UserAccount result = testSubject.createUser(userRequest);

    // Verify
    assertNotNull("createUser failed", result);
    userRequest.setBody(result);
    assertUserExists(userRequest);
  }

  @Test
  public void testCreateUserInvalidDates() 
  {
    // Setup
    ServiceRequest<UserAccount> userRequest = createRequest("createUser");

    // Execute
    Date fr = new Date(System.currentTimeMillis() + 1399350);
    Date to = new Date(System.currentTimeMillis() - 1399350);
    userRequest.getBody().setActiveFrom(fr);
    userRequest.getBody().setActiveTo(to);
    
    try {
      testSubject.createUser(userRequest);
      fail("User created with invalid dates");
    } catch (Exception e) {
      System.out.println("Caught expected " + e.getCause().getMessage());
    }
  }

  @Test
  public void testCreateUserChangedStatus() 
  {
    // Setup
    ServiceRequest<UserAccount> userRequest = createRequest("createUser");
    userRequest.getBody().setStatus("D");

    // Execute
    UserAccount result= testSubject.createUser(userRequest);

    // Verify
    assertNotNull("createUser failed", result);
    userRequest.setBody(result);
    assertUserExists(userRequest);
  }

  @Test
  public void testUpdateUser() {
    // Set-up
    ServiceRequest<UserAccount> userRequest = createRequest("updateUser");
    UserAccount result = testSubject.createUser(userRequest);
    assertNotNull("createUser failed", result);

    // Execute
    Person person=new Person();
    result.setPerson(person);
    person.setFirstName("firstName_updated");
    person.setLastName("secondName_updated");
    person.setEmail("test@updated.com");
    userRequest.setBody(result);
    
    testSubject.updateUser(userRequest);

    UserAccount check = assertUserExists(userRequest);
    assertEquals("Unexpected firstName value", result.getPerson().getFirstName(),
            check.getPerson().getFirstName());
  }

  
  @Test
  public void testUpdateUserInvalidDates() {
    // Set-up
    ServiceRequest<UserAccount> userRequest = createRequest("updateUser");
    UserAccount result = testSubject.createUser(userRequest);
    assertNotNull("createUser failed", result);

    // Execute
    Date fr = new Date(System.currentTimeMillis() + 1399350);
    Date to = new Date(System.currentTimeMillis() - 1399350);
    userRequest.getBody().setActiveFrom(fr);
    userRequest.getBody().setActiveTo(to);
    
    try {
    testSubject.updateUser(userRequest);
      fail("User updated with invalid dates");
    } catch (Exception e) {
      System.out.println("Caught expected " + e.getCause().getMessage());
    }
  }

  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordViolatePolicy() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);

    // Execute
    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(new ChangePassword());
    request.getBody().setNewPassword("pwd");
    request.getBody().setUserName(setup.getBody().getUserName());
    
    try {
      testSubject.changePassword(request);
      fail("Failed to trigger IllegalArgumentException");
    } catch (EJBException | IllegalArgumentException exc) {
      System.out.println("Triggered expected exception: " + exc.getMessage());
    }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordNoChange() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);

    ServiceRequest<ChangePassword> request = createPasswordRequest(USM_ADMIN,setup.getBody().getUserName(),
    		null,"p@$3W0Rd");
    testSubject.changePassword(request);
    
    try {
      testSubject.changePassword(request);
      fail("Failed to trigger IllegalArgumentException");
    } catch (EJBException | IllegalArgumentException exc) {
      System.out.println("Triggered expected exception: " + exc.getMessage());
    }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePassword() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,setup.getBody().getUserName(),
    		null,"p@$3W0Rd"));
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordTenTimes() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);

    // Execute
    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
    request.setRequester(USM_ADMIN);
    request.setBody(new ChangePassword());
    request.getBody().setUserName(setup.getBody().getUserName());

    for (int i = 0; i <10 ; i++) {
      request.getBody().setNewPassword("p@$3W0Rd" + i);
      testSubject.changePassword(request);
    }
    
    try {
      request.getBody().setNewPassword("p@$3W0Rd" + 8);
      testSubject.changePassword(request);
      fail("Failed to trigger IllegalArgumentException");
    } catch (EJBException | IllegalArgumentException exc) {
      System.out.println("Triggered expected exception: " + exc.getMessage());
    }
    
  }
  
  
  private UserAccount assertUserExists(ServiceRequest<UserAccount> userRequest) 
  throws RuntimeException 
  {
    // Verify
    GetUserQuery query = new GetUserQuery();
    query.setUserName(userRequest.getBody().getUserName());
    ServiceRequest<GetUserQuery> request = new ServiceRequest<>();
    request.setRequester(userRequest.getRequester());
    request.setBody(query);
    UserAccount check = viewUser.getUser(request);
    assertNotNull("User does not exist", check);
    
    return check;
  }

  private ServiceRequest<UserAccount> createRequest(String operation) 
  {
    ServiceRequest<UserAccount> request = new ServiceRequest<>();
    UserAccount user = new UserAccount();
    request.setRequester(USM_ADMIN);

    user.setUserName("test:" + operation + System.currentTimeMillis());
    user.setStatus("E");
    
    Organisation org=new Organisation();
    user.setOrganisation(org);
    org.setName("DG-MARE");

    Person person=new Person();
    user.setPerson(person);
    person.setFirstName("firstName1");
    person.setLastName("lastName1");
    person.setEmail("blanchedouglas@email.com");
    user.setNotes("testing status data");

    user.setActiveFrom(DateParser.parseDate("activeFrom", "2015-01-01T01:01:01.001+0000"));
    user.setActiveTo(DateParser.parseDate("activeTo", "2020-12-31T23:59:59.059+0000"));
    request.setBody(user);
    return request;
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUser() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    
    // Set-up
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd","p@$3W0Rd2");
    
    // Execute
    testSubject.changePassword(request);
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUserWithWrongCurrentPassword() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    // Set-up
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd2","p@$3W0Rd3");
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUserWithEmptyCurrentPassword() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    // Set-up
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		null,"p@$3W0Rd3");
    
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByDisabledUser() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    setup.getBody().setStatus("D");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    // Set-up
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd","p@$3W0Rd2");
    
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUserOmittingUppercaseChars() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    
    // Set-up a new password omitting upper case letters
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd","p@$3w0rd2");
    
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUserOmittingLowercaseChars() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    
    // Set-up a new password omitting lower case letters
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd","P@$3W0RD2");
    
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }
  
  /**
   * Tests the changePassword operation.
   */
  @Test
  public void testChangePasswordByUserOmittingNumericChars() 
  {
    // Set-up
    ServiceRequest<UserAccount> setup = createRequest("changePassword");
    UserAccount result = testSubject.createUser(setup);
    assertNotNull("createUser failed", result);
    String userName = result.getUserName();

    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
    
    // Set-up a new password omitting numeric chars
    ServiceRequest<ChangePassword> request = createPasswordRequest(userName,userName,
    		"p@$3W0Rd","P@$sWeRDs");
    
    // Execute
    try {
    	testSubject.changePassword(request);
        fail("Failed to trigger IllegalArgumentException");
      } catch (EJBException | IllegalArgumentException exc) {
        System.out.println("Triggered expected exception: " + exc.getMessage());
      }
  }

  private ServiceRequest<ChangePassword> createPasswordRequest(String requester,String userName, 
		  String currentPassword, String newPassword) {
	    ServiceRequest<ChangePassword> request = new ServiceRequest<>();
	    request.setRequester(requester);
	    request.setBody(new ChangePassword());
	    request.getBody().setCurrentPassword(currentPassword);
	    request.getBody().setNewPassword(newPassword);
	    request.getBody().setUserName(userName);
	    return request;
	}
  
  /**
   * Tests the updateSecurityQuestion operation.
   */
  @Test
  public void testGetChallengeInformation() 
  {
    
    ServiceRequest<String> request = new ServiceRequest<String>();
    request.setRequester("vms_admin_com");
    request.setBody("vms_admin_com");    
    
    // Execute

    ChallengeInformationResponse response = testSubject.getChallengeInformation(request);
    assertNotNull("get challenge failed", response.getResults());
  }  
  
  /**
   * Tests the updateSecurityQuestion operation.
   */
  @Test
  public void testSetChallengeInformation() 
  {
    
    ServiceRequest<ChallengeInformationResponse> request = new ServiceRequest<ChallengeInformationResponse>();
    request.setRequester("vms_admin_com");
    
    ChallengeInformationResponse challengeInformationResponse = new ChallengeInformationResponse();    
    List<ChallengeInformation> challengeInformations = new ArrayList<ChallengeInformation>(); 
    ChallengeInformation challengeInformation = new ChallengeInformation();
    challengeInformation.setChallenge("Name of first pet updated!!");
    challengeInformation.setResponse("Tartampion updated!!");
    challengeInformations.add(challengeInformation);
    challengeInformationResponse.setResults(challengeInformations);
    challengeInformationResponse.setUserPassword("password");
    
    
    request.setBody(challengeInformationResponse);    
    
    // Execute

    ChallengeInformationResponse response = testSubject.setChallengeInformation(request, "vms_admin_com");
    assertNotNull("set challenge failed", response.getResults());
    assertNotNull("first challenge id should not be null", response.getResults().get(0).getChallengeId());  

  }
  
  /**
   * Tests the reset password operation.
   */
	@Test
	public void testResetPassword() {
		// create a new user
	    ServiceRequest<UserAccount> setup = createRequest("changePassword");
	    UserAccount result = testSubject.createUser(setup);
	    assertNotNull("createUser failed", result);
	    String userName = result.getUserName();
	    
	    // set password for the new user
	    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
		
	    // set challenge information for the new user
	    ServiceRequest<ChallengeInformationResponse> ciRequest = new ServiceRequest<ChallengeInformationResponse>();
	    ciRequest.setRequester(userName);
	    ChallengeInformationResponse challengeInformationResponse = new ChallengeInformationResponse();    
	    List<ChallengeInformation> challengeInformations = new ArrayList<ChallengeInformation>(); 
	    ChallengeInformation challengeInformation = new ChallengeInformation();
	    challengeInformation.setChallenge("Name of first pet");
	    challengeInformation.setResponse("Scooby-Doo");
	    challengeInformations.add(challengeInformation);
	    challengeInformationResponse.setResults(challengeInformations);
	    challengeInformationResponse.setUserPassword("p@$3W0Rd");
	    ciRequest.setBody(challengeInformationResponse);    
	    
	    ChallengeInformationResponse response = testSubject.setChallengeInformation(ciRequest, userName);
		
		// Set-up
		ServiceRequest<ResetPasswordQuery> request = new ServiceRequest<>();
		request.setRequester(userName);
		List<ChallengeInformation> list = new ArrayList<>();
		
		ChallengeInformation challengeOne = new ChallengeInformation();
		challengeOne.setChallenge(response.getResults().get(0).getChallenge());
		challengeOne.setResponse(response.getResults().get(0).getResponse());
		list.add(challengeOne);
		
		ResetPasswordQuery query = new ResetPasswordQuery();
		query.setUserName(userName);
		query.setChallenges(list);
		query.setPassword("Password123!@");
		request.setBody(query);

		testSubject.resetPassword(request);
	}
	
	/**
	 * Tests the reset password operation providing wrong answers.
	 */
	@Test
	public void testResetPasswordProvidingWrongAnswers() {
		// create a new user
	    ServiceRequest<UserAccount> setup = createRequest("changePassword");
	    UserAccount result = testSubject.createUser(setup);
	    assertNotNull("createUser failed", result);
	    String userName = result.getUserName();
	    
	    // set password for the new user
	    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
		
	    // set challenge information for the new user
	    ServiceRequest<ChallengeInformationResponse> ciRequest = new ServiceRequest<ChallengeInformationResponse>();
	    ciRequest.setRequester(userName);
	    ChallengeInformationResponse challengeInformationResponse = new ChallengeInformationResponse();    
	    List<ChallengeInformation> challengeInformations = new ArrayList<ChallengeInformation>(); 
	    ChallengeInformation challengeInformation = new ChallengeInformation();
	    challengeInformation.setChallenge("Name of first pet");
	    challengeInformation.setResponse("Scooby-Doo");
	    challengeInformations.add(challengeInformation);
	    challengeInformationResponse.setResults(challengeInformations);
	    challengeInformationResponse.setUserPassword("p@$3W0Rd");
	    ciRequest.setBody(challengeInformationResponse);    
	    
	    ChallengeInformationResponse response = testSubject.setChallengeInformation(ciRequest, userName);
		
		// Set-up
		ServiceRequest<ResetPasswordQuery> request = new ServiceRequest<>();
		request.setRequester(userName);
		List<ChallengeInformation> list = new ArrayList<>();

		ChallengeInformation challengeOne = new ChallengeInformation();
		challengeOne.setChallenge(response.getResults().get(0).getChallenge());
		// set wrong response
		challengeOne.setResponse("Scooby-Scooby-Doo");
		list.add(challengeOne);

		ResetPasswordQuery query = new ResetPasswordQuery();
		query.setUserName(userName);
		query.setChallenges(list);
		query.setPassword("Password123!@");
		request.setBody(query);

		try {
			testSubject.resetPassword(request);
	        fail("Failed to trigger IllegalArgumentException");
	      } catch (EJBException | IllegalArgumentException exc) {
	        System.out.println("Triggered expected exception: " + exc.getMessage());
	      }
	}
	
	/**
	 * Tests the reset password operation performed by a disabled user
	 */
	@Test
	public void testResetPasswordByDisabledUser() {
		// create a new user
	    ServiceRequest<UserAccount> setup = createRequest("changePassword");
	    UserAccount result = testSubject.createUser(setup);
	    assertNotNull("createUser failed", result);
	    String userName = result.getUserName();
	    
	    // set password for the new user
	    testSubject.changePassword(createPasswordRequest(USM_ADMIN,userName,null,"p@$3W0Rd"));
		
	    // set challenge information for the new user
	    ServiceRequest<ChallengeInformationResponse> ciRequest = new ServiceRequest<ChallengeInformationResponse>();
	    ciRequest.setRequester(userName);
	    ChallengeInformationResponse challengeInformationResponse = new ChallengeInformationResponse();    
	    List<ChallengeInformation> challengeInformations = new ArrayList<ChallengeInformation>(); 
	    ChallengeInformation challengeInformation = new ChallengeInformation();
	    challengeInformation.setChallenge("Name of first pet");
	    challengeInformation.setResponse("Scooby-Doo");
	    challengeInformations.add(challengeInformation);
	    challengeInformationResponse.setResults(challengeInformations);
	    challengeInformationResponse.setUserPassword("p@$3W0Rd");
	    ciRequest.setBody(challengeInformationResponse);    
	    
	    ChallengeInformationResponse response = testSubject.setChallengeInformation(ciRequest, userName);

	    // make sure user is disabled
	    result.setStatus("D");
	    setup.setBody(result);
	    testSubject.updateUser(setup);
	    
		// Set-up
		ServiceRequest<ResetPasswordQuery> request = new ServiceRequest<>();
		request.setRequester(userName);
		List<ChallengeInformation> list = new ArrayList<>();

		ChallengeInformation challengeOne = new ChallengeInformation();
		challengeOne.setChallenge(response.getResults().get(0).getChallenge());
		challengeOne.setResponse(response.getResults().get(0).getResponse());
		list.add(challengeOne);

		ResetPasswordQuery query = new ResetPasswordQuery();
		query.setUserName(userName);
		query.setChallenges(list);
		query.setPassword("Password123!@");
		request.setBody(query);

		try {
			testSubject.resetPassword(request);
	        fail("Failed to trigger UnauthorisedException");
	      } catch (EJBException | UnauthorisedException exc) {
	        System.out.println("Triggered expected exception: " + exc.getMessage());
	      }
	}
	
	/**
	 * Tests the reset password and notification operation.
	 */
	@Test
	public void testResetPasswordAndNotify() 
  {
    // Setup
    ServiceRequest<UserAccount> userRequest = createRequest("createUser");
    userRequest.getBody().getPerson().setEmail(getProperties().getProperty("notification.sender"));

    UserAccount result = testSubject.createUser(userRequest);
    String userName = result.getUserName();

    // Execute
    ServiceRequest<NotificationQuery> request = new ServiceRequest<>();
    request.setRequester(userName);
    NotificationQuery query = new NotificationQuery();
    query.setUserName(userName);
    request.setBody(query);
    
    try {
      testSubject.resetPasswordAndNotify(request);
	    } catch (EJBException exc) {
      if (exc.getCause() instanceof RuntimeException && 
          exc.getCause().getCause() instanceof MessagingException) {
        fail("SMTP Session not properly configured or not operational: " + 
             exc.getCause().getCause().getMessage());
      } else {
        throw exc;
      }
    }
  }
	
	/**
	 * Tests the reset password and notification operation performed 
   * by a disabled user.
	 */
	@Test
	public void testResetPasswordAndNotifyByDisabledUser() 
  {
    // Setup
    ServiceRequest<UserAccount> userRequest = createRequest("createUser");
    userRequest.getBody().setStatus("D");
    userRequest.getBody().getPerson().setEmail(getProperties().getProperty("notification.sender"));
    UserAccount result = testSubject.createUser(userRequest);
    String userName = result.getUserName();
    
    // Execute
    ServiceRequest<NotificationQuery> request = new ServiceRequest<>();
    request.setRequester(userName);
    NotificationQuery query = new NotificationQuery();
    query.setUserName(userName);
    request.setBody(query);
    try {
      testSubject.resetPasswordAndNotify(request);
      fail("Failed to trigger Unauthorised exception");
    } catch (EJBException exc) {
      if (exc.getCause() instanceof UnauthorisedException) {
        System.out.println("Triggered expected exception: " + 
                            exc.getCause().getMessage());
      } else {
        fail("Failed to trigger Unauthorised exception");
      }
    }
	}
	
  private Properties getProperties() 
  {
    String PROPERTIES = "/notification.properties";
    Properties ret = new Properties();

    InputStream is = getClass().getResourceAsStream(PROPERTIES);

    try {
      if (is == null) {
        throw new IOException("Resource " + PROPERTIES + " not found");
      }
      ret.load(is);
    } catch (IOException exc) {
      fail("Failed to load notification.properties: " + exc.getMessage());
    }

    return ret;
  }
  
}
