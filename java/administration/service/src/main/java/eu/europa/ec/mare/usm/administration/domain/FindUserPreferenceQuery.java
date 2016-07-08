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
 * Service request for the retrieval of a list of user preferences.
 */
public class FindUserPreferenceQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String groupName;

	/**
	 * Creates a new instance.
	 */
	public FindUserPreferenceQuery() {
	}

	/**
	 * Gets the value of userName
	 *
	 * @return the value of userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the value of userName
	 *
	 * @param nation new value of userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Gets the value of groupName
	 *
	 * @return the value of groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the value of groupName
	 *
	 * @param groupName new value of groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "FindUserPreferenceQuery{" +   
            "userName=" + userName + 
            ", groupName=" + groupName +
        '}';
	}

}