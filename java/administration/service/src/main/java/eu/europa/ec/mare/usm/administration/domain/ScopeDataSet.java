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