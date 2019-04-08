package eu.europa.ec.mare.usm.authentication.service;

import eu.europa.ec.mare.usm.service.impl.AbstractJdbcDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
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
public class JdbcTestFixture extends AbstractJdbcDao  {
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
  public String select(String sql, Object[] parameters) 
  {
    LOGGER.info("select(" + sql +", " + parameters + ") - (ENTER)");
    
    String ret = null;
    
    Connection co = null;
    PreparedStatement sel = null;
    
    try {
      co = getConnection();
      sel = co.prepareStatement(sql);
      if (parameters != null) {
        for (int i = 0; i < parameters.length; i++) {
          bindParameter(sel, i + 1, parameters[i]);
        }
      }      
      
      ResultSet rs = sel.executeQuery();
      rs.next();
      ret = rs.getString(1);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(sel);
      closeConnection(co);
    }
    LOGGER.info("select() - (LEAVE)");
    return ret;

  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public Integer selectID(String sql, String parameter) 
  {
    LOGGER.info("select(" + sql +", " + parameter + ") - (ENTER)");
    
    Integer ret = null;
    
    Connection co = null;
    PreparedStatement sel = null;
    
    try {
      co = getConnection();
      sel = co.prepareStatement(sql);
      bindParameter(sel, 1, parameter);
      
      ResultSet rs = sel.executeQuery();
      rs.next();
      ret = (int) rs.getInt(1);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(sel);
      closeConnection(co);
    }
    LOGGER.info("select() - (LEAVE)");
    return ret;

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
  
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void delete(String sql, Object[] parameters) 
  {
    LOGGER.info("delete(" + sql +", " + parameters + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement del = null;
    
    try {
      co = getConnection();
      del = co.prepareStatement(sql);
      if (parameters != null) {
        for (int i = 0; i < parameters.length; i++) {
          bindParameter(del, i + 1, parameters[i]);
        }
      }      
      
      int cnt = del.executeUpdate();
      LOGGER.debug("delete row count: " + cnt);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(del);
      closeConnection(co);
    }
    
    LOGGER.info("delete() - (LEAVE)");
  }

  /**
   * Binds a parameter to a prepared statement, using the pertinent
   * setter depending on the parameter type. 
   * 
   * @param stm the prepared statement
   * @param idx the parameter index
   * @param param the parameter value
   * 
   * @throws SQLException 
   */
  private void bindParameter(PreparedStatement stm, int idx, Object param) 
  throws SQLException 
  {
    if (param == null) {
      stm.setObject(idx, param);
    } else {
      if (param instanceof String) {
        stm.setString(idx, (String) param);
      } else if (param instanceof Integer) {
        stm.setInt(idx, (Integer) param);
      } else if (param instanceof Long) {
        stm.setLong(idx, (Long) param);
      } else if (param instanceof Float) {
        stm.setFloat(idx, (Float) param);
      } else if (param instanceof Date) {
        stm.setTimestamp(idx, getTimestamp((Date) param));
      } else if (param instanceof Timestamp) {
        stm.setTimestamp(idx, (Timestamp) param);
      } else if (param instanceof java.sql.Date) {
        stm.setDate(idx, (java.sql.Date) param);
      } else {
        stm.setObject(idx, param);
      }
    }
  }

  /**
   * Converts the provided Date to a Timestamp.
   * 
   * @param date the Date to be converted
   * 
   * @return the resulting Timestamp
   */
  protected Timestamp getTimestamp(Date date) 
  {
    Timestamp ret = null;
    
    if (date != null) {
      ret = new Timestamp(date.getTime());
    }
    
    return ret;
  }

  
}
