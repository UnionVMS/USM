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

    /**
     * Creates a new instance.
     */
    public ComprehensiveRole() {
    }


    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the value of role status
     *
     * @return the value of role status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the value of role status
     *
     * @param status new value of role status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the value of role description
     *
     * @return the value of role description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of role description
     *
     * @param description new value of role description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the value of roleId
     *
     * @return the value of role roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Set the value of roleId
     *
     * @param roleId new value of roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the featureIds
     */
    public List<Long> getFeatures() {
        return features;
    }


    /**
     * @param features the featureIds to set
     */
    public void setFeatures(List<Long> features) {
        this.features = features;
    }

    /**
     * Get the number of active users
     *
     * @return the number of active users
     */
    public int getActiveUsers() {
        return activeUsers;
    }

    /**
     * Set the number of active user
     *
     * @param activeUsers new value of active user
     */
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

    /**
     * Formats a human-readable view of this instance.
     *
     * @return a human-readable view
     */
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
