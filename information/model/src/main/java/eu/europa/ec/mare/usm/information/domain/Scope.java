package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Holds a Scope based on a set of datasets from any applications.
 * The goal is to limit the visibility on data.
 */
public class Scope implements Serializable {
    private static final long serialVersionUID = 1L;
    private String scopeName;
    private Date activeFrom;
    private Date activeTo;
    private Date dataFrom;
    private Date dataTo;
    private Set<DataSet> datasets;

    public Scope() {
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public Date getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(Date dataFrom) {
        this.dataFrom = dataFrom;
    }

    public Date getDataTo() {
        return dataTo;
    }

    public void setDataTo(Date dataTo) {
        this.dataTo = dataTo;
    }

    public Set<DataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(Set<DataSet> datasets) {
        this.datasets = datasets;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "scopeName=" + scopeName +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", dataFrom=" + dataFrom +
                ", dataTo=" + dataTo +
                ", datasets=" + datasets +
                '}';
    }

}
