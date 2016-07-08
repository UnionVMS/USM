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
package eu.europa.ec.mare.usm.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for JDBC based data access objects.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using a configurable JNDI name (default is 'jdbc/USM2').
 */
public class AbstractJdbcDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJdbcDao.class);
  private static final String FAILED_TO_EXECUTE_QUERY = "Failed to execute query: ";
  private static final String DATASOURCE_NAME = "jdbc/USM2";
  private String dataSourceName = DATASOURCE_NAME;
  private DataSource dataSource;

  /**
   * Creates a new instance
   */
  protected AbstractJdbcDao() {
  }

  /**
   * Gets the JNDI name of the JDBC data-source used
   * 
   * @return the JNDI name of the JDBC data-source
   */
  public String getDataSourceName() {
    return dataSourceName;
  }

  /**
   * Sets the JNDI name of the JDBC data-source to be used.
   * 
   * @param dataSourceName JNDI name of the JDBC data-source
   */
  public void setDataSourceName(String dataSourceName) 
  {
    this.dataSourceName = dataSourceName;
  }

  /**
   * Retrieves the JDBC data-source from the JNDI context using the configured
   * JNDI name (default 'jdbc/USM2').
   *
   * @throws RuntimeException in case the JNDI lookup fails
   */
  protected void lookupDatasource() 
  throws RuntimeException 
  {
    LOGGER.info("lookupDatasource() - (ENTER)");
    try {
      Context context = new InitialContext();
      dataSource = (DataSource) context.lookup(dataSourceName);
      context.close();
    } catch (NamingException ex) {
      String msg = "Failed to lookup data-source: " + dataSourceName;
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }
    LOGGER.info("lookupDatasource() - (LEAVE)");
  }

  protected Connection getConnection() 
  throws SQLException
  {
    if (dataSource == null) {
      lookupDatasource();
    }
    return dataSource.getConnection();
  }
  
  protected void handleException(Exception ex) 
  throws RuntimeException 
  {
    String msg = FAILED_TO_EXECUTE_QUERY + ex.getMessage();
    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

  protected void closeConnection(Connection co) 
  {
    if (co != null) {
      try {
        co.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing connection", ex);
      }
    }
  }

  protected void closeStatement(PreparedStatement ps) 
  {
    if (ps != null) {
      try {
        ps.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing statement", ex);
      }
    }
  }

  protected void closeResultSet(ResultSet rs) 
  {
    if (rs != null) {
      try {
        rs.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing ResultSet", ex);
      }
    }
  }
  
}