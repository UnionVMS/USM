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
package eu.europa.ec.mare.usm.authentication.service.impl;

import eu.europa.ec.mare.usm.service.impl.AbstractJdbcDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import java.util.Date;
import javax.annotation.PostConstruct;

/**
 * Stateless Session Bean, JDBC based data access object for the authentication 
 * of users.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AuthenticationDao extends AbstractJdbcDao  {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDao.class);
  
  private static final String[] PERSON_COLUMNS = {"first_name", "last_name", "phone_number", "mobile_number",
		  "fax_number", "e_mail"};
  
  /**
   * Creates a new instance.
   */
  public AuthenticationDao() 
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
   * Retrieves the unique identifier (PK) of the user with the given name and 
   * password.
   * 
   * @param userName the user name
   * @param password the MD5 hash for the user password
   * 
   * @return the unique identifier of the user if it exists, is active, is 
   * neither disabled nor locked-out, and has the given password; or null 
   * otherwise.
   */
  public Long getActiveUserId(String userName, String password) 
  {
    LOGGER.info("getActiveUserId(" + userName + ") - (ENTER)");
    
    Long ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select USER_ID from active_user_v" +
                               " where user_name=?" +
                               "   and password=?");
      ps.setString(1, userName);
      ps.setString(2, password);
      rs = ps.executeQuery();
      if (rs.next()) {
        if (rs.getObject(1) != null){
          ret = rs.getLong(1);
        }
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getActiveUserId() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the lockout reason of the user with the given name.
   * 
   * @param userName the user name
   * 
   * @return the lockout reason of the user if it exists, is 
   * locked-out, and has the given password; or null otherwise.
   */
  public String getLockoutReason(String userName) 
  {
    LOGGER.info("getLockoutReason(" + userName + ") - (ENTER)");
    
    String ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select LOCKOUT_REASON from USER_T" +
                               " where user_name=?");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      if (rs.next()) {
        if (rs.getObject(1) != null){
          ret = rs.getString(1);
        }
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getLockoutReason() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the status of the user with the given name.
   * 
   * @param userName the user name
   * 
   * @return the user status, if it exists, null otherwise
   */
  public String getUserStatus(String userName) 
  {
    LOGGER.info("getUserStatus(" + userName + ") - (ENTER)");
    
    String ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select STATUS from USER_T" +
                               " where user_name=?");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      if (rs.next()) {
        if (rs.getObject(1) != null){
          ret = rs.getString(1);
        }
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getUserStatus() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the unique identifier (PK) of the user matching the given 
   * challenge response.
   * 
   * @param request the challenge response
   * 
   * @return the unique identifier of the user if it exists, is active, is 
   * neither disabled nor locked-out, and matches the given challenge response; 
   * or null otherwise.
   */
  public Long getActiveUserId(ChallengeResponse request) 
  {
    LOGGER.info("getActiveUserId() - (ENTER)");
    
    Long ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select c.USER_ID" +
                               " from active_user_v u, challenge_t c" +
                               " where c.USER_ID=u.USER_ID" +
                               "  and u.USER_NAME=?" +
                               "  and c.CHALLENGE=?" +
                               "  and c.RESPONSE=?");
      ps.setString(1, request.getUserName());
      ps.setString(2, request.getChallenge());
      ps.setString(3, request.getResponse());
      rs = ps.executeQuery();
      if (rs.next()) {
        if (rs.getObject(1) != null){
          ret = rs.getLong(1);
        }
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getActiveUserId() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves the list of challenges defined for the given user.
   * 
   * @param userName the user name
   * 
   * @return the possibly-empty list of defined challenges
   */
  public List<ChallengeResponse> getUserChallenges(String userName) 
  {
    LOGGER.info("getUserChallenges(" + userName + ") - (ENTER)");
    
    List<ChallengeResponse> ret = new ArrayList<>();
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = getConnection();
      ps = co.prepareStatement("select u.USER_NAME, c.CHALLENGE" +
                               " from active_user_v u, challenge_t c" +
                               " where c.USER_ID=u.USER_ID" +
                               "  and u.USER_NAME=?" +
                               " order by CHALLENGE desc");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      while (rs.next()) {
        ChallengeResponse item = new ChallengeResponse();
        item.setUserName(rs.getString("USER_NAME"));
        item.setChallenge(rs.getString("CHALLENGE"));
        
        ret.add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getUserChallenges() - (LEAVE)");
    return ret;
  }

  /**
   * Records a successful login of the given user.
   * 
   * @param userName the user name
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void recordLoginSuccess(String userName) 
  {
    LOGGER.info("recordLoginSuccess(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    
    try {
      co = getConnection();
      ps = co.prepareStatement("update USER_T set " + 
                                " LAST_LOGON=?," + 
                                " LOGON_FAILURE=0, " +
                                " LOCKOUT_REASON=null, " + 
                                " LOCKOUT_TO=null"+
                                " where USER_NAME=?");
      ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
      ps.setString(2, userName);
      int cnt = ps.executeUpdate();
      LOGGER.debug("Row count: " + cnt);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("recordLoginSuccess() - (LEAVE)");
  }

  /**
   * Gets the password expiry time for the given user.
   * 
   * @param userName the user name
   * 
   * @return the password expiry time of the user
   */
  public Date getPasswordExpiry(String userName) 
  {
    LOGGER.info("getPasswordExpiry(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    Date ret = null;
    
    try {
      co = getConnection();
      ps = co.prepareStatement("select PASSWORD_EXPIRY from USER_T" + 
                               " where USER_NAME=?");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      if (rs.next()) {
        ret = rs.getDate(1);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getPasswordExpiry() - (LEAVE)");
    return ret;
  }

  /**
   * Records a failed login for the given user.
   * 
   * @param userName the user name
   * 
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void recordLoginFailure(String userName) 
  {
    LOGGER.info("recordLoginFailure(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement upd = null;
    
    try {
      co = getConnection();
      upd = co.prepareStatement("update USER_T set LOGON_FAILURE=1 + " +
                               " coalesce(LOGON_FAILURE, 0)" + 
                               " where USER_NAME=?");
      upd.setString(1, userName);
      int cnt = upd.executeUpdate();
      LOGGER.debug("Row count: " + cnt);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(upd);
      closeConnection(co);
    }
    
    LOGGER.info("recordLoginFailure() - (LEAVE)");
  }

  /**
   * Retrieves the number of consecutive failed login attempts for 
   * the given user.
   * 
   * @param userName the user name
   * 
   * @return the number of consecutive failed login attempts
   * 
   */
  public int getLoginFailures(String userName) 
  {
    LOGGER.info("getLoginFailures(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int ret = 0;
    
    try {
      co = getConnection();
      ps = co.prepareStatement("select LOGON_FAILURE from USER_T" + 
                               " where USER_NAME=?");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      if (rs.next()) {
        ret = rs.getInt(1);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getLoginFailures() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Locks-out the user with the provided user name.
   * 
   * @param userName the user name
   * @param lockoutExpiry the lock-out expiry time
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void lockUser(String userName, Date lockoutExpiry) 
  {
    LOGGER.info("lockUser(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement upd = null;
    
    try {
      co = getConnection();
      upd = co.prepareStatement("update USER_T set STATUS='L',LOCKOUT_TO=?," + 
                                "LOCKOUT_REASON='Consecutive login failures'" + 
                                " where STATUS='E' and USER_NAME=?");
      upd.setTimestamp(1, new Timestamp(lockoutExpiry.getTime()));
      upd.setString(2, userName);
      int cnt = upd.executeUpdate();
      LOGGER.debug("Row count: " + cnt);
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(upd);
      closeConnection(co);
    }
    
    LOGGER.info("lockUser() - (LEAVE)");
  }
  
  /**
   * Gets the person id of person for the given user.
   * 
   * @param userName the user name
   * 
   * @return the id of person for the given user
   */
  public int getPersonId(String userName) 
  {
    LOGGER.info("getPersonId(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int ret = -1;
    
    try {
      co = getConnection();
      ps = co.prepareStatement("select PERSON_ID from USER_T" + 
                               " where USER_NAME=?");
      ps.setString(1, userName);
      rs = ps.executeQuery();
      if (rs.next()) {
        ret = rs.getInt(1);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getPersonId() - (LEAVE)");
    return ret;
  }  
  
  /**
   * Create a new person using the passed person attributes and associate with the given user.
   * 
   * @param userMap the person's first name, last name and mail of the given user in a map.
   * @param userName the user name
   * 
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void createPersonForUser(Map<String, Object> userMap, String userName) 
  {
    LOGGER.info("createPersonForUser(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement select = null;
    PreparedStatement insert = null;
    PreparedStatement update = null;
    
    try {

      String[] queryAttributes = LDAP.getQueryAttributes(userMap);	
      String[] values = new String[PERSON_COLUMNS.length];
      
      co = getConnection();
      insert = co.prepareStatement("insert into person_t(first_name, last_name, phone_number, mobile_number, "
        		+ "fax_number, e_mail) values (?, ?, ?, ?, ?, ?)", 
      		  Statement.RETURN_GENERATED_KEYS);
      
      int i;
      for (i = 0; i < PERSON_COLUMNS.length; i++) {
    	if (i < queryAttributes.length) {
    		values[i] = (String) userMap.get(queryAttributes[i]);
    		insert.setString(i+1, values[i]);
    	} else {
    		insert.setString(i+1, null);
    	}
      }
      	
      int cnt = insert.executeUpdate();
      
      LOGGER.debug("Insert Row count: " + cnt);
      
      ResultSet rs = insert.getGeneratedKeys();
      rs.next();
      String rowId = rs.getString(1);
      if (rowId.length() > 10) {
    	  
    	  LOGGER.debug("Created Person rowid: " + rowId);  
          
    	  select = co.prepareStatement("select person_id from person_t where rowid=?");
    	  select.setString(1, rowId);
    	  ResultSet rsPersonId = select.executeQuery();
    	  rsPersonId.next();
          update = co.prepareStatement("update user_t set person_id = ? where user_name = ?");
          update.setInt(1, rsPersonId.getInt(1));
          update.setString(2, userName);
          cnt = update.executeUpdate();
          
          LOGGER.debug("Update Row count: " + cnt);
      } else {
    	  
    	  int personId = Integer.parseInt(rowId);
          
    	  LOGGER.debug("Created Person id: " + personId);  
          
          update = co.prepareStatement("update user_t set person_id = ? where user_name = ?");
          update.setInt(1, personId);
          update.setString(2, userName);
          cnt = update.executeUpdate();
          
          LOGGER.debug("Update Row count: " + cnt);
      }
      
      
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(select);
      closeStatement(insert);
      closeStatement(update);
      closeConnection(co);
    }
    
    LOGGER.info("createPersonForUser() - (LEAVE)");
  }  

  /**
   * Sync the person of the given id with the passed values 
   * 
   * @param map values to be sync
   * @param personId the user name
   * 
   * @return  true if sync took place or false if the values was identical
   */
  public boolean syncPerson(Map<String, Object> map, int personId) 
  {
    LOGGER.info("syncPerson(" + personId + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    PreparedStatement update = null;
    ResultSet rs = null;
    boolean identical = false;
    String[] dbValues = new String[PERSON_COLUMNS.length];    
    
    try {
      co = getConnection();
      ps = co.prepareStatement("select first_name, last_name, phone_number, mobile_number, fax_number, e_mail"
      						 + " from person_t where person_id=?");
      ps.setInt(1, personId);
      rs = ps.executeQuery();
      if (rs.next()) {
    	  dbValues[0] = rs.getString(1);
    	  dbValues[1] = rs.getString(2);
    	  dbValues[2] = rs.getString(3);
    	  dbValues[3] = rs.getString(4);
    	  dbValues[4] = rs.getString(5);
    	  dbValues[5] = rs.getString(6);
        
        if (rs.next()) {
        	LOGGER.warn("Many rows for person id: " + personId); 
        }
        
        String[] queryAttributes = LDAP.getQueryAttributes(map);
        String[] values = new String[PERSON_COLUMNS.length];
        
    	update = co.prepareStatement("update person_t set first_name = ?, last_name = ?, phone_number = ?, "
			+ "mobile_number = ?, fax_number = ?, e_mail = ?"
					 + " where person_id=?");
        
        String ldap = "";
        String db = "";
        int i;
        for (i = 0; i < PERSON_COLUMNS.length; i++) {
        	db = db + dbValues[i];
	      	if (i < queryAttributes.length) {
	      		values[i] = (String) map.get(queryAttributes[i]);
	      		if (queryAttributes[i].equals("")) {
	      			ldap = ldap + dbValues[i];
	      			update.setString(i+1, dbValues[i]);
	      		} else {
	      			ldap = ldap + values[i];
	      			update.setString(i+1, values[i]);
	      		}
	      	} else {
	      		ldap = ldap + dbValues[i];
	      		update.setString(i+1, dbValues[i]);
	      	}
        }
        
        identical = db.equals(ldap);
        if (!identical) {
        	LOGGER.debug("LDAP sync needed");
        	update.setInt(7,  personId);
        	int cnt = update.executeUpdate();
        	LOGGER.debug("LDAP sync rows updated: " + cnt);
        } else {
        	LOGGER.debug("No LDAP sync needed");
        }
        	
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeStatement(update);
      closeConnection(co);
    }
    
    LOGGER.info("syncPerson() - (LEAVE)");
    return identical;
  }    
  
  
}