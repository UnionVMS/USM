package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for role search
 */
public class FindRolesQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleName;
    private String applicationName;
    private String status;
    private Paginator paginator;

    public FindRolesQuery() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    @Override
    public String toString() {
        return "FindUsersQuery{" +
                "roleName=" + roleName +
                ", applicationName=" + applicationName +
                ", status=" + status +
                ", paginator=" + paginator +
                '}';
    }

}
