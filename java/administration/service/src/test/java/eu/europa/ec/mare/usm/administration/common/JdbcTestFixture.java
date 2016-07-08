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
package eu.europa.ec.mare.usm.administration.common;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JdbcTestFixture extends BaseJdbcDao  {
  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTestFixture.class);

  /**
   * Creates a new instance.
   */
  public JdbcTestFixture() {
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

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void update(String sql) 
  {
    update(sql, null);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void update(String sql, Object parameter) 
  {
    Object[] parameters = null;
    
    if (parameter != null ) {
      parameters = new Object[1];
      parameters[0] = parameter;
    }
    
    update(sql, parameters);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void update(String sql, Object[] parameters) 
  {
    LOGGER.info("update(" + sql +", " + parameters + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement upd = null;
    
    try {
      co = getConnection();
      upd = co.prepareStatement(sql);
      if (parameters != null) {
        for (int i = 0; i < parameters.length; i++) {
          bindParameter(upd, i + 1, parameters[i]);
        }
      }      
      
      int cnt = upd.executeUpdate();
      LOGGER.debug("Row count: " + cnt);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(upd);
      closeConnection(co);
    }
    
    LOGGER.info("update() - (LEAVE)");
  }

  protected void handleException(Exception ex) 
  throws RuntimeException 
  {
    String msg = "Failed to execute statement: " + ex.getMessage();
    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

  
  
}