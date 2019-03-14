package eu.europa.ec.mare.usm.information.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.DataSet;
import eu.europa.ec.mare.usm.information.domain.DataSetFilter;
import eu.europa.ec.mare.usm.information.domain.EndPoint;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.Preference;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.domain.UserPreference;

import java.util.List;
import static org.junit.Assert.fail;

/**
 * Unit-test for the InformationService 
 */
@RunWith(Arquillian.class)
public class InformationServiceTest extends DeploymentFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(InformationServiceTest.class);
  private static final String APP_QUOTA = "Quota";
  private static final String OPT_USER_LOCALE = "userLocale";
  private static final String OPT_ROWS_PER_PAGE = "rowsPerPage";
  private static final String OPT_LEVEL_CODE = "levelCode";
  private static final String QUOTA_USR_GRC = "quota_usr_grc";
  private static final String QUOTA_USR_FRA = "quota_usr_fra";

  @EJB
  InformationService testSubject;

  /**
   * Creates a new instance
   */
  public InformationServiceTest() {
  }
  

  
  @Test
  @InSequence(1)
  public void testCreateUserPreference(){
	UserPreference userPreference = new UserPreference();
	userPreference.setApplicationName("Quota");
	userPreference.setRoleName("Super User");
	userPreference.setScopeName(null);
	userPreference.setUserName("vms_super_com");
	userPreference.setOptionName("userLocale");
	userPreference.setOptionValue("optionValue".getBytes());
	testSubject.createUserPreference(userPreference );
	
	assertNotNull("Unexpected null result", new String(testSubject.getUserPreference(userPreference).getOptionValue()));
	
  }
  

  
  @Test
  @InSequence(2)
  public void testUpdateUserPreference(){
	UserPreference userPreference = new UserPreference();
	userPreference.setApplicationName("Quota");
	userPreference.setRoleName("Super User");
	userPreference.setScopeName("");
	userPreference.setUserName("vms_super_com");
	userPreference.setOptionName("userLocale");
	userPreference.setOptionValue("optionValueUpdated".getBytes());
	testSubject.updateUserPreference(userPreference );
	
	assertEquals("Unexpected not equal objects",
			new String(testSubject.getUserPreference(userPreference).getOptionValue()),  new String(userPreference.getOptionValue()));
	
  }
  
  
  
  @Test
  @InSequence(3)
  public void testDeleteUserPreference(){
	UserPreference userPreference = new UserPreference();
	userPreference.setApplicationName("Quota");
	userPreference.setRoleName("Super User");
	userPreference.setScopeName("");
	userPreference.setUserName("vms_super_com");
	userPreference.setOptionName("userLocale");
	
	testSubject.deleteUserPreference( userPreference );
	
	assertNull("Unexpected not null result", testSubject.getUserPreference(userPreference).getOptionValue());
	
  }
  
  
  @Test
  @InSequence(4)
  public void testCreateDataSet(){
	DataSet dataSet = new DataSet();
	dataSet.setApplicationName("Quota");
	dataSet.setName("test_dataset");
	dataSet.setCategory("category");
	dataSet.setDescription("description");
	dataSet.setDiscriminator("discriminator");

	testSubject.createDataSet(dataSet);
	
	assertNotNull("Unexpected null result", testSubject.getDataSet(dataSet.getName(), dataSet.getApplicationName()));
	
  }
  
  @Test
  @InSequence(5)
  public void testFindDataSet(){
	DataSetFilter dataSetFilter = new DataSetFilter();
	dataSetFilter.setApplicationName("Quota");
	dataSetFilter.setName("test_dataset");
	dataSetFilter.setCategory("category");
	dataSetFilter.setDiscriminator("discriminator");
	
	assertNotNull("Unexpected null result", testSubject.getDataSets(dataSetFilter));
	
  }
  
  @Test
  @InSequence(6)
  public void testUpdateDataSet(){
	DataSet dataSet = new DataSet();
	dataSet.setApplicationName("Quota");
	dataSet.setName("test_dataset");
	dataSet.setCategory("category");
	dataSet.setDescription("descriptionUpdated");
	dataSet.setDiscriminator("discriminatorUpdated");

	testSubject.updateDataSet(dataSet);
	
	assertEquals("Unexpected not equal objects", testSubject.getDataSet(dataSet.getName(), dataSet.getApplicationName()).getDiscriminator(), dataSet.getDiscriminator());
	
  }
  
  @Test
  @InSequence(7)
  public void testDeleteDataSet(){
		DataSet dataSet = new DataSet();
		dataSet.setApplicationName("Quota");
		dataSet.setName("test_dataset");
		dataSet.setCategory("category");
		dataSet.setDescription("description");
		dataSet.setDiscriminator("discriminator");
	
	testSubject.deleteDataSet(dataSet);
	
	assertNull("Unexpected not null result", testSubject.getDataSet(dataSet.getName(), dataSet.getApplicationName()));
	
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
          LOGGER.info("after update item = "+item);
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
                         "en_GB",new String(item.getOptionValue()));
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
 // @Test
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
    List<Organisation> result = testSubject.findOrganisations("FRA");
    
    // Verify
    assertNotNull("Unexpected null result", result);
    assertFalse("Unexpected empty endpoints result", result.get(0).getEndPoints().isEmpty());
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
    assertEquals("Unexpected null result.nation", expected, result.getNation());
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
    assertEquals("Unexpected null result.nation", expected, result.getNation());
    assertTrue("Unexpected disabled result", result.isEnabled());
    assertNotNull("Unexpected null result.endPoints", result.getEndPoints());
    assertFalse("Unexpected empty result.endPoints", 
                result.getEndPoints().isEmpty());
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
    assertEquals("Unexpected null result.nation", expected, result.getNation());
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
    assertEquals("Unexpected null result.nation", expected, 
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

