package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds summary information about a UserContext
 */
public class ComprehensiveUserContext implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long userContextId;
    private Long roleId;
    private String role;
    private String roleStatus;
    private String roleDescription;
    private Long scopeId;
    private String scope;
    private String scopeStatus;
    private String scopeDescription;
    private int userPreferenceCount;

    public ComprehensiveUserContext() {
    }

    public Long getUserContextId() {
        return userContextId;
    }

    public void setUserContextId(Long userContextId) {
        this.userContextId = userContextId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScopeStatus() {
        return scopeStatus;
    }

    public void setScopeStatus(String scopeStatus) {
        this.scopeStatus = scopeStatus;
    }

    public String getScopeDescription() {
        return scopeDescription;
    }

    public void setScopeDescription(String scopeDescription) {
        this.scopeDescription = scopeDescription;
    }

    public int getUserPreferenceCount() {
        return userPreferenceCount;
    }

    public void setUserPreferenceCount(int userPreferenceCount) {
        this.userPreferenceCount = userPreferenceCount;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
        result = prime * result + ((scopeId == null) ? 0 : scopeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComprehensiveUserContext other = (ComprehensiveUserContext) obj;
        if (roleId == null) {
            if (other.roleId != null)
                return false;
        } else if (!roleId.equals(other.roleId))
            return false;
        if (scopeId == null) {
            if (other.scopeId != null)
                return false;
        } else if (!scopeId.equals(other.scopeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComprehensiveUserContext {" + " userContextId=" + userContextId + ", roleId=" + roleId + ", role=" + role + ", roleStatus="
                + roleStatus + ", roleDescription=" + roleDescription + ", scopeId=" + scopeId + ", scope=" + scope + ", scopeStatus="
                + scopeStatus + ", scopeDescription=" + scopeDescription + '}';
    }
}
