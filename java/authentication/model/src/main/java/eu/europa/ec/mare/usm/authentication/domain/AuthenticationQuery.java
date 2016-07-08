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
package eu.europa.ec.mare.usm.authentication.domain;

import java.io.Serializable;

/**
 * Base class for authentication requests.
 */
public class AuthenticationQuery implements Serializable {
	
	private static final long serialVersionUID = -9095642037256734250L;
	private String userName;

  /**
   * Creates a new instance.
   */
  public AuthenticationQuery() {
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
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "AuthenticationQuery{" + "userName=" + userName + '}';
  }

  
}