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

public class UserPreference {

	private String userName;
	private String roleName;
	private String scopeName;
	private String applicationName;
	private String optionName;
	private byte[] optionValue;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	
	

	public byte[] getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(byte[] optionValue) {
		this.optionValue = optionValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "UserPreference{" + "userName=" + userName + 
				", roleName=" + roleName + 
				", scopeName=" + scopeName + 
				", roleName=" + roleName +
				", applicationName=" + applicationName +
				", optionName=" + optionName + 
				", optionValue=" + optionValue +'}';
	}

}