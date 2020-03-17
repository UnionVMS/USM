package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Holds the information about the scope entity.
 */
public class Scope implements Serializable {

    private static final long serialVersionUID = 8932203204225084011L;

    private String name;
    private String description;
    private String status;
    private Date activeFrom;
    private Date activeTo;
    private Date dataFrom;
    private Date dataTo;
    private List<DataSet> dataSets;
    private int activeUsers;
    //for update scope purposes we will need also the identifier
    private Long scopeId;
    private Boolean updateDatasets = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<DataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<DataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Boolean getUpdateDatasets() {
        return updateDatasets;
    }

    public void setUpdateDatasets(Boolean updateDatasets) {
        this.updateDatasets = updateDatasets;
    }

    @Override
    public String toString() {
        return "Scope [name=" + name + ", description=" + description
                + ", status=" + status + ", activeFrom=" + activeFrom
                + ", activeTo=" + activeTo + ", dataFrom=" + dataFrom
                + ", dataTo=" + dataTo + ", dataSets=" + dataSets
                + ", activeUsers=" + activeUsers + ", scopeId=" + scopeId + " ]";
    }

}
