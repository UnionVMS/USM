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

/**
 * Defines all known User Statuses
 */
public enum UserStatus {

  /**
   * The User is enabled and may hence use the system
   */
  ENABLED("E"),
  /**
   * The User is (permanently) disabled and may not use the system
   */
  DISABLED("D"),
  /**
   * The User is (temporarily) locked, probably due to consecutive
   * authentication failures *
   */
  LOCKED("L");

  private final String value;

  UserStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}