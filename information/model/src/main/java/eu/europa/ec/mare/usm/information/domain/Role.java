package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Holds a Role based on a set a features from any applications.
 * The goal is to define the actions that can be performed on data;.
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleName;
    private Set<Feature> features;

    public Role() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleName=" + roleName +
                ", features=" + features +
                '}';
    }
}
