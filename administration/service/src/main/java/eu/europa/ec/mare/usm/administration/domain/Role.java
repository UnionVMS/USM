package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds Role details
 */
public class Role implements Serializable {

    private static final long serialVersionUID = 3916859100684925516L;
    private Long roleId;
    private String name;
    private String description;
    private String status;
    private List<Feature> features;
    private int activeUsers;

    public Role() {
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name=" + name +
                ", status=" + status +
                ", description=" + description +
                ", features=" + features +
                ", activeUsers=" + activeUsers +
                '}';
    }

}
