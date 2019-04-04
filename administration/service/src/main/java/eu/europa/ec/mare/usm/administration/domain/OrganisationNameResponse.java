package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class OrganisationNameResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String parentOrgName;
	private String nation;
	private String status;
	
	/**
	 * @return the parentOrgName
	 */
	public String getParentOrgName() {
		return parentOrgName;
	}
	/**
	 * @param parentOrgName the parentOrgName to set
	 */
	public void setParentOrgName(String parentOrgName) {
		this.parentOrgName = parentOrgName;
	}
	/**
	 * @return the nation
	 */
	public String getNation() {
		return nation;
	}
	/**
	 * @param nation the nation to set
	 */
	public void setNation(String nation) {
		this.nation = nation;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrganisationNameResponse [parentOrgName=" + parentOrgName + ", nation=" + nation + ", status=" + status + "]";
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
