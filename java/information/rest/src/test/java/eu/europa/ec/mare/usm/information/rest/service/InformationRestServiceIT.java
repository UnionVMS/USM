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

import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.EndPoint;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.Preference;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.service.InformationService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the (User) Information REST Service
 */
public class InformationRestServiceIT {
  private static final String APP_QUOTA = "Quota";
  private static final String OPT_USER_LOCALE = "userLocale";
  private static final String OPT_ROWS_PER_PAGE = "rowsPerPage";
  private static final String OPT_LEVEL_CODE = "levelCode";
  private static final String QUOTA_USR_GRC = "quota_usr_grc";
  private static final String QUOTA_USR_FRA = "quota_usr_fra";
  private static final String URL = "http://localhost:8080/usm-information/rest/";
  private InformationRestClient testSubject = null;
  private final String endPoint;
  
  /**
   * Creates a new instance.
   * 
   * @throws IOException in case the test.properties can not be accessed
   */
  public InformationRestServiceIT() 
  throws IOException 
  {
    InputStream is = getClass().getResourceAsStream("/test.properties");
    Properties props = new Properties();
    props.load(is);
    endPoint = props.getProperty("rest.endpoint", URL);
  }
  
  @Before
  public void setUp() 
  {
    testSubject = new InformationRestClient(endPoint);
  }
  
  /**
   * Tests the getUserContext method.
   */
  @Test
  public void testGetUserContext()
  {
    // Set-up
    UserContextQuery query = new UserContextQuery();
    query.setApplicationName(APP_QUOTA);
    query.setUserName(QUOTA_USR_FRA);

    // Execute
    UserContext result = testSubject.getUserContext(query);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected null result.applicationName",
                 query.getApplicationName(),  result.getApplicationName());
    assertEquals("Unexpected null result.userName", 
                 query.getUserName(),  result.getUserName());
    assertNotNull("Unexpected null ContextSet", result.getContextSet());
  }
  
  /**
   * Tests the getUserContext method.
   */
  @Test
  public void testGetUserContextWithPreferences() 
  {
    // Set-up
    UserContextQuery query = new UserContextQuery();
    query.setApplicationName(APP_QUOTA);
    query.setUserName(QUOTA_USR_FRA);

    // Execute
    UserContext result = testSubject.getUserContext(query);
    
    // Verify
    String expectedLevelCodeValue = "FRA";
    String expectedRowPerPageValue = "10";
    String expectedRoleName = "MS Quota User";
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected null result.applicationName",
                 query.getApplicationName(),  result.getApplicationName());
    assertEquals("Unexpected null result.userName", 
                 query.getUserName(),  result.getUserName());
    assertNotNull("Unexpected null result.contextSet", result.getContextSet());
    assertNotNull("Unexpected null result.contextSet.contexts", 
                  result.getContextSet().getContexts());
    
    boolean foudExpectedRole = false;
    for (Context ct : result.getContextSet().getContexts()) {
      assertNotNull("Unexpected null context.role", ct.getRole());
      assertNotNull("Unexpected null context.role.name", ct.getRole().getRoleName());
      
      if (expectedRoleName.equals(ct.getRole().getRoleName())) {
        foudExpectedRole = true;
        assertNotNull("Unexpected null context.preferences", ct.getPreferences());
        assertNotNull("Unexpected null context.preferences.preferences", 
                      ct.getPreferences().getPreferences());
        assertFalse("Unexpected empty context.preferences.preferences", 
                    ct.getPreferences().getPreferences().isEmpty());
        boolean foundLevelCode = false;
        boolean foundRowsPerPage = false;
        for (Preference item : ct.getPreferences().getPreferences()) {
          if (OPT_LEVEL_CODE.equals(item.getOptionName())){
            foundLevelCode = true;
            assertEquals("Unexpected 'levelCode' preference value", 
                         expectedLevelCodeValue, new String(item.getOptionValue()));
          } else if (OPT_ROWS_PER_PAGE.equals(item.getOptionName())){
            foundRowsPerPage = true;
            assertEquals("Unexpected 'rowsPerPage' preference value", 
                         expectedRowPerPageValue, new String(item.getOptionValue()));
          }
        }
        if (!foundLevelCode) {
          fail("Expected 'levelCode' preference not found in UserContext");
        }
        if (!foundRowsPerPage) {
          fail("Expected 'rowsPerPage' preference not found in UserContext");
        }
      }
    }
    if (!foudExpectedRole) {
      fail("Role " + expectedRoleName + " not found in UserContext");
    }
  }
  
  /**
   * Tests the updateUserContext method.
   */
  @Test
  public void testUpdateUserContextWithPreferences() 
  {
    // Set-up
    UserContextQuery query = new UserContextQuery();
    query.setApplicationName(APP_QUOTA);
    query.setUserName(QUOTA_USR_GRC);
    UserContext request = testSubject.getUserContext(query);
    assertNotNull("Unexpected null UserContext", request);
    assertNotNull("Unexpected null ContextSet", request.getContextSet());

    // Execute
    String expectedRoleName = "MS Quota User";
    boolean foudExpectedRole = false;
    for (Context ct : request.getContextSet().getContexts()) {
      if (expectedRoleName.equals(ct.getRole().getRoleName())) {
        foudExpectedRole = true;
        assertNotNull("Unexpected null context.preferences", ct.getPreferences());
        assertNotNull("Unexpected null context.preferences.preferences", 
                      ct.getPreferences().getPreferences());
        for (Preference item : ct.getPreferences().getPreferences()) {
          if (OPT_LEVEL_CODE.equals(item.getOptionName())){
            item.setOptionValue("*");
          } else if (OPT_ROWS_PER_PAGE.equals(item.getOptionName())){
            item.setOptionValue("7");
          } else if (OPT_USER_LOCALE.equals(item.getOptionName())){
            item.setOptionValue("el_GR");
          }
        }
      } 
    }
    if (!foudExpectedRole) {
      fail("Role " + expectedRoleName + " not found in UserContext");
    }
    testSubject.updateUserPreferences(request);
    
    // Verify
    UserContext check = testSubject.getUserContext(query);
    assertEquals("Unexpected null result.applicationName",
                 query.getApplicationName(),  check.getApplicationName());
    assertEquals("Unexpected null result.userName", 
                 query.getUserName(),  check.getUserName());
    foudExpectedRole = false;
    for (Context ct : check.getContextSet().getContexts()) {
      assertNotNull("Unexpected null context.role", ct.getRole());
      assertNotNull("Unexpected null context.role.name", ct.getRole().getRoleName());
      
      if (expectedRoleName.equals(ct.getRole().getRoleName())) {
        foudExpectedRole = true;
        assertNotNull("Unexpected null context.preferences", ct.getPreferences());
        assertNotNull("Unexpected null context.preferences.preferences", 
                      ct.getPreferences().getPreferences());
        for (Preference item : ct.getPreferences().getPreferences()) {
          if (OPT_LEVEL_CODE.equals(item.getOptionName())){
            assertEquals("Unexpected 'levelCode' preference value", 
                         "*", new String(item.getOptionValue()));
          } else if (OPT_ROWS_PER_PAGE.equals(item.getOptionName())){
            assertEquals("Unexpected 'rowsPerPage' preference value", 
                         "7", new String(item.getOptionValue()));
          } else if (OPT_USER_LOCALE.equals(item.getOptionName())){
            assertEquals("Unxpected 'userLocale' preference value", 
                         "el_GR", new String(item.getOptionValue()));
          }
        }
      }
    }
    if (!foudExpectedRole) {
      fail("Role " + expectedRoleName + " not found in UserContext");
    }
  }
  
  /**
   * Tests the updateUserContext method.
   */
  @Test
  public void testUpdateUserContextWithoutPreferences() 
  {
    // Set-up
    UserContextQuery query = new UserContextQuery();
    query.setApplicationName(APP_QUOTA);
    query.setUserName(QUOTA_USR_GRC);
    UserContext request = testSubject.getUserContext(query);
    assertNotNull("Unexpected null UserContext", request);
    assertNotNull("Unexpected null ContextSet", request.getContextSet());

    // Execute
    for (Context ctx : request.getContextSet().getContexts()) {
      ctx.setPreferences(null);
    }
    testSubject.updateUserPreferences(request);
    
    // Verify
    UserContext check = testSubject.getUserContext(query);
    assertNotNull("Unexpected null UserContext", check);
    assertNotNull("Unexpected null ContextSet", check.getContextSet());
    assertEquals("Unexpected null result.applicationName",
                 query.getApplicationName(),  check.getApplicationName());
    assertEquals("Unexpected null result.userName", 
                 query.getUserName(),  check.getUserName());
    
    String expectedRoleName = "MS Quota User";
    boolean foudExpectedRole = false;
    for (Context ct : check.getContextSet().getContexts()) {
      assertNotNull("Unexpected null context.role", ct.getRole());
      assertNotNull("Unexpected null context.role.name", ct.getRole().getRoleName());
      
      if (expectedRoleName.equals(ct.getRole().getRoleName())) {
        foudExpectedRole = true;
        assertNotNull("Unexpected null context.preferences", ct.getPreferences());
        assertNotNull("Unexpected null context.preferences.preferences", 
                      ct.getPreferences().getPreferences());
        for (Preference item : ct.getPreferences().getPreferences()) {
          if (OPT_ROWS_PER_PAGE.equals(item.getOptionName())){
            assertEquals("Unexpected 'rowsPerPage' preference value", 
                         "10", new String(item.getOptionValue()));
          } else if (OPT_USER_LOCALE.equals(item.getOptionName())){
            assertEquals("Unexpected 'userLocale' preference value", 
                         "en_GB", new String(item.getOptionValue()));
          }
        }
      }
    }
    if (!foudExpectedRole) {
      fail("Role " + expectedRoleName + " not found in UserContext");
    }
  }
  
  /**
   * Tests the getContactDetails method.
   */
  @Test
  public void testGetContactDetails() 
  {
    // Execute
    ContactDetails result = testSubject.getContactDetails(QUOTA_USR_FRA);
    
    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.email", 
                  "quota_usr_fra@gouv.fr", 
                  result.geteMail());
    assertEquals("Unexpected result.organisationName", 
                 "FRA", result.getOrganisationName());
  }

  /**
   * Tests the getContactDetails method.
   */
  @Test
  public void testGetContactDetailsNoOrganisation() 
  {
    // Execute
    ContactDetails result = testSubject.getContactDetails("orphan");
    
    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.email", "orphan@mail.org", 
                  result.geteMail());
    assertNull("Unexpected non-null result.organisationName", 
                result.getOrganisationName());
  }

  /**
   * Tests the getContactDetails method.
   */
  @Test
  public void testGetContactDetailsNotFound() 
  {
    // Execute
    ContactDetails result = testSubject.getContactDetails("anonymous");
    
    // Verify
    assertNull("Unexpected non-null result", result);
  }

  
  /**
   * Tests the findOrganisations method.
   */
  @Test
  public void testFindOrganisations() 
  {
    // Execute
    List<Organisation> result = testSubject.findOrganisations("EEC");
    
    // Verify
    assertNotNull("Unexpected null result", result);
    assertFalse("Unexpected empty result",  result.isEmpty());
  }
  
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void testGetOrganisationWithEndPoints() 
  {
    // Execute
    Organisation result = testSubject.getOrganisation("FRA");
    
    // Verify
    String expected = "FRA";
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.nation value", expected, result.getNation());
    assertTrue("Unexpected disabled result", result.isEnabled());
    assertNotNull("Unexpected null result.endPoints", result.getEndPoints());
    assertFalse("Unexpected empty result.endPoints", 
                result.getEndPoints().isEmpty());
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void testGetOrganisationWithEndPointChannel() 
  {
    // Execute
    Organisation result = testSubject.getOrganisation("GRC");
    
    // Verify
    String expected = "GRC";
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.nation value", expected, result.getNation());
    assertTrue("Unexpected disabled result", result.isEnabled());
    assertNotNull("Unexpected null result.endPoints", result.getEndPoints());
    assertFalse("Unexpected empty result.endPoints", 
                result.getEndPoints().isEmpty());
    assertNotNull("Unexpected null result.endPoints[0].channels", 
                  result.getEndPoints().get(0).getChannels());
    
    for (EndPoint ep : result.getEndPoints()) {
      if ("FLUX.GRC_backup".equals(ep.getName())) {
      assertNotNull("Unexpected null endPoint[FLUX.GRC_backup].channels", 
                    ep.getChannels());
        assertFalse("Unexpected empty endPoint[FLUX.GRC_backup].channels", 
                    ep.getChannels().isEmpty());
      }
    }
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void testGetOrganisationWithoutEndPoints() 
  {
    // Execute
    Organisation result = testSubject.getOrganisation("DG-MARE");
    
    // Verify
    String expected = "EEC";
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.nation value", expected, result.getNation());
    assertTrue("Unexpected disabled result", result.isEnabled());
    assertNull("Unexpected non-null result.endPoints", result.getEndPoints());
  }

  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void testGetOrganisationWitParent() 
  {
    // Execute
    Organisation result = testSubject.getOrganisation("DG-MARE");
    
    // Verify
    String expected = "EC";
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected result.nation value", expected, 
                 result.getParentOrganisation());
  }
  
  /**
   * Tests the getOrganisation method.
   */
  @Test
  public void testGetOrganisationWitChildren() 
  {
    // Execute
    Organisation result = testSubject.getOrganisation("EC");
    
    // Verify
    String expected = "DG-MARE";
    assertNotNull("Unexpected null result", result);
    assertNotNull("Unexpected null result.childOrganisations", 
                  result.getChildOrganisations());
    assertFalse("Unexpected empty result.childOrganisations", 
                result.getChildOrganisations().isEmpty());

    boolean foundIt = false;
    for (String item : result.getChildOrganisations()) {
      if (expected.equals(item)) {
        foundIt = true;
        break;
      }
    }
    assertTrue("childOrganisation " + expected + " not found", foundIt);
  }
  
  
}