package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds identification information about an organisation.
 */
public class Organisation implements Serializable {
    private static final long serialVersionUID = 2L;
    private String name;
    private String nation;
    private String email;
    private String description;
    private boolean enabled;
    private String parentOrganisation;
    private List<String> childOrganisations;
    private List<EndPoint> endPoints;

    public Organisation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getParentOrganisation() {
        return parentOrganisation;
    }

    public void setParentOrganisation(String parentOrganisation) {
        this.parentOrganisation = parentOrganisation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getChildOrganisations() {
        return childOrganisations;
    }

    public void setChildOrganisations(List<String> childOrganisations) {
        this.childOrganisations = childOrganisations;
    }

    public List<EndPoint> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(List<EndPoint> endPoints) {
        this.endPoints = endPoints;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "name=" + name +
                ", nation=" + nation +
                ", email=" + email +
                ", enabled=" + enabled +
                ", description=" + description +
                ", parentOrganisation=" + parentOrganisation +
                ", childOrganisations=" + childOrganisations +
                ", endPoints=" + endPoints +
                '}';
    }
}
