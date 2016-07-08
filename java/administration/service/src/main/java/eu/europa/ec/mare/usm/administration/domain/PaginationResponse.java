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
import java.util.List;

/**
 * Holds a response that contains the response plus the total number of the results
 */
public class PaginationResponse<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<T> results;
  private int total;

	/**
	 * Creates a new instance
	 */
	public PaginationResponse() {
	}

	/**
	 * Get the results of the query
	 *
	 * @return the list of returned values
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Set the results of the query
	 *
	 * @param results
	 *            new list of returned values
	 */
	public void setResults(List<T> results) {
		this.results = results;
	}

	/**
	 * Get the value of total results
	 *
	 * @return the value of total results
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Set the value of total results
	 *
	 * @param total
	 *            value of total results
	 */
	public void setTotal(int total) {
		this.total = total;
	}

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "PaginationResponse{" + 
            "results=" + results + 
            "total=" + total + 
            '}';
  }

}