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
 * A very simple query for retrieval of an Application information.
 */
public class GetApplicationQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long applicationId;
	
	/**
	 * Gets the value of applicationId
	 *
	 * @return the value of applicationId
	 */
	public Long getApplicationId() {
		return applicationId;
	}

	/**
	 * Sets the value of applicationId
	 *
	 * @param applicationId new value of applicationId
	 */
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

  /**
   * Creates a new instance.
   */
  public GetApplicationQuery() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ApplicationQuery{" +
    		"applicationId=" + applicationId + '}';
  }

  
}