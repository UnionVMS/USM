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
import java.util.Date;
import java.util.Set;

/**
 * Holds a Scope based on a set of datasets from any applications. 
 * The goal is to limit the visibility on data.
 */
public class Scope implements Serializable {
	private static final long serialVersionUID = 1L;
  private String scopeName;
  private Date activeFrom;
  private Date activeTo;
  private Date dataFrom;
  private Date dataTo;
  private Set<DataSet> datasets;

  /**
   * Creates a new instance
   */
  public Scope() {
  }

  /**
   * Get the value of scopeName
   *
   * @return the value of scopeName
   */
  public String getScopeName() {
    return scopeName;
  }

  /**
   * Set the value of scopeName
   *
   * @param scopeName new value of scopeName
   */
  public void setScopeName(String scopeName) {
    this.scopeName = scopeName;
  }

  /**
   * Get the value of activeFrom
   *
   * @return the value of activeFrom
   */
  public Date getActiveFrom() {
    return activeFrom;
  }

  /**
   * Set the value of activeFrom
   *
   * @param activeFrom new value of activeFrom
   */
  public void setActiveFrom(Date activeFrom) {
    this.activeFrom = activeFrom;
  }

  /**
   * Get the value of activeTo
   *
   * @return the value of activeTo
   */
  public Date getActiveTo() {
    return activeTo;
  }

  /**
   * Set the value of activeTo
   *
   * @param activeTo new value of activeTo
   */
  public void setActiveTo(Date activeTo) {
    this.activeTo = activeTo;
  }

  /**
   * Get the value of dataFrom
   *
   * @return the value of dataFrom
   */
  public Date getDataFrom() {
    return dataFrom;
  }

  /**
   * Set the value of dataFrom
   *
   * @param dataFrom new value of dataFrom
   */
  public void setDataFrom(Date dataFrom) {
    this.dataFrom = dataFrom;
  }

  /**
   * Get the value of dataTo
   *
   * @return the value of dataTo
   */
  public Date getDataTo() {
    return dataTo;
  }

  /**
   * Set the value of dataTo
   *
   * @param dataTo new value of dataTo
   */
  public void setDataTo(Date dataTo) {
    this.dataTo = dataTo;
  }

  /**
   * Get the value of datasets
   *
   * @return the value of datasets
   */
  public Set<DataSet> getDatasets() {
    return datasets;
  }

  /**
   * Set the value of datasets
   *
   * @param datasets new value of datasets
   */
  public void setDatasets(Set<DataSet> datasets) {
    this.datasets = datasets;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Scope{" + 
            "scopeName=" + scopeName + 
            ", activeFrom=" + activeFrom + 
            ", activeTo=" + activeTo + 
            ", dataFrom=" + dataFrom + 
            ", dataTo=" + dataTo + 
            ", datasets=" + datasets + 
            '}';
  }

  
}