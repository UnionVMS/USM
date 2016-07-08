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
package eu.europa.ec.mare.usm.policy.service.impl;

import java.io.Serializable;
import java.util.Properties;

/**
 * Holds definition/configuration properties for a specific policy.
 */
public class PolicyDefinition implements Serializable {
  private String subject;
  private Properties properties;

  /**
   * Creates a new instance.
   */
  public PolicyDefinition() {
  }

  /**
   * Get the value of subject
   *
   * @return the value of subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Set the value of subject
   *
   * @param subject new value of subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Get the value of properties
   *
   * @return the value of properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * Set the value of properties
   *
   * @param properties new value of properties
   */
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "PolicyDefiniion{" + 
            "subject=" + subject + 
            ", properties=" + properties + 
            '}';
  }
  
}