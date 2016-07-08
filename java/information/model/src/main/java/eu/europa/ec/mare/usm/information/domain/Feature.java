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
 * Holds a Feature.
 * <ul>
 * <li>It can be optionally associated to a group to identify rapidly all 
 * features acting on a same business entity: ex: feature "create organisation" 
 * to the group "organisation";</li> 
 * <li>It is attached to the application in which it has been defined;</li>
 * <li>A feature is defined in the configuration file of that application and 
 * delivered to USM at deployment time;</li>
 * <li>A feature is not managed (CRUD) by USM but by the application exposed 
 * it;</li>
 * </ul>
 */
public class Feature implements Serializable {
	private static final long serialVersionUID = 1L;
  private String applicationName;
  private String featureName;

  /**
   * Creates a new instance
   */
  public Feature() {
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
   * Get the value of featureName
   *
   * @return the value of featureName
   */
  public String getFeatureName() {
    return featureName;
  }

  /**
   * Set the value of featureName
   *
   * @param featureName new value of featureName
   */
  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Feature{" + 
            "applicationName=" + applicationName + 
            ", featureName=" + featureName + 
            '}';
  }

}