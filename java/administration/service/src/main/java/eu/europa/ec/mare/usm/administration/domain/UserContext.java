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
 * Holds summary information about a User
 */
public class UserContext implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long userContextId;
  private String userName;
  private Long roleId;
  private Long scopeId;

  /**
   * Creates a new instance.
   */
  public UserContext() {
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
   * @param userContextId new userContextId
   */
  public void setUserContextId(Long userContextId) {
    this.userContextId = userContextId;
  }
  
  /**
   * Gets the userName
   *
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the userName
   *
   * @param userName new userName
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  /**
   * Gets the roleId
   *
   * @return the roleId
   */
  public Long getRoleId() {
    return roleId;
  }

  /**
   * Sets the roleId
   *
   * @param roleId new roleId
   */
  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }
  
  /**
   * Gets the scopeId
   *
   * @return the scopeId
   */
  public Long getScopeId() {
    return scopeId;
  }

  /**
   * Sets the scopeId
   *
   * @param scopeId new scopeId
   */
  public void setScopeId(Long scopeId) {
    this.scopeId = scopeId;
  }
  
  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "UserContext {"
            + "userContextId=" + userContextId
            + ", userName=" + userName
            + ", roleId=" + roleId
            + ", scopeId=" + scopeId
            + '}';
  }
}