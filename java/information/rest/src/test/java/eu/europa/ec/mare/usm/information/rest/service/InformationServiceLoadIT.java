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
package eu.europa.ec.mare.usm.information.rest.service;

import java.io.IOException;
import org.junit.Test;

/**
 * Load-test for the InformationService 
 */
public class InformationServiceLoadIT extends InformationRestServiceIT {
  private final int iterations = 32;

  /**
   * Creates a new instance
   * 
   * @throws IOException in case the test.properties can not be accessed
   */
  public InformationServiceLoadIT() 
  throws IOException 
  {
  }
  
  /**
   * Tests the getUserContext method.
   */
  @Test
  public void loadTestGetUserContextWithPreferences() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetUserContextWithPreferences();
    }
  }
  
  /**
   * Tests the getUserContext method.
   */
  @Test
  public void loadTestGetUserContext() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetUserContext();
    }
  }
  
  /**
   * Tests the updateUserContext method.
   */
  @Test
  public void loadTestUpdateUserContextWithPreferences() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testUpdateUserContextWithPreferences();
    }
  }

  /**
   * Tests the updateUserContext method.
   */
  @Test
  public void loadTestUpdateUserContextWithoutPreferences() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testUpdateUserContextWithoutPreferences();
    }
  }
  
  /**
   * Tests the getContactDetails method.
   */
  @Test
  public void loadTestGetContactDetails() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetContactDetails();
    }
  }
  
  /**
   * Tests the findOrganisations method.
   */
  @Test
  public void loadTestFindOrganisations() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testFindOrganisations();
    }
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void loadTestGetOrganisationWithEndPoints() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetOrganisationWithEndPoints();
    }
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void loadTestGetOrganisationWithEndPointChannel() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetOrganisationWithEndPointChannel();
    }
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void loadTestGetOrganisationWithoutEndPoints() 
  {
    for (int i = 0 ; i <iterations;i++) {
      testGetOrganisationWithoutEndPoints();
    }
  }
}