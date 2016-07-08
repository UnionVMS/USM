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
package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;


public class DataSetFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	private String applicationName;
	private String name;
	private String category;
	private String discriminator;

	/**
	 * Creates a new instance
	 */
	public DataSetFilter() {
	}

	/**
	 * Get the value of applicationName
	 *
	 * @return the value of applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Set the value of applicationName
	 *
	 * @param applicationName
	 *            new value of applicationName
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "DataSetFilter{" + "applicationName=" + applicationName + ", name=" + name + ", category="
				+ category + ", discriminator=" + discriminator + '}';
	}

}