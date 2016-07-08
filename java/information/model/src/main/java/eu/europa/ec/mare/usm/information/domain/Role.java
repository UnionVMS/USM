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
import java.util.Set;

/**
 * Holds a Role based on a set a features from any applications. 
 * The goal is to define the actions that can be performed on data;.
 */
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;
  private String roleName;
  private Set<Feature> features;

  /**
   * Creates a new instance
   */
  public Role() {
  }

  /**
   * Get the value of roleName
   *
   * @return the value of roleName
   */
  public String getRoleName() {
    return roleName;
  }

  /**
   * Set the value of roleName
   *
   * @param roleName new value of roleName
   */
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
  
  /**
   * Get the value of features
   *
   * @return the value of features
   */
  public Set<Feature> getFeatures() {
    return features;
  }

  /**
   * Set the value of features
   *
   * @param features new value of features
   */
  public void setFeatures(Set<Feature> features) {
    this.features = features;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Role{" + 
             "roleName=" + roleName + 
            ", features=" + features + 
            '}';
  }
}