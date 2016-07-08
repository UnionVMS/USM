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

import eu.europa.ec.mare.usm.service.impl.AbstractJdbcDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC based data access object for the retrieval of policy related 
 * information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using JNDI name 'jdbc/USM2'.
 */
public class PolicyDao  extends AbstractJdbcDao  {
  private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDao.class);
  
  /**
   * Creates a new instance.
   */
  public PolicyDao() 
  {
  }

  /**
   * Retrieves the JDBC data-source from the JNDI context.
   *
   * @throws RuntimeException in case the JNDI lookup fails
   */
  @PostConstruct
  public void postConstruct() 
  throws RuntimeException 
  {
    lookupDatasource();
  }


  /**
   * Retrieves policy configuration/definition properties for the provided 
   * subject.
   * 
   * @param subject the policy subject
   * 
   * @return the possibly-empty configuration/definition properties
   */
  public Properties getProperties(String subject) 
  {
    LOGGER.info("getProperties() - (ENTER)");
    
    Properties ret = new Properties();
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select NAME,VALUE from POLICY_T" +
                               " where SUBJECT=?");
      ps.setString(1, subject);
      rs = ps.executeQuery();
      while (rs.next()) {
        String key = rs.getString("NAME");
        String value = rs.getString("VALUE");
        
        ret.setProperty(key, value);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getProperties() - (LEAVE)");
    return ret;
  }

  
}