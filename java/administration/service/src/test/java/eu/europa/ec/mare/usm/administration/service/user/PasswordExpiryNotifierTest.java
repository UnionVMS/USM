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