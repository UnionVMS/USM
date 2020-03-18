package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

/**
 * Holds a Feature.
 * <ul>
 * <li>It can be optionally associated to a group to identify rapidly all
 * features acting on a same business entity: ex: feature "create organisation"
 * to the group "organisation";</li>
 * <li>It is attached to the application in which it has been defined;</li>
 * <li>A feature is defined in the configuration file of that application and
 * delivered to USM at deployment time;</li>
 * <li>A feature is not managed (CRUD) by USM but by the application exposed
 * it;</li>
 * </ul>
 */
public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;
    private String applicationName;
    private String featureName;
    private Integer featureId;

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public Feature() {
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "applicationName=" + applicationName +
                ", featureId=" + featureId +
                ", featureName=" + featureName +
                '}';
    }

}
