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
package eu.europa.ec.mare.usm.authentication.service;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Load-test for the AuthenticationService configured to authenticate
 * users against the USM database schema.
 */
@RunWith(Arquillian.class)
public class AuthenticationServiceLoadDBTest extends AuthenticationServiceDBTest {
  private static final int ITERATIONS = 32;
  
  /**
   * Creates a new instance
   */
  public AuthenticationServiceLoadDBTest() {
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserPasswordExpired() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserPasswordExpired();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserTriggerLockOut() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserTriggerLockOut();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserSuccess();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureInvalidCredentials() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureInvalidCredentials();
      super.tearDown();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureAccountLocked() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureAccountLocked();
    }
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  @Override
  public void testGetUserChallengeSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testGetUserChallengeSuccess();
    }
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  @Override
  public void testGetUserChallengeFailure() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testGetUserChallengeFailure();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserChallengeResponseSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserChallengeResponseSuccess();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserChallengeResponseFailure() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserChallengeResponseFailure();
    }
  }
  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserSuccessUserEnabled() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserSuccessUserEnabled();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureUserDisabled() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureUserDisabled();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureUserLocked() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureUserLocked();
    }
  }

}