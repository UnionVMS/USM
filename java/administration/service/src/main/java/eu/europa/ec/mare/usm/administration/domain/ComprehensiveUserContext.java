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
 * Holds summary information about a UserContext
 */
public class ComprehensiveUserContext implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long userContextId;
	private Long roleId;
	private String role;
	private String roleStatus;
	private String roleDescription;
	private Long scopeId;
	private String scope;
	private String scopeStatus;
	private String scopeDescription;
	private int userPreferenceCount;

	/**
	 * Creates a new instance.
	 */
	public ComprehensiveUserContext() {
	}

	/**
	 * Gets the userContextId
	 *
	 * @return the userContextId
	 */
	public Long getUserContextId() {
		return userContextId;
	}

	/**
	 * Sets the userContextId
	 * 
	 * @param userContextId
	 *            new userContextId
	 */
	public void setUserContextId(Long userContextId) {
		this.userContextId = userContextId;
	}

	/**
	 * Gets the role
	 *
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role
	 *
	 * @param role
	 *            new role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Gets the roleStatus
	 *
	 * @return the roleStatus
	 */
	public String getRoleStatus() {
		return roleStatus;
	}

	/**
	 * Sets the roleStatus
	 *
	 * @param roleStatus
	 *            new roleStatus
	 */
	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}

	/**
	 * Gets the roleDescription
	 *
	 * @return the roleDescription
	 */
	public String getRoleDescription() {
		return roleDescription;
	}

	/**
	 * Sets the roleDescription
	 *
	 * @param roleDescription
	 *            new roleDescription
	 */
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	/**
	 * Gets the scope
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope
	 *
	 * @param scope
	 *            new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the scopeStatus
	 *
	 * @return the scopeStatus
	 */
	public String getScopeStatus() {
		return scopeStatus;
	}

	/**
	 * Sets the scopeStatus
	 *
	 * @param scopeStatus
	 *            new scopeStatus
	 */
	public void setScopeStatus(String scopeStatus) {
		this.scopeStatus = scopeStatus;
	}

	/**
	 * Gets the scopeDescription
	 *
	 * @return the scopeDescription
	 */
	public String getScopeDescription() {
		return scopeDescription;
	}

	/**
	 * Sets the scopeDescription
	 *
	 * @param scopeDescription
	 *            new scopeDescription
	 */
	public void setScopeDescription(String scopeDescription) {
		this.scopeDescription = scopeDescription;
	}

	/**
	 * @return the userPreferenceCount
	 */
	public int getUserPreferenceCount() {
		return userPreferenceCount;
	}

	/**
	 * @param userPreferenceCount
	 *            the userPreferenceCount to set
	 */
	public void setUserPreferenceCount(int userPreferenceCount) {
		this.userPreferenceCount = userPreferenceCount;
	}

	/**
	 * @return the roleId
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the scopeId
	 */
	public Long getScopeId() {
		return scopeId;
	}

	/**
	 * @param scopeId the scopeId to set
	 */
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result + ((scopeId == null) ? 0 : scopeId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComprehensiveUserContext other = (ComprehensiveUserContext) obj;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (scopeId == null) {
			if (other.scopeId != null)
				return false;
		} else if (!scopeId.equals(other.scopeId))
			return false;
		return true;
	}

	/**
	 * Formats a human-readable view of this instance.
	 *
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "ComprehensiveUserContext {" + " userContextId=" + userContextId + ", roleId=" + roleId +", role=" + role + ", roleStatus="
				+ roleStatus + ", roleDescription=" + roleDescription+ ", scopeId=" + scopeId + ", scope=" + scope + ", scopeStatus="
				+ scopeStatus + ", scopeDescription=" + scopeDescription + '}';
	}
}