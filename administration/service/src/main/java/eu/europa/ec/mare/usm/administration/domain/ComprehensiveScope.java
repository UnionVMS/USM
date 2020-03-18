package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a comprehensive scope info
 */
public class ComprehensiveScope implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String status;
    private Long scopeId;

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

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public ComprehensiveScope() {
    }

    @Override
    public String toString() {
        return "ScopeQuery{" +
                "name=" + name +
                ", status=" + status +
                ", scopeId=" + scopeId +
                '}';
    }

}
