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
 * Holds a Context.<br/>
 * A context is an association of three elements:
 * <ul>
 * <li>A scope (optional) based on a set of datasets from any 
 * applications. The goal  is to limit the visibility on data;</li>
 * <li>A role (mandatory) based on a set a features from any applications. 
 * The goal is  to define the actions that can be performed on data;</li>
 * <li>A set of contextual preferences (optional): These preferences are not 
 * managed through the USM user interface. B;</li>
 * </ul>
 * A context is defined by an USM administrator by associating it with a user. 
 * It is his responsibility to guarantee that the different elements composing 
 * the context are coherent from a business point of view;<br/>
 * A user may have no context if no role has been associated to him. 
 * He may have multiple contexts.;<br/>
 * The USM administrator cannot define the default context of the user. 
 * If an application wants to provide such a feature it may do so using a 
 * preference;<br/>
 */
public class Context implements Serializable {
	private static final long serialVersionUID = 1L;
  private Role role;
  private Scope scope;

  /**
   * Creates a new instance
   */
  public Context() {
  }

  /**
   * Get the value of role
   *
   * @return the value of role
   */
  public Role getRole() {
    return role;
  }

  /**
   * Set the value of role
   *
   * @param role new value of role
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * Get the value of scope
   *
   * @return the value of scope
   */
  public Scope getScope() {
    return scope;
  }

  /**
   * Set the value of scope
   *
   * @param scope new value of scope
   */
  public void setScope(Scope scope) {
    this.scope = scope;
  }

  private Preferences preferences;

  /**
   * Get the value of preferences
   *
   * @return the value of preferences
   */
  public Preferences getPreferences() {
    return preferences;
  }

  /**
   * Set the value of preferences
   *
   * @param preferences new value of preferences
   */
  public void setPreferences(Preferences preferences) {
    this.preferences = preferences;
  }

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Context{" + 
            "role=" + role + 
            ", scope=" + scope + 
            ", preferences=" + preferences + 
            '}';
  }
}