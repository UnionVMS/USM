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
 * Application details.
 */
public class Application implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name;
  private String description;
  private String parent;

  /**
   * Creates a new instance.
   */
  public Application() {
  }

  /**
   * Get the value of application's name
   *
   * @return the value of application's name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of application's name
   *
   * @param name new value of application's name
   */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	   * Get the value of application's description
	   *
	   * @return the value of application's description
	   */
	public String getDescription() {
		return description;
	}


	/**
	   * Set the value of application's description
	   *
	   * @param description new value of application's description
	   */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	   * Get the value of application's parent
	   *
	   * @return the value of application's parent
	   */
	public String getParent() {
		return parent;
	}

	/**
	   * Set the value of application's parent
	   *
	   * @param parent new value of application's parent
	   */
	public void setParent(String parent) {
		this.parent = parent;
	}


  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Application{" + 
            "name=" + name + 
            ", description=" + description +
            ", parent=" + parent +
            '}';
  }
  
}