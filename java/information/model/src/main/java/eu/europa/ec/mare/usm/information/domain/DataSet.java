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

/**
 * Holds a Filter/dataset<br/>.
 *  
 * Definition: a filter is a value for a given criteria: Ex: criteria for the 
 * vessel application: vessel_group, filter: Group A 
 * It contains the criteria (or type of filter) (ex: vessel_group) is on which 
 * the filter should be applied
 * it is attached to the application in which it has been defined;
 * it is defined through the user interface of an application;
 * Datasets can also be defined in the configuration file of that application 
 * and  delivered to USM at deployment time;
 * A filter is not managed (CRUD) by USM but by the application exposing it;
 */
public class DataSet implements Serializable {
	private static final long serialVersionUID = 1L;
	private String applicationName;
	private String name;
	private String category;
	private String discriminator;
	private String description;

	/**
	 * Creates a new instance
	 */
	public DataSet() {
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

	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "DataSet{" + "applicationName=" + applicationName + ", name=" + name + ", category="
				+ category + ", discriminator=" + discriminator + '}';
	}

}