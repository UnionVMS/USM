package eu.europa.ec.mare.usm.administration.service.user;

import eu.europa.ec.mare.usm.administration.common.JdbcTestFixture;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.administration.service.user.impl.PasswordExpiryNotifier;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for PasswordExpiryNotifier
 */
@RunWith(Arquillian.class)
public class PasswordExpiryNotifierTest extends DeploymentFactory {

  @EJB
  PasswordExpiryNotifier testSubject;
  @EJB
  PolicyProvider policyProvider;
  
  @EJB
  JdbcTestFixture jdbcDao;
  
  
  @Before
  public void setUp ()
  {
    jdbcDao.update("update user_t set expiry_notification=null" + 
                   " where user_name='remind_me'");
    jdbcDao.update("update policy_t set value='7'" + 
                   " where subject='Password'" + 
                   " and name='password.renewalReminder'");
    //we must reset the policyProvider to avoid hitting the cache
    policyProvider.reset();
  }
  
  @Test
  public void testFindUsersToNotify()
  {
    // Execute
    List<String> result = testSubject.findUsersToNotify();
    
    // Verify
    assertNotNull("Unexpected null UsersToNotify list", result);
    assertFalse("Unexpected empy UsersToNotify list", result.isEmpty()); 
  } 
  
  @Test
  public void testNotifyUser()
  {
    // Set-up
    List<String> setup = testSubject.findUsersToNotify();
    assertNotNull("Unexpected null UsersToNotify list", setup);
    assertFalse("Unexpected empy UsersToNotify list", setup.isEmpty());

    // Execute
    String userName = setup.get(0);
    testSubject.notifyUser(userName);
    
    // Verify
    List<String> checkList = testSubject.findUsersToNotify();
    assertNotNull("Unexpected null UsersToNotify list", checkList);
    for (String checkItem : checkList) {
      assertNotSame("Unexpected UsersToNotify value", userName, checkItem);
    }
  }
}
