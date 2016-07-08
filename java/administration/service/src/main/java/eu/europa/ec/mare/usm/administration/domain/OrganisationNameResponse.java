/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
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