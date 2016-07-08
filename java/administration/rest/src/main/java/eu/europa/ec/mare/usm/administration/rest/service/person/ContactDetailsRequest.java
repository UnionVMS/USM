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
package eu.europa.ec.mare.usm.administration.rest.service.person;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;

/**
 * Holds a request to change a user contact details
 */
public class ContactDetailsRequest extends ContactDetails {
  private String userName;
  private String password;

  /**
   * Creates a new instance
   */
  public ContactDetailsRequest() {
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
   * Get the value of password
   *
   * @return the value of password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the value of password
   *
   * @param password new value of password
   */
  public void setPassword(String password) {
    this.password = password;
  }

}