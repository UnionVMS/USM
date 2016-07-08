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

public class Permission implements Serializable {
  private static final long serialVersionUID = 2L;

  private Long roleId;
  private Long featureId;

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  @Override
  public String toString() {
    return "Permission ["
            + "roleId=" + roleId
            + ", featureId=" + featureId
            + "]";
  }

}