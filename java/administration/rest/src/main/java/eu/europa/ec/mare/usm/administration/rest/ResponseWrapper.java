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

/**
 * 
 * class to be used as wrapper class in case
 * we need to wrap a single class into json object for the service response
 *
 */
public class ResponseWrapper<T> implements Serializable {
	private static final long serialVersionUID = 1;
	private T result;

	/**
	 * Get the value of result
	 *
	 * @return the value of result
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Set the value of result
	 *
	 * @param result new value of result
	 */
	public void setResult(T result) {
		this.result = result;
	}

}