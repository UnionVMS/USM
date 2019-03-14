package eu.europa.ec.mare.usm.administration.service.userContext.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.ComprehensiveUserContext;
import eu.europa.ec.mare.usm.administration.domain.UserContextResponse;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using JNDI name 'jdbc/USM2'.
 */
public class UserContextJdbcDao extends BaseJdbcDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserContextJdbcDao.class.getName());

  /**
   * Creates a new instance.
   */
  public UserContextJdbcDao() 
  {
  }

  /**
   * Retrieves a list of user roles according to the request
   * 
   * @param userName a FindUserContextsQuery request.
   * 
   * @return a UserContextResponse
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public UserContextResponse findUserContexts(String userName ) 
  {
    LOGGER.info("findUserContexts(" + userName + ") - (ENTER)");
    
    Query query = getFindContextsQuery(userName);
    
    LOGGER.info("query: " + query);
    
    List<ComprehensiveUserContext> lst = queryForList(query, new UserContextMapper());
    UserContextResponse ret = new UserContextResponse();
    ret.setResults(lst);
    
    LOGGER.info("findUserContexts() - (LEAVE): " + ret);
    return ret;
  }
  
  private static class UserContextMapper implements RowMapper {

    public UserContextMapper() {
    }

    @Override
    public Object mapRow(ResultSet rs) 
    throws SQLException 
    {
      ComprehensiveUserContext ret = new ComprehensiveUserContext();
      
      if (rs.getObject("USER_CONTEXT_ID") != null) {
        ret.setUserContextId(rs.getLong("USER_CONTEXT_ID"));
      }
      if (rs.getObject("ROLE_ID") != null) {
        ret.setRoleId(rs.getLong("ROLE_ID"));
      }
      ret.setRole(rs.getString("ROLE_NAME"));
      ret.setRoleDescription(rs.getString("ROLE_DESCRIPTION"));
      ret.setRoleStatus(rs.getString("ROLE_STATUS"));
      
      if (rs.getObject("SCOPE_ID") != null) {
        ret.setScopeId(rs.getLong("SCOPE_ID"));
      }
      ret.setScope(rs.getString("SCOPE_NAME"));
      ret.setScopeDescription(rs.getString("SCOPE_DESCRIPTION"));
      ret.setScopeStatus(rs.getString("SCOPE_STATUS"));
      
      if (rs.getObject("PREF_NUMBER") != null) {
        ret.setUserPreferenceCount(rs.getInt("PREF_NUMBER"));
      }    	
      return ret;
    }
  }
  
  private Query getFindContextsQuery(String userName)
  {
	  String select = "select uc.USER_CONTEXT_ID," + 
    " sc.SCOPE_ID, sc.NAME AS SCOPE_NAME, sc.DESCRIPTION AS SCOPE_DESCRIPTION, sc.STATUS AS SCOPE_STATUS," + 
    " rl.ROLE_ID, rl.NAME AS ROLE_NAME, rl.DESCRIPTION AS ROLE_DESCRIPTION, rl.STATUS AS ROLE_STATUS," + 
    " (select count(preference_id) from PREFERENCE_T pref where pref.user_context_id = uc.USER_CONTEXT_ID) PREF_NUMBER" + 
    " from USER_T u " + 
    " join USER_CONTEXT_T uc on uc.user_id = u.user_id "+ 
    " join ROLE_T rl on rl.role_id = uc.role_id "+
    " left outer join SCOPE_T sc on sc.scope_id = uc.scope_id "+ 
    " where 1=1";
	  
	  Query ret = new Query();
	  ret.append(select);
	  if (userName!= null)  {
	    	ret.append(" and u.user_name=?").add(userName);
	  }
	  return ret;
  }
  
  /**
   * Checks whether a role exists with the provided name for the 
   * specific application
   * 
   * @param userContextId the user role id
   * 
   * @return <i>true</i> if the userContext exists, <i>false</i> otherwise
   */
  public boolean userContextExists( Long userContextId) 
  {
    LOGGER.info("userContextExists(" + userContextId + ") - (ENTER)");

    Query query = new Query("select 1 from USER_CONTEXT_T uc" + 
                            " where  uc.USER_CONTEXT_ID=?");
    query.add(userContextId);
    
    boolean ret = queryForExistence(query);
    
    LOGGER.info("userContextExists() - (LEAVE): " + ret);
    return ret;
  }
  
  
  public boolean userContextExists(String userName,Long roleId , Long scopeId){
	  
	  LOGGER.info("userContextExists(" + userName+", roleId="+roleId+", scopeId="+scopeId + ") - (ENTER)");
	  
	 String initialQuery="select 1 from USER_CONTEXT_T uc, USER_T us" + 
             " where us.user_name=? and uc.USER_ID=us.user_id and uc.role_id=? ";
	 if (scopeId!=null){
			 initialQuery+=" and uc.scope_id=? ";  
	 }
	  Query query = new Query(initialQuery);
	  
	  query.add(userName);
	  query.add(roleId);
	  if (scopeId!=null){
		  query.add(scopeId);  
	 }
	  
	
	  
	  boolean ret = queryForExistence(query);
	  LOGGER.info("userContextExists() - (LEAVE): " + ret);
	  return ret;
  }
  
 
}