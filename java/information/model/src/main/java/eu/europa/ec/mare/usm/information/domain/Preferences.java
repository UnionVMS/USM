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
 * Holds a set of Preferences
 */
public class Preferences implements Serializable {
	private static final long serialVersionUID = 1L;
  private Set<Preference> preferences;

  /**
   * Creates a new instance
   */
  public Preferences() {
  }

  
  /**
   * Get the value of preferences
   *
   * @return the value of preferences
   */
  public Set<Preference> getPreferences() {
    return preferences;
  }

  /**
   * Set the value of preferences
   *
   * @param preferences new value of preferences
   */
  public void setPreferences(Set<Preference> preferences) {
    this.preferences = preferences;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Preferences{" + "preferences=" + preferences + '}';
  }
}