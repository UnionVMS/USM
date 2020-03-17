package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Domain object for describing the relationship between scope and dataSet.
 */
public class ScopeDataSet implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long scopeId;
    private Long datasetId;

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    @Override
    public String toString() {
        return "ScopeDataSet [scopeId=" + scopeId + ", datasetId=" + datasetId
                + "]";
    }

}
