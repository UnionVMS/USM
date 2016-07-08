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
 * A very simple query for retrieval of Application information.
 */
public class FindApplicationQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String parentName;
	private Paginator paginator;
	
	/**
	 * Gets the value of name
	 *
	 * @return the value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of name
	 *
	 * @param name new value of name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the value of paginator
	 *
	 * @return the value of paginator
	 */
	public Paginator getPaginator() {
		return paginator;
	}

	/**
	 * Sets the value of paginator
	 *
	 * @param paginator new value of paginator
	 */
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}
	
	/**
	 * Gets the value of parentName
	 *
	 * @return the value of parentName
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * Sets the value of parentName
	 *
	 * @param parentName new value of parentName
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

  /**
   * Creates a new instance.
   */
  public FindApplicationQuery() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ApplicationQuery{" +
    		"name=" + name +
    		", parentName=" + parentName +
    		", paginator=" + paginator + '}';
  }
  
}