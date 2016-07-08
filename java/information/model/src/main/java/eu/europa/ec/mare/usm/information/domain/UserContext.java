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
 * Holds a UserContext.<br/>
 */
public class UserContext implements Serializable {
	private static final long serialVersionUID = 1L;
  private String userName;
  private String applicationName;
  private ContextSet contextSet;

  /**
   * Creates a new instance
   */
  public UserContext() {
  }

  /**
   * Get the value of userName
   *
   * @return the value of userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set the value of userName
   *
   * @param userName new value of userName
   */
  public void setUserName(String userName) {
    this.userName = userName;
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
   * @param applicationName new value of applicationName
   */
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Get the value of contextSet
   *
   * @return the value of contextSet
   */
  public ContextSet getContextSet() {
    return contextSet;
  }

  /**
   * Set the value of contextSet
   *
   * @param contextSet new value of contextSet
   */
  public void setContextSet(ContextSet contextSet) {
    this.contextSet = contextSet;
  }

}