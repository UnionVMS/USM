package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
/**
 *Domain object for describing the relationship between scope and dataSet.  
 *
 */
public class ScopeDataSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long scopeId;
	private Long datasetId;
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
	/**
	 * @return the datasetId
	 */
	public Long getDatasetId() {
		return datasetId;
	}
	/**
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScopeDataSet [scopeId=" + scopeId + ", datasetId=" + datasetId
				+ "]";
	}
	
	
	
	
}
