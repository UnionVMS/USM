package eu.europa.ec.mare.usm.information.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.EJBException;

import org.junit.Before;
import org.junit.Test;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.domain.deployment.Dataset;
import eu.europa.ec.mare.usm.information.domain.deployment.Feature;
import eu.europa.ec.mare.usm.information.domain.deployment.Option;
import eu.europa.ec.mare.usm.information.service.DeploymentService;

/**
 * Integration-test for the REST implementation of the DeploymentService
 *
 */
public class DeploymentRestServiceIT {
  private static final String UPDATED = "Updated: ";
	private DeploymentService testSubject;
	private final String endPoint;
	private final String serviceRequester;

	/**
	 * Creates a new instance.
	 * 
	 * @throws IOException in case the '/test.properties' class-path resource 
   * cannot be loaded
	 */
	public DeploymentRestServiceIT() 
  throws IOException 
  {
		InputStream is = getClass().getResourceAsStream("/test.properties");
		Properties props = new Properties();
		props.load(is);
		endPoint = props.getProperty("rest.endpoint");
		serviceRequester = props.getProperty("jwtoken.usm_admin");
	}

	@Before
	public void setUp() 
  {
		testSubject = new DeploymentRestClient(endPoint);
	}

	/**
	 * Tests the getDeploymentDescriptor method
	 */
	@Test
	public void testGetDeploymentDescriptor() 
  {
		// Setup
    String appName = "USM";

		// Execute
		Application response = testSubject.getDeploymentDescriptor(appName);

		// Verify
		assertNotNull("Unexpected null response", response);
		assertEquals("Unexpected name", appName, response.getName());
    assertFalse("Unexpected empty feature list", response.getFeature().isEmpty());
    assertTrue("Unexpected non-empty dataset list", response.getDataset().isEmpty());
    assertTrue("Unexpected non-empty option list", response.getOption().isEmpty());
	}

	/**
	 * Tests the deployApplication method
	 */
	@Test
	public void testDeployApplication() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);

    // Execute
    request.setName("Test:testDeployApplication-" +
                              System.currentTimeMillis());
    Feature nf = new Feature();
    nf.setName("Test:testDeployApplication");
    nf.setDescription("Tests the deployApplication method");
    request.getFeature().add(nf);
		testSubject.deployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
		assertEquals("Unexpected name", request.getName(), 
                                    verify.getName());
    assertFalse("Unexpected empty feature list", verify.getFeature().isEmpty());
    assertFalse("Unexpected empty dataset list", verify.getDataset().isEmpty());
    assertFalse("Unexpected empty option list", verify.getOption().isEmpty());
	}

	/**
	 * Tests the deployApplication method
	 */
	@Test
	public void testDeployApplicationWithDuplicates() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);

    // Execute
    request.setName("Test:testDeployApplicationWithDuplicates-" +
                              System.currentTimeMillis());
    Feature nf = new Feature();
    nf.setName("Test:testDeployApplicationWithDuplicates");
    nf.setDescription("Tests the deployApplication method");
    request.getFeature().add(nf);
    request.getFeature().add(nf);
    Option  no = new Option();
    no.setName("Test:testOption");
    no.setDescription("Test options");
    no.setDataType("JSON");
   
    // no.setDefaultValue("{\"Name\"= \"Value\"}");
    
    request.getOption().add(no);
    request.getOption().add(no);
    Dataset  nd = new Dataset();
    nd.setName("Test:testDataset");
    nd.setDescription("Test datasets");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testRedeployApplication");
    request.getDataset().add(nd);
    request.getDataset().add(nd);

    // Execute
    try {
      testSubject.deployApplication(request);
      // Verify
      fail("Expected IllegalArgumentException not reported");
    } catch(EJBException | IllegalArgumentException e) {
      System.out.println("Caught expected exception:" + e.getMessage());
    }    
	}

	/**
	 * Tests the deployApplication method
	 */
	@Test
	public void testDeployMinimalisticApplication() 
  {
		// Setup
    Application request =new Application();
    request.setName("Test:testDeployMinimalisticApplication-" + 
                              System.currentTimeMillis());

    // Execute
		testSubject.deployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
		assertEquals("Unexpected name", request.getName(), 
                                    verify.getName());
    assertTrue("Unexpected non-empty feature list", verify.getFeature().isEmpty());
    assertTrue("Unexpected non-empty dataset list", verify.getDataset().isEmpty());
    assertTrue("Unexpected non-empty option list", verify.getOption().isEmpty());
	}

	/**
	 * Tests the deployApplication method
	 */
	@Test
	public void testDeployFeaturesOnlyApplication() 
  {
		// Setup
    Application request = new Application();
    request.setName("Test:testDeployFeaturesOnlyApplication-" + 
                              System.currentTimeMillis());
    Feature nf = new Feature();
    nf.setName("Test:testDeployFeaturesOnlyApplication");
    nf.setDescription("Tests the deployApplication method");
    request.getFeature().add(nf);

    // Execute
		testSubject.deployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
		assertEquals("Unexpected name", request.getName(), 
                                    verify.getName());
    assertFalse("Unexpected empty feature list", verify.getFeature().isEmpty());
    assertTrue("Unexpected non-empty dataset list", verify.getDataset().isEmpty());
    assertTrue("Unexpected non-empty option list", verify.getOption().isEmpty());
	}

	/**
	 * Tests the undeployApplication method
	 */
	@Test
	public void testUndeployApplication() 
  {
		// Setup
		Application setup = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", setup);
		setup.setName("Test:testUndeployApplication-" + 
                            System.currentTimeMillis());
		testSubject.deployApplication(setup);

    // Execute
		testSubject.undeployApplication(setup.getName());

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(setup.getName());
		assertNull("Unexpected non null response", verify);
	}
  
	/**
	 * Tests the undeployApplication method with an invalid request
	 */
	@Test
	public void testUndeployApplicationNotAllowed() 
  {
		// Setup
		String request = "USM";
		
    // Execute
    try {
      testSubject.undeployApplication(request);
      // Verify
      fail("Expected IllegalArgumentException not reported");
    } catch (EJBException | IllegalArgumentException e) {
      System.out.println("Caught expected exception:" + e.getMessage());
    }

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request);
		assertNotNull("Unexpected null response", verify);
	}
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationRetainingAllDatasets() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		String appName = "Test:testRedeployApplicationAddDetails-" + 
                System.currentTimeMillis(); 
		request.setName(appName);
		testSubject.deployApplication(request);
		
	    Dataset  nd = new Dataset();
	    nd.setName("Test:testDataset");
	    nd.setDescription("Test datasets");
	    nd.setCategory("Test");
	    nd.setDiscriminator("Name=testRedeployApplication");
	    request.getDataset().add(nd);
	    
	    Dataset  ndt = new Dataset();
	    ndt.setName("Test:testDataset2");
	    ndt.setDescription("Test datasets 2");
	    ndt.setCategory("Test");
	    ndt.setDiscriminator("Name=testRedeployApplication");
	    request.getDataset().add(ndt);
		
		testSubject.deployDatasets(request);
		
		Application datasetsVerify = testSubject.getDeploymentDescriptor(appName);
		assertNotNull("Unexpected null response", datasetsVerify);
		assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 datasetsVerify.getDataset().size());

		request = testSubject.getDeploymentDescriptor("Quota");
		request.setName(appName);
		request.setRetainDatasets(true);
		
	    // Execute
		testSubject.redeployApplication(request);
	
		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
	    assertEquals("Unexpected number of Features", 
	                 request.getFeature().size(), 
	                 verify.getFeature().size());
	    assertEquals("Unexpected number of Options", 
	                 request.getOption().size(), 
	                 verify.getOption().size());
	    assertEquals("Unexpected number of Datasets", 
	    			datasetsVerify.getDataset().size(), 
	                 verify.getDataset().size());
	}
	
	@Test
	public void testRedeployApplication() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
    request.setName("Test:testRedeployApplication-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    request.getFeature().remove(0);
    Feature nf = new Feature();
    nf.setName("Test:testRedeployApplication");
    nf.setDescription("Tests the undeployApplication method");
    request.getFeature().add(nf);

    request.getOption().remove(0);
    Option  no = new Option();
    no.setName("Test:testOption");
    no.setDescription("Test options");
    no.setDataType("JSON");
    
    //no.setDefaultValue("{\"Name\"= \"Value\"}");
   
    request.getOption().add(no);

    request.getDataset().remove(0);
    Dataset  nd = new Dataset();
    nd.setName("Test:testDataset");
    nd.setDescription("Test datasets");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testRedeployApplication");
    request.getDataset().add(nd);
    
		testSubject.redeployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationNoChanges() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);
		request.setName("Test:testRedeployApplicationNoChanges-" + 
            System.currentTimeMillis());
    
		testSubject.deployApplication(request);

		// Execute
		testSubject.redeployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationAddDetails() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);
    request.setName("Test:testRedeployApplicationAddDetails-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    Feature nf = new Feature();
    nf.setName("Test:testRedeployApplication");
    nf.setDescription("Tests the undeployApplication method");
    request.getFeature().add(nf);

    Option  no = new Option();
    no.setName("Test:testOption");
    no.setDescription("Test options");
    no.setDataType("JSON");
    
    //no.setDefaultValue("{\"Name\"= \"Value\"}");
    
    request.getOption().add(no);

    Dataset  nd = new Dataset();
    nd.setName("Test:testDataset");
    nd.setDescription("Test datasets");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testRedeployApplication");
    request.getDataset().add(nd);
    
		testSubject.redeployApplication(request);

		// Verify
		Application verify =testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationUpdateDescriptions() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		request.setName("Test:testRedeployApplicationUpdateDescriptions-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    for (Feature nf : request.getFeature()) {
      nf.setDescription(UPDATED + nf.getDescription());
    }
    for (Option  no : request.getOption()) {
      no.setDescription(UPDATED + no.getDescription());
    }
    for (Dataset  nd : request.getDataset()) {
      nd.setDescription(UPDATED + nd.getDescription());
    }
    
		testSubject.redeployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
    for (Feature nf : request.getFeature()) {
      assertTrue("Unexpected description", 
                 nf.getDescription().startsWith(UPDATED));
    }
    for (Option  no : request.getOption()) {
      assertTrue("Unexpected description", 
                 no.getDescription().startsWith(UPDATED));
    }
    for (Dataset  nd : request.getDataset()) {
      assertTrue("Unexpected description", 
                 nd.getDescription().startsWith(UPDATED));
    }
  }
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationDropDetails() 
  {
		// Setup
		Application request =testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);
		
    request.setName("Test:testRedeployApplicationDropDetails-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    request.getFeature().clear();
    request.getOption().clear();
    request.getDataset().clear();
    
		testSubject.redeployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the redeployApplication method
	 */
	@Test
	public void testRedeployApplicationReplaceDetails() 
  {
		// Setup
		Application request = testSubject.getDeploymentDescriptor("Quota");
		assertNotNull("Unexpected null response", request);
		request.setName("Test:testRedeployApplicationReplaceDetails-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    request.getFeature().clear();
    request.getOption().clear();
    request.getDataset().clear();
    Feature nf = new Feature();
    nf.setName("Test:testRedeployApplication");
    nf.setDescription("Tests the undeployApplication method");
    request.getFeature().add(nf);

    Option  no = new Option();
    no.setName("Test:testOption");
    no.setDescription("Test options");
    no.setDataType("JSON");
    
    //no.setDefaultValue("{\"Name\"= \"Value\"}");
    
    request.getOption().add(no);

    Dataset  nd = new Dataset();
    nd.setName("Test:testDataset");
    nd.setDescription("Test datasets");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testRedeployApplication");
    request.getDataset().add(nd);
    
		testSubject.redeployApplication(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Features", 
                 request.getFeature().size(), 
                 verify.getFeature().size());
    assertEquals("Unexpected number of Options", 
                 request.getOption().size(), 
                 verify.getOption().size());
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the deployDatasets method
	 */
	@Test
	public void testDeployDatasets() 
  {
		// Setup
		Application request = new Application();
    request.setName("Test:testDeployDatasets-" + System.currentTimeMillis());
		testSubject.deployApplication(request);

    // Execute
    Dataset  nd = new Dataset();
    nd.setName("Test:testDataset1");
    nd.setDescription("Test dataset one");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testDeployDatasets1");
    request.getDataset().add(nd);
    nd = new Dataset();
    nd.setName("Test:testDataset2");
    nd.setDescription("Test dataset two");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testDeployDatasets2");
    request.getDataset().add(nd);
    
		testSubject.deployDatasets(request);

		// Verify
		Application verify = testSubject.getDeploymentDescriptor(request.getName());
		assertNotNull("Unexpected null response", verify);
    assertEquals("Unexpected number of Datasets", 
                 request.getDataset().size(), 
                 verify.getDataset().size());
	}
  
	/**
	 * Tests the deployDatasets method with an invalid request
	 */
	@Test
	public void testDeployDatasetsWithDuplicates() 
  {
		// Setup
		Application request = new Application();
		request.setName("Test:testDeployDuplicateDatasets-" + 
                              System.currentTimeMillis());
		testSubject.deployApplication(request);

    Dataset nd = new Dataset();
    nd.setName("Test:testDataset1");
    nd.setDescription("Test dataset one");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testDeployDatasets1");
    request.getDataset().add(nd);
    nd = new Dataset();
    nd.setName("Test:testDataset1");
    nd.setDescription("Test dataset two");
    nd.setCategory("Test");
    nd.setDiscriminator("Name=testDeployDatasets2");
    request.getDataset().add(nd);

    // Execute
    try {
      testSubject.deployDatasets(request);
      // Verify
      fail("Expected IllegalArgumentException not reported");
    } catch(EJBException | IllegalArgumentException e) {
      System.out.println("Caught expected exception:" + e.getMessage());
    }
	}
  
  
}
