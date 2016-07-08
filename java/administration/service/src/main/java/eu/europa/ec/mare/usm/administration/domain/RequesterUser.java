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
 * Base class for user retrieval requests.
 */
public class RequesterUser implements Serializable {
  private static final long serialVersionUID = 1L;
  private String requesterUserName;

  /**
   * Gets the value of requestedUserName
   *
   * @return the value of requestedUserName
   */
  public String getRequesterUserName() {
    return requesterUserName;
  }

  /**
   * Sets the value of requestedUserName
   *
   * @param requesterUserName new value of requestedUserName
   */
  public void setRequesterUserName(String requesterUserName) {
    this.requesterUserName = requesterUserName;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "RequesterUser{" + "requestedUserName=" + requesterUserName + '}';
  }

}