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
 * object to be used for pagination
 */
public class Paginator implements Serializable {
  private static final long serialVersionUID = 1L;
  private int offset;
  private int limit;
  private String sortColumn;
  private String sortDirection;

  /**
   * Creates a new instance.
   */
  public Paginator() {
  }
	
	/**
	   * Get the value of offset
	   *
	   * @return the value of offset
	   */
	public int getOffset() {
		return offset;
	}

	/**
	   * Set the value of offset
	   *
	   * @param offset new value of offset
	   */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	   * Get the value of limit
	   *
	   * @return the value of limit
	   */
	public int getLimit() {
		return limit;
	}

	/**
	   * Set the value of limit
	   *
	   * @param limit new value of limit
	   */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	   * Get the value of sortColumn
	   *
	   * @return the value of sortColumn
	   */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	   * Set the value of sortColumn
	   *
	   * @param sortColumn new value of sortColumn
	   */
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/**
	   * Get the value of sortDirection
	   *
	   * @return the value of sortDirection
	   */
	public String getSortDirection() {
		return sortDirection;
	}

	/**
	   * Set the value of sortDirection
	   *
	   * @param sortDirection new value of sortDirection
	   */
	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}


  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Option{" + 
            "offset=" + offset + 
            ", limit=" + limit +
            ", sortColumn=" + sortColumn +
            ", sortDirection=" + sortDirection +
            '}';
  }
  
}