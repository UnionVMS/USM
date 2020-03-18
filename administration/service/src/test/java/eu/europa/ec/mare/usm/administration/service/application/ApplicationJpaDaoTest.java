package eu.europa.ec.mare.usm.administration.service.application;

import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import junit.framework.Assert;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ApplicationJpaDaoTest extends DeploymentFactory {
    private static final String APPLICATION_NAME_PREFIX = "testJpaDaoApplicationName";
    private static final String APPLICATION_DESCRIPTION_PREFIX = "Desciption for app ";
    private static final String PARENT_NAME_RELATIONSHIP_FLAG = "_";
    private static final String[] APPLICATION_NAMES = {
            APPLICATION_NAME_PREFIX + "1",
            APPLICATION_NAME_PREFIX + "2",
            APPLICATION_NAME_PREFIX + "3",
            APPLICATION_NAME_PREFIX + "3" + PARENT_NAME_RELATIONSHIP_FLAG + "1",
            APPLICATION_NAME_PREFIX + "3" + PARENT_NAME_RELATIONSHIP_FLAG + "2"
    };

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Before
    public void preparePersistenceTest() throws Exception {
        clearData();
        insertData();
        startTransaction();
    }

    @Test
    public void shouldFindAllAppsUsingJpqlQuery() throws Exception {
        String fetchingAllAppsInJpql = "select a from ApplicationEntity a where a.name like '"
                + APPLICATION_NAME_PREFIX + "%' order by a.applicationId";

        List<ApplicationEntity> apps = em.createQuery(fetchingAllAppsInJpql, ApplicationEntity.class).getResultList();
        assertContainsAllApps(apps);
    }

    private static void assertContainsAllApps(List<ApplicationEntity> apps) {
        assertEquals(APPLICATION_NAMES.length, apps.size());
        final List<String> names = new ArrayList<>();
        for (ApplicationEntity app : apps) {
            names.add(app.getName());
            assertParent(app);
            assertChilds(app);
        }
        assertTrue(names.containsAll(Arrays.asList(APPLICATION_NAMES)));
    }

    private static void assertChilds(ApplicationEntity app) {
        if (app.getChildApplicationList() != null && !app.getChildApplicationList().isEmpty()) {
            for (ApplicationEntity child : app.getChildApplicationList()) {
                assertChildNamePointsParent(app, child);
            }
        }
    }

    private static void assertChildNamePointsParent(ApplicationEntity app, ApplicationEntity child) {
        assertTrue(child.getName().startsWith(app.getName()));
    }

    private static void assertParent(ApplicationEntity app) {
        if (hasParentAccordingName(app)) {
            assertEquals(app.getParentApplication().getName(), parentNameAccordingName(app));
        }
    }

    @After
    public void commitTransaction() throws Exception {
        utx.commit();
    }

    private void startTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
    }

    private void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();
        for (String name : APPLICATION_NAMES) {
            ApplicationEntity app = new ApplicationEntity();
            app.setName(name);
            appSetDescription(app, name);
            appSetParent(app, name);
            em.persist(app);
        }
        utx.commit();
        em.clear();
    }

    private void appSetDescription(ApplicationEntity app, String name) {
        app.setDescription(APPLICATION_DESCRIPTION_PREFIX + app.getName());
    }

    private void appSetParent(ApplicationEntity app, String name) {
        if (hasParentAccordingName(app)) {
            ApplicationEntity parent = (ApplicationEntity) em.createQuery(
                    "select p from ApplicationEntity p where p.name = '" + parentNameAccordingName(app)
                            + "'").getSingleResult();
            app.setParentApplication(parent);
        }
    }

    private static boolean hasParentAccordingName(ApplicationEntity app) {
        return app.getName().contains(PARENT_NAME_RELATIONSHIP_FLAG);
    }

    private static String parentNameAccordingName(ApplicationEntity app) {
        return app.getName().substring(0, app.getName().indexOf("_"));
    }

    private void clearData() throws Exception {
        utx.begin();
        em.joinTransaction();
        em.createQuery("delete from ApplicationEntity a where a.name like '" + APPLICATION_NAME_PREFIX
                + "%'").executeUpdate();
        utx.commit();
    }
}
