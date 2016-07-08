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
 * Holds an end-point channel.
 */
public class Channel  implements Serializable {
	private static final long serialVersionUID = 2L;
  
  private String dataFlow;
  private String service;
  private Integer priority;

  /**
   * Creates a new instance.
   */
  public Channel() {
  }

  
  /**
   * Get the value of dataFlow
   *
   * @return the value of dataFlow
   */
  public String getDataFlow() {
    return dataFlow;
  }

  /**
   * Set the value of dataFlow
   *
   * @param dataFlow new value of dataFlow
   */
  public void setDataFlow(String dataFlow) {
    this.dataFlow = dataFlow;
  }


  /**
   * Get the value of service
   *
   * @return the value of service
   */
  public String getService() {
    return service;
  }

  /**
   * Set the value of service
   *
   * @param service new value of service
   */
  public void setService(String service) {
    this.service = service;
  }


  /**
   * Get the value of priority
   *
   * @return the value of priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Set the value of priority
   *
   * @param priority new value of priority
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Channel{" + 
            "dataFlow=" + dataFlow + 
            ", service=" + service + 
            ", priority=" + priority + 
            '}';
  }
}