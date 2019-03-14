package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;

/**
 * Holds the information about the scope entity.
 * 
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
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the activeFrom
	 */
	public Date getActiveFrom() {
		return activeFrom;
	}
	/**
	 * @param activeFrom the activeFrom to set
	 */
	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}
	/**
	 * @return the activeTo
	 */
	public Date getActiveTo() {
		return activeTo;
	}
	/**
	 * @param activeTo the activeTo to set
	 */
	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}
	/**
	 * @return the dateFrom
	 */
	public Date getDataFrom() {
		return dataFrom;
	}
	/**
	 * @param dataFrom the dataFrom to set
	 */
	public void setDataFrom(Date dataFrom) {
		this.dataFrom = dataFrom;
	}
	/**
	 * @return the dateTo
	 */
	public Date getDataTo() {
		return dataTo;
	}
	/**
	 * @param dataTo the dataTo to set
	 */
	public void setDataTo(Date dataTo) {
		this.dataTo = dataTo;
	}
	/**
	 * @return the dataSets
	 */
	public List<DataSet> getDataSets() {
		return dataSets;
	}
	/**
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
	
	/**
	 * @return the activeUsers
	 */
	public int getActiveUsers() {
		return activeUsers;
	}
	/**
	 * @param activeUsers the activeUsers to set
	 */
	public void setActiveUsers(int activeUsers) {
		this.activeUsers = activeUsers;
	}
	
	/**
	 * @return the scopeId
	 */
	public Long getScopeId() {
		return scopeId;
	}
	/**
	 * @param scopeId the scopeId to set
	 */
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
				+ ", activeUsers="+activeUsers+", scopeId=" + scopeId+" ]";
	} 
	
}
