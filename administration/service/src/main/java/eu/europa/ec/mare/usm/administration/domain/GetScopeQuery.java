package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query for the retrieval of a scope.
 */
public class GetScopeQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long scopeId;

    public GetScopeQuery() {
    }

    public GetScopeQuery(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public String toString() {
        return "GetScopeQuery{" + "scopeId=" + scopeId + '}';
    }

}
