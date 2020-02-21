package eu.europa.ec.mare.usm.information.rest.service;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.domain.deployment.Dataset;
import eu.europa.ec.mare.usm.information.domain.deployment.Feature;
import eu.europa.ec.mare.usm.information.domain.deployment.Option;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DeploymentRestServiceIT extends BuildInformationRestDeployment {

    private static final String DEPLOYMENTS_ENDPOINT = "deployments";

    @Test
    @OperateOnDeployment("normal")
    public void getDeploymentTest() {
        Application application = getApplicationByName("USM");
        assertNotNull(application);
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployApplicationTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testDeployApplication-" + System.currentTimeMillis());

        Feature feature = new Feature();
        feature.setName("Test:testDeployApplication");
        feature.setDescription("Tests the deployApplication method");
        application.getFeature().add(feature);

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertNotNull(fetchedApp);
        assertEquals(application.getName(), fetchedApp.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployApplicationWithDuplicatesTest() {
        Application application = getApplicationByName("Quota");

        application.setName("Test:testDeployApplicationWithDuplicates-" + System.currentTimeMillis());
        Feature feature = new Feature();
        feature.setName("Test:testDeployApplicationWithDuplicates");
        feature.setDescription("Tests the deployApplication method");

        application.getFeature().add(feature);
        application.getFeature().add(feature);

        Option option = new Option();
        option.setName("Test:testOption");
        option.setDescription("Test options");
        option.setDataType("JSON");

        application.getOption().add(option);
        application.getOption().add(option);

        Response response = deployApplication(application);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployMinimalisticApplicationTest() {
        Application application = new Application();
        application.setName("Test:testDeployMinimalisticApplication-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());
        assertNotNull(fetchedApp);
        assertEquals(application.getName(), fetchedApp.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployFeaturesOnlyApplicationTest() {
        Application application = new Application();
        application.setName("Test:testDeployFeaturesOnlyApplication-" + System.currentTimeMillis());

        Feature feature = new Feature();
        feature.setName("Test:testDeployFeaturesOnlyApplication");
        feature.setDescription("Tests the deployApplication method");
        application.getFeature().add(feature);

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertNotNull(fetchedApp);
        assertEquals(application.getName(), fetchedApp.getName());
        assertTrue(fetchedApp.getDataset().isEmpty());
        assertTrue(fetchedApp.getOption().isEmpty());
        assertFalse(fetchedApp.getFeature().isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void unDeployApplicationTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testUndeployApplication-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Response delete = deleteApplication(application.getName());
        assertEquals(Response.Status.OK.getStatusCode(), delete.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());
        assertNull(fetchedApp);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unDeployApplicationNotAllowedTest() {
        String applicationName = "USM";
        Response delete = deleteApplication(applicationName);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), delete.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationRetainingAllDataSetsTest() {
        // GET Quota Application
        Application application = getApplicationByName("Quota");

        String appName = "Test:testRedeployApplicationAddDetails-" + System.currentTimeMillis();
        application.setName(appName);

        // Update the name and create as new
        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Add 2 DataSets to Application
        Dataset dataset1 = new Dataset();
        dataset1.setName("Test:testDataset");
        dataset1.setDescription("Test datasets");
        dataset1.setCategory("Test");
        dataset1.setDiscriminator("Name=testRedeployApplication");
        application.getDataset().add(dataset1);

        Dataset dataset2 = new Dataset();
        dataset2.setName("Test:testDataset2");
        dataset2.setDescription("Test datasets 2");
        dataset2.setCategory("Test");
        dataset2.setDiscriminator("Name=testRedeployApplication");
        application.getDataset().add(dataset2);

        // UPDATE Application with 2 DataSets
        Response update = updateDataSets(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        // GET Quota application again
        Application fetchedApp = getApplicationByName("Quota");

        fetchedApp.setName(appName);
        fetchedApp.setRetainDatasets(true);

        // UPDATE Quota and setRetainDataSets
        redeployApplication(fetchedApp);

        // GET updated Quota Application
        fetchedApp = getApplicationByName(fetchedApp.getName());

        assertEquals(fetchedApp.getFeature().size(), application.getFeature().size());
        assertEquals(fetchedApp.getDataset().size(), application.getDataset().size());
        assertEquals(fetchedApp.getOption().size(), application.getOption().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplication-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        application.getFeature().remove(0);
        Feature feature = new Feature();
        feature.setName("Test:testRedeployApplication");
        feature.setDescription("Tests the undeployApplication method");
        application.getFeature().add(feature);

        application.getOption().remove(0);
        Option option = new Option();
        option.setName("Test:testOption");
        option.setDescription("Test options");
        option.setDataType("JSON");
        application.getOption().add(option);

        application.getDataset().remove(0);
        Dataset dataset = new Dataset();
        dataset.setName("Test:testDataset");
        dataset.setDescription("Test datasets");
        dataset.setCategory("Test");
        dataset.setDiscriminator("Name=testRedeployApplication");
        application.getDataset().add(dataset);

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertNotNull(fetchedApp);
        assertEquals(application.getOption().size(), fetchedApp.getOption().size());
        assertEquals(application.getFeature().size(), fetchedApp.getFeature().size());
        assertEquals(application.getDataset().size(), fetchedApp.getDataset().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationNoChangesTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplicationNoChanges-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertNotNull(fetchedApp);
        assertEquals(application.getOption().size(), fetchedApp.getOption().size());
        assertEquals(application.getFeature().size(), fetchedApp.getFeature().size());
        assertEquals(application.getDataset().size(), fetchedApp.getDataset().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationAddDetailsTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplicationAddDetails-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Feature feature = new Feature();
        feature.setName("Test:testRedeployApplication");
        feature.setDescription("Tests the undeployApplication method");
        application.getFeature().add(feature);

        Option option = new Option();
        option.setName("Test:testOption");
        option.setDescription("Test options");
        option.setDataType("JSON");
        application.getOption().add(option);

        Dataset dataset = new Dataset();
        dataset.setName("Test:testDataset");
        dataset.setDescription("Test datasets");
        dataset.setCategory("Test");
        dataset.setDiscriminator("Name=testRedeployApplication");
        application.getDataset().add(dataset);

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertNotNull(fetchedApp);
        assertEquals(application.getOption().size(), fetchedApp.getOption().size());
        assertEquals(application.getFeature().size(), fetchedApp.getFeature().size());
        assertEquals(application.getDataset().size(), fetchedApp.getDataset().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationUpdateDescriptionsTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplicationUpdateDescriptions-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final String UPDATED = "Updated: ";

        application.getFeature().forEach(f -> f.setDescription(UPDATED + f.getDescription()));
        application.getOption().forEach(o -> o.setDescription(UPDATED + o.getDescription()));
        application.getDataset().forEach(d -> d.setDescription(UPDATED + d.getDescription()));

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        fetchedApp.getFeature().forEach(f -> assertTrue(f.getDescription().startsWith(UPDATED)));
        fetchedApp.getOption().forEach(o -> assertTrue(o.getDescription().startsWith(UPDATED)));
        fetchedApp.getDataset().forEach(d -> assertTrue(d.getDescription().startsWith(UPDATED)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationDropDetailsTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplicationDropDetails-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        application.getFeature().clear();
        application.getOption().clear();
        application.getDataset().clear();

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertTrue(fetchedApp.getFeature().isEmpty());
        assertTrue(fetchedApp.getOption().isEmpty());
        assertTrue(fetchedApp.getDataset().isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void redeployApplicationReplaceDetailsTest() {
        Application application = getApplicationByName("Quota");
        application.setName("Test:testRedeployApplicationReplaceDetails-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        application.getFeature().clear();
        application.getOption().clear();
        application.getDataset().clear();

        Feature feature = new Feature();
        feature.setName("Test:testRedeployApplication");
        feature.setDescription("Tests the undeployApplication method");
        application.getFeature().add(feature);

        Option option = new Option();
        option.setName("Test:testOption");
        option.setDescription("Test options");
        option.setDataType("JSON");
        application.getOption().add(option);

        Dataset dataset = new Dataset();
        dataset.setName("Test:testDataset");
        dataset.setDescription("Test datasets");
        dataset.setCategory("Test");
        dataset.setDiscriminator("Name=testRedeployApplication");
        application.getDataset().add(dataset);

        Response update = redeployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());

        assertEquals(application.getFeature().size(), fetchedApp.getFeature().size());
        assertEquals(application.getOption().size(), fetchedApp.getOption().size());
        assertEquals(application.getDataset().size(), fetchedApp.getDataset().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployDataSetsTest() {
        Application application = new Application();
        application.setName("Test:testDeployDatasets-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Dataset dataset1 = new Dataset();
        dataset1.setName("Test:testDataset1");
        dataset1.setDescription("Test dataset one");
        dataset1.setCategory("Test");
        dataset1.setDiscriminator("Name=testDeployDatasets1");
        application.getDataset().add(dataset1);

        Dataset dataset2 = new Dataset();
        dataset2.setName("Test:testDataset2");
        dataset2.setDescription("Test dataset two");
        dataset2.setCategory("Test");
        dataset2.setDiscriminator("Name=testDeployDatasets2");
        application.getDataset().add(dataset2);

        Response update = updateDataSets(application);
        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());

        Application fetchedApp = getApplicationByName(application.getName());
        assertEquals(application.getDataset().size(), fetchedApp.getDataset().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deployDataSetsWithDuplicatesTest() {
        Application application = new Application();
        application.setName("Test:testDeployDuplicateDatasets-" + System.currentTimeMillis());

        Response response = deployApplication(application);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Dataset dataset1 = new Dataset();
        dataset1.setName("Test:testDataset1");
        dataset1.setDescription("Test dataset one");
        dataset1.setCategory("Test");
        dataset1.setDiscriminator("Name=testDeployDatasets1");
        application.getDataset().add(dataset1);

        Dataset dataset2 = new Dataset();
        dataset2.setName("Test:testDataset1");
        dataset2.setDescription("Test dataset two");
        dataset2.setCategory("Test");
        dataset2.setDiscriminator("Name=testDeployDatasets2");
        application.getDataset().add(dataset2);

        Response update = updateDataSets(application);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), update.getStatus());
    }

    private Application getApplicationByName(String applicationName) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS_ENDPOINT)
                .path(applicationName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Application.class);
    }

    private Response deployApplication(Application application) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS_ENDPOINT)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(application));
    }

    private Response redeployApplication(Application application) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS_ENDPOINT)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .put(Entity.json(application));
    }

    private Response deleteApplication(String applicationName) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS_ENDPOINT)
                .path(applicationName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .delete();
    }

    private Response updateDataSets(Application application) {
        return getWebTargetInternal()
                .path(DEPLOYMENTS_ENDPOINT)
                .path("datasets")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .put(Entity.json(application));
    }
}
