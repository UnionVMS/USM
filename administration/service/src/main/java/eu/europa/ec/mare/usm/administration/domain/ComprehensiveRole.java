package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds summary information about a Role
 */
public class ComprehensiveRole implements Serializable {

    private static final long serialVersionUID = 3916859100684925516L;
    private String name;
    private String description;
    private String status;
    private Long roleId;
    private List<Long> features;
    private int activeUsers;
    private Boolean updateFeatures = true;

    public ComprehensiveRole() {
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getFeatures() {
        return features;
    }

    public void setFeatures(List<Long> features) {
        this.features = features;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    /**
     * Get the flag update features for updating features when update a role
     *
     * @return updateFeatures new value of flag update features
     */
    public Boolean getUpdateFeatures() {
        return updateFeatures;
    }

    /**
     * Set the flag update features for updating features when update a role
     *
     * @param updateFeatures new value of flag update features
     */
    public void setUpdateFeatures(Boolean updateFeatures) {
        this.updateFeatures = updateFeatures;
    }

    @Override
    public String toString() {
        return "ComprehensiveRole{" +
                "roleId=" + roleId +
                ", name=" + name +
                ", description=" + description +
                ", status=" + status +
                ", updateFeatures=" + updateFeatures +
                ", features=" + features +
                ", activeUsers=" + activeUsers +
                '}';
    }

}
