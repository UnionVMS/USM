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
package eu.europa.ec.mare.usm.administration.common.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps rows form a ResultSet to Objects
 */
public interface RowMapper {
  /**
   * Maps one row form a ResultSet to an Object
   * 
   * @param rs the ResultSet to be mapped
   * 
   * @return the mapped object
   * 
   * @throws SQLException in case of mapping error
   */
  public Object mapRow(ResultSet rs) 
  throws SQLException;
}