package eu.europa.ec.mare.usm.administration.common.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC based data access object for the retrieval of information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using JNDI name 'jdbc/USM2'.
 */
public class BaseJdbcDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseJdbcDao.class);
  private static final String FAILED_TO_EXECUTE_QUERY = "Failed to execute query: ";
  private static final String DATASOURCE_NAME = "jdbc/USM2";
  private DataSource dataSource;

  
  /**
   * Creates a new instance.
   */
  public BaseJdbcDao() 
  {
  }

  /**
   * Retrieves the JDBC data-source from the JNDI context using JNDI name
   * 'jdbc/USM2'.
   *
   * @throws RuntimeException in case the JNDI lookup fails
   */
  @PostConstruct
  public void lookupDatasource() 
  {
    LOGGER.info("lookupDatasource() - (ENTER)");
    
    try {
      Context context = new InitialContext();
      dataSource = (DataSource) context.lookup(DATASOURCE_NAME);
      context.close();
    } catch (NamingException ex) {
      String msg = "Failed to lookup data-source: " + DATASOURCE_NAME;
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
  
  /**
   * Gets the list of objects matching the given query.
   *
   * @param query the query to be executed
   *
   * @param rm the RowMapper to be used to map results to objects
   *
   * @return the possibly-empty list of objects
   */
  public List queryForList(Query query, RowMapper rm) 
  {
    LOGGER.info("queryForList(" + query + ") - (ENTER)");
    
    List ret = new ArrayList<>();
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement(query.getStatement());
      for (int i = 0; i < query.getParameters().size(); i++) {
        bindParameter(ps, i + 1, query.getParameters().get(i));
      }
      rs = ps.executeQuery();
      while (rs.next()) {
        ret.add(rm.mapRow(rs));
      }
    } catch (Exception ex) {
      logException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("queryForList() - (LEAVE)");
    return ret;
  }

  /**
   * Gets the (single) object matching the given query.
   *
   * @param query the query to be executed
   *
   * @param rm the RowMapper to be used to map results to objects
   *
   * @return the retrieved object if the query matched a single row, 
   * <i>null</i> otherwise
   */
  public Object queryForObject(Query query, RowMapper rm) 
  {
    LOGGER.info("queryForObject(" + query + ") - (ENTER)");
    
    Object ret =null;
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement(query.getStatement());
      for (int i = 0; i < query.getParameters().size(); i++) {
        bindParameter(ps, i + 1, query.getParameters().get(i));
      }
      rs = ps.executeQuery();
      if (rs.next()) {
        Object tm = rm.mapRow(rs);
        
        // Ensure there are no other results
        if (!rs.next()) {
          ret = tm;
        }
      }
    } catch (Exception ex) {
      logException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("queryForObject() - (LEAVE)");
    return ret;
  }
  
  
  /**
   * Checks whether the given query matches at least one row.
   *
   * @param query the query to be executed
   *
   * @return <i>true</i> if the query matches at least one row, 
   * <i>false</i> otherwise
   */
  public boolean queryForExistence(Query query) 
  {
    LOGGER.info("queryForExistence(" + query + ") - (ENTER)");
    
    boolean ret = false;
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement(query.getStatement());
      for (int i = 0; i < query.getParameters().size(); i++) {
        bindParameter(ps, i + 1, query.getParameters().get(i));
      }
      rs = ps.executeQuery();
      
      ret = rs.next();
    } catch (Exception ex) {
      logException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("queryForExistence() - (LEAVE)");
    return ret;
  }
  
  
  public int queryForCount(Query query){
	  LOGGER.info("queryForCount(" + query + ") - (ENTER)");
	  int ret = 0;
	    Connection co = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    try {
	      co = dataSource.getConnection();
	      ps = co.prepareStatement(query.getStatement());
	      for (int i = 0; i < query.getParameters().size(); i++) {
	        bindParameter(ps, i + 1, query.getParameters().get(i));
	      }
	      rs = ps.executeQuery();
	      if (rs.next()){
	    	  ret=rs.getInt(1);
	      }
	    } catch (Exception ex) {
	      logException(ex);
	    } finally {
	      closeResultSet(rs);
	      closeStatement(ps);
	      closeConnection(co);
	    }
	    
	    LOGGER.info("queryForCount() - (LEAVE)");
	    return ret;
  }
  
  /**
   * Gets the total number of records that the given query will fetch.
   *
   * @param query the query to be executed
   * 
   * @param rm the RowMapper to be used to map results to objects
   *
   * @return the total number of records 
   */
  public int queryForTotalRecords(Query query, RowMapper rm) 
  {
    LOGGER.info("queryForTotalRecords(" + query + ") - (ENTER)");
    
    int ret = 0;
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement(query.getStatement());
      for (int i = 0; i < query.getParameters().size(); i++) {
        bindParameter(ps, i + 1, query.getParameters().get(i));
      }
      rs = ps.executeQuery();
      if (rs.next()) {
    	  ret = (int)rm.mapRow(rs);
      }
      
    } catch (Exception ex) {
      logException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("queryForExistence() - (LEAVE)");
    return ret;
  }
  
  
	public void bindParameter(PreparedStatement stm, int idx, Object param)
  throws SQLException 
  {
		if (param == null) {
			stm.setObject(idx, param);
		} else {
			if (param instanceof String) {
				stm.setString(idx, (String) param);
			} else if (param instanceof Integer) {
				stm.setInt(idx, (Integer) param);
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

	public Timestamp getTimestamp(Date date) 
  {
		Timestamp ret = null;

		if (date != null) {
			ret = new Timestamp(date.getTime());
		}

		return ret;
	}


  public void logException(Exception ex) 
  throws RuntimeException 
  {
    String msg = FAILED_TO_EXECUTE_QUERY + ex.getMessage();
    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

  public void closeConnection(Connection co) {
    if (co != null) {
      try {
        co.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing connection", ex);
      }
    }
  }

  public void closeStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing statement", ex);
      }
    }
  }

  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (Exception ex) {
        LOGGER.info("Error closing ResultSet", ex);
      }
    }
  }
  
  
  public static class StringMapper implements RowMapper {

    public StringMapper() {
    }

    @Override
    public String mapRow(ResultSet rs) 
    throws SQLException 
    {
      return rs.getString(1);
    }
  }

  
}
