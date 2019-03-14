package eu.europa.ec.mare.usm.information.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.domain.Channel;
import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.ContextSet;
import eu.europa.ec.mare.usm.information.domain.DataSet;
import eu.europa.ec.mare.usm.information.domain.EndPoint;
import eu.europa.ec.mare.usm.information.domain.Feature;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.Preference;
import eu.europa.ec.mare.usm.information.domain.Preferences;
import eu.europa.ec.mare.usm.information.domain.Role;
import eu.europa.ec.mare.usm.information.domain.Scope;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI 
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class InformationDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationDao.class.getName());
  private static final String FAILED_TO_EXECUTE_QUERY = "Failed to execute query: ";
  private static final String DATASOURCE_NAME = "jdbc/USM2";
  private static final String ENABLED = "E";
  
  private DataSource dataSource;

  /**
   * Creates a new instance
   */
  public InformationDao() 
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
  throws RuntimeException
  {
    LOGGER.info("lookupDatasource() - (ENTER)");
    
    try {
      javax.naming.Context context = new InitialContext();    

      dataSource = (DataSource) context.lookup(DATASOURCE_NAME);
      
      context.close();
    } catch (NamingException ex) {
      String msg = "Failed to lookup data-source: " + DATASOURCE_NAME + 
                   " " + ex.getMessage();
      LOGGER.error(msg, ex);
      throw new RuntimeException(msg, ex);
    }
    
    LOGGER.info("lookupDatasource() - (LEAVE)");
  }

  /**
   * Retrieves contact information about a specific application end-user.
   * 
   * @param userName the (unique) user name
   * 
   * @return the user contact details if the user exists, <i>null</i> otherwise
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public ContactDetails getContactDetails(String userName) 
  {
    LOGGER.info("getContactDetails(" + userName + ") - (ENTER)");
    
    ContactDetails ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement("select FIRST_NAME,LAST_NAME,PHONE_NUMBER," +
                                "MOBILE_NUMBER,FAX_NUMBER,E_MAIL," +
                                "u.ORGANISATION_ID" +
                                " from USER_T u, PERSON_T p" +
                                " where u.PERSON_ID=p.PERSON_ID" +
                                " and u.USER_NAME=?");
      ps.setString(1, userName);

      rs = ps.executeQuery();
      if (rs.next()) {
        ret = mapContactDetails(rs);

        if (ret != null && rs.getObject("ORGANISATION_ID") != null) {
          Long parentId = rs.getLong("ORGANISATION_ID");
          ret.setOrganisationName(getOrganisationName(co, parentId));
        }

      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getContactDetails() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves information about an organisation and its associated end-points.
   * 
   * @param organisationName the (unique) organisation name
   * 
   * @return the organisation details if the organisation exists, <i>null</i> 
   * otherwise
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public Organisation getOrganisation(String organisationName) 
  {
    LOGGER.info("getOrganisation(" + organisationName + ") - (ENTER)");
    
    Organisation ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement("select ORGANISATION_ID,NAME,ISOA3CODE,"+
                                "E_MAIL,STATUS,PARENT_ID,DESCRIPTION" +
                                " from ORGANISATION_T" +
                                " where NAME=?");
      ps.setString(1, organisationName);

      rs = ps.executeQuery();
      if (rs.next()) {
        ret = mapOrganisation(co, rs);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getOrganisation() - (LEAVE)");
    return ret;
  }
  
  /**
   * Retrieves information about all organisations associated with the
   * provided nation.
   * 
   * @param nation the nation
   * 
   * @return the possibly-empty list of Organisations and their associated 
   * EndPoints
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public List<Organisation> findOrganisations(String nation) 
  {
    LOGGER.info("findOrganisations(" + nation + ") - (ENTER)");
    
    List<Organisation> ret = null;
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement("select ORGANISATION_ID,NAME,ISOA3CODE,"+
                                "E_MAIL,STATUS,PARENT_ID,DESCRIPTION" +
                                " from ORGANISATION_T" +
                                " where ISOA3CODE=?");
      ps.setString(1, nation);

      rs = ps.executeQuery();
      ret = new ArrayList<>();
      while (rs.next()) {
        ret.add(mapOrganisation(co, rs));
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("findOrganisations() - (LEAVE)");
    return ret;
  }

  private Organisation mapOrganisation(Connection co, ResultSet rs) 
  throws SQLException 
  {
    Organisation org = new Organisation();
    org.setName(rs.getString("NAME"));
    org.setNation(rs.getString("ISOA3CODE"));
    org.setEmail(rs.getString("E_MAIL"));
    org.setDescription(rs.getString("DESCRIPTION"));
    org.setEnabled(ENABLED.equals(rs.getString("STATUS")));
    Long organisationId = rs.getLong("ORGANISATION_ID");
    org.setEndPoints(getEndPoints(co, organisationId));
    org.setChildOrganisations(getChildOrganisationNames(co, organisationId));
    if (rs.getObject("PARENT_ID") != null) {
      Long parentId = rs.getLong("PARENT_ID");
      org.setParentOrganisation(getOrganisationName(co, parentId));
    }
    return org;
  }
  
  public boolean optionExists(String applicationName, String optionName) 
  {
    LOGGER.info("optionExists("+ applicationName + ", " + optionName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    boolean ret = false;
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement("select 1 from OPTION_T o, APPLICATION_T a" +
                               " where o.APPLICATION_ID=a.APPLICATION_ID" +
                               " and a.NAME=? and o.NAME=?");
      ps.setString(1, applicationName);
      ps.setString(2, optionName);
      
      rs = ps.executeQuery();
      ret = rs.next();
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("optionExists() - (LEAVE): " + ret);
    return ret;
  }

  public boolean userExists(String userName) 
  {
    LOGGER.info("userExists(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    boolean ret = false;
    try {
      co = dataSource.getConnection();
      ps = co.prepareStatement("select * from USER_T where USER_NAME=?");
      ps.setString(1, userName);
      
      rs = ps.executeQuery();
      ret = rs.next();
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("userExists() - (LEAVE): " + ret);
    return ret;
  }

  public boolean userContextExists(String userName, Context ctx) 
  {
    LOGGER.info("userContextExists(" + userName + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    boolean ret = false;
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("select 1 from user_context_t uc, user_t u, role_t r");
      if (ctx.getScope() != null) {
        sb.append(", scope_t s");
      }
      sb.append(" where uc.USER_ID=u.USER_ID")
        .append(" and uc.ROLE_ID=r.ROLE_ID")
        .append(" and u.USER_NAME=?")
        .append(" and r.NAME=?");
      if (ctx.getScope() != null) {
        sb.append(" and uc.SCOPE_ID=s.SCOPE_ID")
          .append(" and s.NAME=?");
      }
              
      String sql = sb.toString();
      LOGGER.info("userContextExists:sql: " + sql);
      
      co = dataSource.getConnection();
      ps = co.prepareStatement(sql);
      ps.setString(1, userName);
      ps.setString(2, ctx.getRole().getRoleName());
      if (ctx.getScope() != null) {
        ps.setString(3, ctx.getScope().getScopeName());
      }
      
      rs = ps.executeQuery();
      ret = rs.next();
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("userContextExists() - (LEAVE): " + ret);
    return ret;
  }

  
  /**
   * Updates the user preferences for the provided user and context.
   * 
   * @param userName the name of the user to which the context applies
   * @param ctx the context holding user preferences to be stored
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public void updateUserContext(String userName, Context ctx) 
  {
    LOGGER.info("updateUserContext(" + ctx + ") - (ENTER)");
    
    Connection co = null;
    PreparedStatement del = null;
    PreparedStatement ins = null;

    try {
      co = dataSource.getConnection();
      
      // Delete all/any previously existing preferences
      StringBuilder delSb = new StringBuilder();
      
      delSb.append("delete from PREFERENCE_T p") 
          .append(" where p.USER_CONTEXT_ID=(select uc.USER_CONTEXT_ID") 
          .append(" from USER_CONTEXT_T uc") 
          .append(" inner join USER_T u on u.USER_ID=uc.USER_ID")
          .append(" inner join ROLE_T r on r.ROLE_ID=uc.ROLE_ID");
      if (ctx.getScope() != null) {
        delSb.append(" inner join SCOPE_T s on s.SCOPE_ID=uc.SCOPE_ID");
      }
      delSb.append(" where u.USER_NAME=?").
            append(" and r.NAME=?");
      if (ctx.getScope() != null) {
        delSb.append(" and s.NAME=?");
      }
      delSb.append(")");
      
      String delSql = delSb.toString();
      
      LOGGER.info("updateUserContext: delSql " + delSql);
              
      del = co.prepareStatement(delSql);

      del.setString(1, userName);
      del.setString(2, ctx.getRole().getRoleName());
      if (ctx.getScope() != null) {
        del.setString(3, ctx.getScope().getScopeName());
      }
      
      int cnt = del.executeUpdate();
      LOGGER.info("updateUserContext: Deleted " + cnt + " preference records");
      
      // Create all/any provided preferences
      if (ctx.getPreferences() != null && 
          ctx.getPreferences().getPreferences() != null && 
          !ctx.getPreferences().getPreferences().isEmpty()) {
        
        StringBuilder insSb = new StringBuilder();

        insSb.append("insert into PREFERENCE_T ") 
            .append("(USER_CONTEXT_ID, OPTION_ID, OPTION_VALUE)") 
            .append(" select uc.USER_CONTEXT_ID, o.OPTION_ID, ?")
            .append(" from APPLICATION_T a,OPTION_T o,USER_CONTEXT_T uc,USER_T u,")
            .append(" ROLE_T r");
        if (ctx.getScope() != null) {
          insSb.append(", SCOPE_T s");
        }
        insSb.append(" where a.NAME=?")
            .append(" and o.NAME=?")
            .append(" and u.USER_NAME=?")
            .append(" and r.NAME=?")
            .append(" and o.APPLICATION_ID=a.APPLICATION_ID ")
            .append(" and uc.USER_ID=u.USER_ID")
            .append(" and uc.ROLE_ID=r.ROLE_ID");
        if (ctx.getScope() != null) {
          insSb.append(" and s.NAME=?").
                append(" and uc.SCOPE_ID=s.SCOPE_ID");
        }
        String insSql = insSb.toString();
      
      LOGGER.info("updateUserContext: insSql " + insSql);
              
        ins = co.prepareStatement(insSql);
        int cnt2 = 0;
        for (Preference item : ctx.getPreferences().getPreferences()) {
          
          ins.setBytes(1,item.getOptionValue()!=null?item.getOptionValue().getBytes():null); 
          ins.setString(2, item.getApplicationName());
          ins.setString(3, item.getOptionName());
          ins.setString(4, userName);
          ins.setString(5, ctx.getRole().getRoleName());
          if (ctx.getScope() != null) {
            ins.setString(6, ctx.getScope().getScopeName());
          }
          
          cnt2 += ins.executeUpdate();
        }
        LOGGER.debug("updateUserContext: Created  " + cnt2 + " preference records");
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeStatement(del);
      closeStatement(ins);
      closeConnection(co);
    }
    
    LOGGER.info("updateUserContext() - (LEAVE)");
  }


  
  private List<EndPoint> getEndPoints(Connection co, long organisationId) 
  {
    LOGGER.debug("getEndPoints(" + organisationId + ") - (ENTER)");
    
    List<EndPoint> ret = null;
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = co.prepareStatement("select END_POINT_ID," + 
                               "NAME,DESCRIPTION,URI,E_MAIL,STATUS" +
                               " from END_POINT_T " +
                               " where ORGANISATION_ID=?");
      ps.setLong(1, organisationId);

      rs = ps.executeQuery();
      while (rs.next()) {
        EndPoint item = new EndPoint();
        item.setName(rs.getString("NAME"));
        item.setDescription(rs.getString("DESCRIPTION"));
        item.setUri(rs.getString("URI"));
        item.setEmail(rs.getString("E_MAIL"));
        item.setEnabled(ENABLED.equals(rs.getString("STATUS")));

        long endPointId = rs.getLong("END_POINT_ID");
        item.setChannels(getChannels(co, endPointId));
        item.setContactDetails(getContactDetails(co, endPointId));

        if (ret == null) {
          ret = new ArrayList<>();
        }
        ret.add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.debug("getEndPoints() - (LEAVE)");
    return ret;
  }

  private List<String> getChildOrganisationNames(Connection co, Long parentId) 
  {
    LOGGER.debug("getChildOrganisationNames(" + parentId + ") - (ENTER)");
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<String> ret = null;

    try {
      ps = co.prepareStatement("select NAME from ORGANISATION_T o" +
                               " where o.PARENT_ID=?");
      ps.setLong(1, parentId);

      rs = ps.executeQuery();
      while (rs.next()) {
        if (ret == null) {
          ret = new ArrayList<>();
        }
        ret.add(rs.getString("NAME"));
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.debug("getChildOrganisationNames() - (LEAVE)");
    return ret;
  }

  private String getOrganisationName(Connection co, Long organisationId) 
  {
    LOGGER.debug("getOrganisationName(" + organisationId + ") - (ENTER)");
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    String ret = null;

    try {
      ps = co.prepareStatement("select NAME from ORGANISATION_T o" +
                               " where o.ORGANISATION_ID=?");
      ps.setLong(1, organisationId);

      rs = ps.executeQuery();
      if (rs.next()) {
        ret = rs.getString("NAME");
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.debug("getOrganisationName() - (LEAVE)");
    return ret;
  }

  /**
   * Retrieves an end-user profile for a specific application. 
   * 
   * @param query the (unique) user and application names
   * 
   * @return the user profile if the user and application exist, <i>null</i> 
   * otherwise
   * 
   * @throws RuntimeException in case an internal error prevented fulfilling 
   * the request
   */
  public UserContext getUserContext(UserContextQuery query) 
  {
    LOGGER.info("getUserContext(" + query + ") - (ENTER)");
    
    UserContext ret = null;
    
    Connection co = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select distinct ROLE_NAME,ROLE_ID,SCOPE_ID,USER_CONTEXT_ID" +
        " from active_user_role_v ar" +
        " where ar.user_name=?" +
        " and (exists (select 1 from role_t r, permission_t p, feature_t f, application_t a" +
        "              where r.role_id=ar.role_id" +
        "              and p.role_id=r.role_id" +
        "              and f.feature_id=p.feature_id" +
        "              and a.application_id=f.application_id";
    if(query.getApplicationName()!=null){
      sql += "         and a.name=?";
    }
    sql += ") or" +
        "     exists (select 1 from scope_t s,scope_dataset_t sd,dataset_t d,application_t a" +
        "              where s.scope_id=ar.scope_id" +
        "              and sd.scope_id=s.scope_id" +
        "              and d.dataset_id=sd.dataset_id" +
        "              and a.application_id=d.application_id";
    if(query.getApplicationName()!=null){
      sql += "         and a.name=?";
    }
    sql += "))";
    LOGGER.debug("getUserContext() - sql: "+sql);
    try {
      co = dataSource.getConnection();
      
      ps = co.prepareStatement(sql);
      ps.setString(1, query.getUserName());
      if(query.getApplicationName()!=null){
        ps.setString(2, query.getApplicationName());  // Once for roles
        ps.setString(3, query.getApplicationName());  // Once for scopes
      }

      rs = ps.executeQuery();
      Preferences preferences;
        
      while (rs.next()) {
        if (ret == null) {
          ret = new UserContext();
          ret.setUserName(query.getUserName());
          ret.setApplicationName(query.getApplicationName());
          ret.setContextSet(new ContextSet());
          ret.getContextSet().setContexts(new HashSet<Context>());
        }
        
        Context item = new Context();
        item.setRole(new Role());
        item.getRole().setRoleName(rs.getString("ROLE_NAME"));
        item.getRole().setFeatures(getFeatures(co, rs.getLong("ROLE_ID")));
        
        if (rs.getObject("SCOPE_ID") != null) {
          item.setScope(getScope(co, rs.getLong("SCOPE_ID")));
        }  
        
        Long userContextId = rs.getLong("USER_CONTEXT_ID");
        preferences = getPreferences(co, query, userContextId);
        item.setPreferences(preferences);
        
        ret.getContextSet().getContexts().add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(co);
    }
    
    LOGGER.info("getUserContext() - (LEAVE)");
    return ret;
  }

  private Set<Feature> getFeatures(Connection co, long roleId) 
  {
    LOGGER.debug("getFeatures(" + roleId + ") - (ENTER)");
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    Set<Feature> ret = new HashSet<>();

    try {
      ps = co.prepareStatement("select f.NAME as FEATURE_NAME, a.NAME as APPLICATION_NAME" +
                               " from permission_t p,feature_t f,application_t a" +
                               " where f.feature_id=p.feature_id" +
                               " and a.application_id=f.application_id" +
                               " and p.role_id=?");
      ps.setLong(1, roleId);

      rs = ps.executeQuery();
      while (rs.next()) {
        Feature item = new Feature();
        item.setApplicationName(rs.getString("APPLICATION_NAME"));
        item.setFeatureName(rs.getString("FEATURE_NAME"));

        ret.add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.debug("getFeatures() - (LEAVE)");
    return ret;
  }

  private Scope getScope(Connection co, long scopeId) 
  {
    LOGGER.info("getScope(" + scopeId + ") - (ENTER)");
    
    Scope ret = null;
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = co.prepareStatement("select s.NAME,s.ACTIVE_FROM,s.ACTIVE_TO,s.DATA_FROM,s.DATA_TO" +
                                " from scope_t s" +
                                " where s.scope_id=?");
      ps.setLong(1, scopeId);

      rs = ps.executeQuery();
      if (rs.next()) {
        ret = new Scope();
        ret.setScopeName(rs.getString("NAME"));
        ret.setActiveFrom(rs.getTimestamp("ACTIVE_FROM"));
        ret.setActiveTo(rs.getTimestamp("ACTIVE_TO"));
        ret.setDataFrom(rs.getTimestamp("DATA_FROM"));
        ret.setDataTo(rs.getTimestamp("DATA_TO"));

        ret.setDatasets(getDataSets(co, scopeId));
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.info("getScope() - (LEAVE)");
    return ret;
  }

  private Set<DataSet> getDataSets(Connection co, long scopeId) 
  {
    LOGGER.info("getDataSets(" + scopeId + ") - (ENTER)");
    
    Set<DataSet> ret = null;
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = co.prepareStatement("select d.NAME as DATASET_NAME,d.CATEGORY,d.DISCRIMINATOR," +
                               " a.NAME as APPLICATION_NAME" +
                               " from scope_t s,scope_dataset_t sd,dataset_t d,application_t a" +
                               " where d.dataset_id=sd.dataset_id" +
                               " and a.application_id=d.application_id" +
                               " and sd.scope_id=?");
      ps.setLong(1, scopeId);

      rs = ps.executeQuery();
      while (rs.next()) {
        if (ret == null) {
          ret = new HashSet<>();
        }
        DataSet item = new DataSet();
        item.setApplicationName(rs.getString("APPLICATION_NAME"));
        item.setCategory(rs.getString("CATEGORY"));
        item.setDiscriminator(rs.getString("DISCRIMINATOR"));
        item.setName(rs.getString("DATASET_NAME"));
        
        ret.add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.info("getDataSets() - (LEAVE)");
    return ret;
  }

  private Preferences getPreferences(Connection co, UserContextQuery query,
                                       Long userContextId) 
  {
    LOGGER.info("getPreferences(" + query + ", " + userContextId + ") - (ENTER)");
    
    Preferences ret = null;
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      StringBuilder sb = new StringBuilder();
      sb.append("select OPTION_NAME,OPTION_VALUE,APPLICATION_NAME"+
                                " from USER_PROFILE_V" +
                                " where USER_NAME=?" +
                                " and (IS_DEFAULT='Y' or USER_CONTEXT_ID=?)");
      if (query.getApplicationName() != null) {
        sb.append(" and APPLICATION_NAME=?");
      }
      String sql = sb.toString();
      LOGGER.debug("getPreferences() - sql: "+sql);
      ps = co.prepareStatement(sql);
      
      ps.setString(1, query.getUserName());
      ps.setLong(2, userContextId);
      if (query.getApplicationName() != null) {
        ps.setString(3, query.getApplicationName());
      }
     
      rs = ps.executeQuery();
      while (rs.next()) {
        if (ret == null) {
          ret = new Preferences();
          ret.setPreferences(new HashSet<Preference>());
        }
        Preference item = new Preference();
        item.setApplicationName(rs.getString("APPLICATION_NAME"));
        item.setOptionName(rs.getString("OPTION_NAME"));
       
      
        byte[] optionValue=rs.getBytes("OPTION_VALUE");
        
        item.setOptionValue(optionValue!=null?new String(optionValue):null);
        ret.getPreferences().add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.info("getPreferences() - (LEAVE)");
    return ret;
  }

  private List<ContactDetails> getContactDetails(Connection co, long endPointId) 
  {
    LOGGER.info("getContactDetails(" + endPointId + ") - (ENTER)");
    
    List<ContactDetails> ret = new ArrayList<>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = co.prepareStatement("select FIRST_NAME,LAST_NAME,PHONE_NUMBER," +
                                "MOBILE_NUMBER,FAX_NUMBER,E_MAIL" +
                                " from END_POINT_CONTACT_T u, PERSON_T p" +
                                " where u.PERSON_ID=p.PERSON_ID" +
                                " and u.END_POINT_ID=?");
      ps.setLong(1, endPointId);

      rs = ps.executeQuery();
      while (rs.next()) {
        ContactDetails cd = mapContactDetails(rs);

        ret.add(cd);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.info("getContactDetails() - (LEAVE)");
    return ret;
  }

  private List<Channel> getChannels(Connection co, long endPointId) 
  {
    LOGGER.info("getChannels(" + endPointId + ") - (ENTER)");
    
    List<Channel> ret = new ArrayList<>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ps = co.prepareStatement("select DATAFLOW,SERVICE,PRIORITY" +
                                " from CHANNEL_T" +
                                " where END_POINT_ID=?");
      ps.setLong(1, endPointId);

      rs = ps.executeQuery();
      while (rs.next()) {
        Channel item = new Channel();
        
        item.setDataFlow(rs.getString("DATAFLOW"));
        item.setService(rs.getString("SERVICE"));
        
        item.setPriority(rs.getInt("PRIORITY"));

        ret.add(item);
      }
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
    }
    
    LOGGER.info("getChannels() - (LEAVE)");
    return ret;
  }

  private ContactDetails mapContactDetails(ResultSet rs) 
  throws SQLException 
  {
    ContactDetails ret = new ContactDetails();
    
    ret.setFirstName(rs.getString("FIRST_NAME"));
    ret.setLastName(rs.getString("LAST_NAME"));
    ret.setPhoneNumber(rs.getString("PHONE_NUMBER"));
    ret.setMobileNumber(rs.getString("MOBILE_NUMBER"));
    ret.setFaxNumber(rs.getString("FAX_NUMBER"));
    ret.seteMail(rs.getString("E_MAIL"));
    
    return ret;
  }

  private void handleException(Exception ex) 
  throws RuntimeException 
  {
    String msg = FAILED_TO_EXECUTE_QUERY + ex.getMessage();
    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

  private void closeConnection(Connection co) 
  {
    if (co != null) {
      try {
        co.close();
      } catch (Exception ex) {
        LOGGER.debug("Error closing connection", ex);
      }
    }
  }

  private void closeStatement(PreparedStatement ps) 
  {
    if (ps != null) {
      try {
        ps.close();
      } catch (Exception ex) {
        LOGGER.debug("Error closing statement", ex);
      }
    }
  }

  private void closeResultSet(ResultSet rs) 
  {
    if (rs != null) {
      try {
        rs.close();
      } catch (Exception ex) {
        LOGGER.debug("Error closing ResultSet", ex);
      }
    }
  }
 
  
}
