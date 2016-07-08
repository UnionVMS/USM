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
 * Holds a comprehensive scope info
 */
public class ComprehensiveScope implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String status;
	private Long scopeId;
	
	/**
	   * Gets the value of scope name
	   *
	   * @return the value of scope name
	   */
	public String getName() {
		return name;
	}

	/**
	   * Sets the value of scope name
	   *
	   * @param name new value of scope name
	   */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	   * Gets the value of status
	   *
	   * @return the value of status
	   */
	public String getStatus() {
		return status;
	}

	/**
	   * Sets the value of status
	   *
	   * @param status new value of status
	   */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	   * Gets the value of scope id
	   *
	   * @return the value of scope id
	   */
	public Long getScopeId() {
		return scopeId;
	}

	/**
	   * Sets the value of scope id
	   *
	   * @param scopeId new value of scope id
	   */
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

  /**
   * Creates a new instance.
   */
  public ComprehensiveScope() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ScopeQuery{" +
    		"name=" + name + 
    		", status=" + status +
            ", scopeId=" + scopeId +
            '}';
  }
  
}