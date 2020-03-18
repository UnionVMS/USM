package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for scope search
 */
public class FindScopesQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String scopeName;
    private String applicationName;
    private String status;
    private Paginator paginator;

    public FindScopesQuery() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
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
                "scopeName=" + scopeName +
                ", applicationName=" + applicationName +
                ", status=" + status +
                ", paginator=" + paginator +
                '}';
    }

}
