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
 * Holds user details retrieved from an LDAP compatible Identity 
 * Management system.
 */
public class LdapUser implements Serializable {
  private static final long serialVersionUID = 1L;

  private String userName;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String mobileNumber;
  private String faxNumber;
  private String email;

  /**
   * Creates a new instance.
   */
  public LdapUser() {
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
   * Gets the firstName
   *
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the firstName
   *
   * @param firstName new firstName
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the lastName
   *
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the lastName
   *
   * @param lastName new lastName
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the phoneNumber
   *
   * @return the phoneNumber
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the phoneNumber
   *
   * @param phoneNumber new phoneNumber
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * Gets the mobileNumber
   *
   * @return the mobileNumber
   */
  public String getMobileNumber() {
    return mobileNumber;
  }

  /**
   * Sets the mobileNumber
   *
   * @param mobileNumber new mobileNumber
   */
  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  /**
   * Gets the faxNumber
   *
   * @return the faxNumber
   */
  public String getFaxNumber() {
    return faxNumber;
  }

  /**
   * Sets the faxNumber
   *
   * @param faxNumber new faxNumber
   */
  public void setFaxNumber(String faxNumber) {
    this.faxNumber = faxNumber;
  }

  /**
   * Gets the email
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email
   *
   * @param email new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "LdapUser{"
            + "userName=" + userName
            + ", firstName=" + firstName
            + ", phoneNumber=" + phoneNumber
            + ", mobileNumber=" + mobileNumber
            + ", faxNumber=" + faxNumber
            + ", email=" + email
            + '}';
  }

}