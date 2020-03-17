package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds summary information about a User
 */
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long userContextId;
    private String userName;
    private Long roleId;
    private Long scopeId;

    public UserContext() {
    }

    public Long getUserContextId() {
        return userContextId;
    }

    public void setUserContextId(Long userContextId) {
        this.userContextId = userContextId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
    public String toString() {
        return "UserContext {"
                + "userContextId=" + userContextId
                + ", userName=" + userName
                + ", roleId=" + roleId
                + ", scopeId=" + scopeId
                + '}';
    }
}
