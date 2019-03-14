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

