package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query for the retrieval of a role.
 */
public class GetRoleQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long roleId;

    public GetRoleQuery() {
    }

    public GetRoleQuery(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "GetRoleQuery{"
                + "roleId=" + roleId
                + '}';
    }

}
