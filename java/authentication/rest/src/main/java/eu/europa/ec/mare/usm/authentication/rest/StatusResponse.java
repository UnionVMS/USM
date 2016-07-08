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
package eu.europa.ec.mare.usm.authentication.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds a status response
 */
@XmlRootElement
public class StatusResponse implements Serializable {
  private int statusCode;
  private String statusMessage;

  /**
   * Creates a new instance
   */
  public StatusResponse() {
  }

  /**
   * Get the value of statusCode
   *
   * @return the value of statusCode
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Set the value of statusCode
   *
   * @param statusCode new value of statusCode
   */
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Get the value of statusMessage
   *
   * @return the value of statusMessage
   */
  public String getMessage() {
    return statusMessage;
  }

  /**
   * Set the value of statusMessage
   *
   * @param statusMessage new value of statusMessage
   */
  public void setMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "StatusResponse{" + 
            "statusCode=" + statusCode + 
            "message=" + statusMessage + 
            '}';
  }

}