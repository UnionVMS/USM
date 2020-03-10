package eu.europa.ec.mare.usm.information.service.impl;

import eu.europa.ec.mare.usm.information.domain.deployment.*;

import javax.ejb.Stateless;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides operations for the validation and authorisation of Application
 * Deployment related service requests
 */
@Stateless
public class DeploymentValidator extends RequestValidator {

    /**
     * Creates a new instance.
     */
    public DeploymentValidator() {
    }


    public void assertValid(Application input, boolean checkDetails) {
        assertNotNull("request", input);
        assertNotEmpty("application.name", input.getName());
        if (checkDetails) {
            checkDetails(input);
        } else {
            assertNotEmpty("application.name", input.getName());
        }
    }

    public void assertValidApplication(String request) {
        assertNotEmpty("application.name", request);
    }

    public void assertValidDatasets(Application request) {
        assertNotNull("request", request);
        assertNotEmpty("application.name", request.getName());
        checkDatasets(request);
        checkForDuplicateDatasets(request);
    }


    private void checkDetails(Application app) {
        assertValid("application", app);

        checkDatasets(app);

        for (int i = 0; i < app.getFeature().size(); i++) {
            Feature f = app.getFeature().get(i);
            String loc = "application.feature[" + i + "]";
            assertValid(loc, f);
        }

        for (int i = 0; i < app.getOption().size(); i++) {
            Option d = app.getOption().get(i);
            String loc = "application.option[" + i + "]";
            assertValid(loc, d);
            assertNotTooLong(loc + ".category", 32, d.getDataType());

            //assertNotTooLong(loc + ".discriminator", 512, d.getDefaultValue());
        }

        checkForDuplicates(app);
    }

    private void checkDatasets(Application app) {
        for (int i = 0; i < app.getDataset().size(); i++) {
            Dataset d = app.getDataset().get(i);
            String loc = "application.dataset[" + i + "]";
            assertValid(loc, d);
            assertNotEmpty(loc + ".category", d.getCategory());
            assertNotTooLong(loc + ".category", 128, d.getCategory());
            assertNotTooLong(loc + ".discriminator", 512, d.getDiscriminator());
        }
        checkForDuplicateDatasets(app);
    }

    private void assertValid(String name, NameAndDescription obj) {
        assertNotEmpty(name + ".name", obj.getName());
        assertNotTooLong(name + ".name", 128, obj.getName());
        assertNotTooLong(name + ".description", 512, obj.getDescription());
    }

    private void checkForDuplicates(Application app) {
        checkForDuplicateDatasets(app);

        if (app.getFeature() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < app.getFeature().size(); i++) {
                Feature item = app.getFeature().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Feature " + item.getName() +
                            " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }

        if (app.getOption() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < app.getOption().size(); i++) {
                Option item = app.getOption().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Option " + item.getName() +
                            " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }
    }

    private void checkForDuplicateDatasets(Application app)
            throws IllegalArgumentException {
        if (app.getDataset() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < app.getDataset().size(); i++) {
                Dataset item = app.getDataset().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Dataset " + item.getName() +
                            " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }
    }


}
