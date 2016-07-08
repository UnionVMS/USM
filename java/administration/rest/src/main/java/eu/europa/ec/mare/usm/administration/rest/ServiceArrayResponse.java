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
package eu.europa.ec.mare.usm.administration.rest;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * class to be used as wrapper class in case
 * we need to wrap json array into json object
 * for the service response
 *
 * 
 */
public class ServiceArrayResponse<T> implements Serializable {
	private static final long serialVersionUID = -3797882871515664905L;
	private List<T> results;

	/**
	 * Get the value of results
	 *
	 * @return the value of results
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Set the value of results
	 *
	 * @param results new value of results
	 */
	public void setResults(List<T> results) {
		this.results = results;
	}

}