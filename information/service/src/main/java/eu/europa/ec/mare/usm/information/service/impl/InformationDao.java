package eu.europa.ec.mare.usm.information.service.impl;

import eu.europa.ec.mare.usm.information.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * <p>
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
     * Retrieves the JDBC data-source from the JNDI context using JNDI name
     * 'jdbc/USM2'.
     *
     * @throws RuntimeException in case the JNDI lookup fails
     */
    @PostConstruct
    public void lookupDatasource() throws RuntimeException {
        try {
            javax.naming.Context context = new InitialContext();
            dataSource = (DataSource) context.lookup(DATASOURCE_NAME);
            context.close();
        } catch (NamingException ex) {
            String msg = "Failed to lookup data-source: " + DATASOURCE_NAME + " " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Retrieves contact information about a specific application end-user.
     *
     * @param userName the (unique) user name
     * @return the user contact details if the user exists, <i>null</i> otherwise
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    public ContactDetails getContactDetails(String userName) {
        ContactDetails ret = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("select FIRST_NAME,LAST_NAME,PHONE_NUMBER," +
                    "MOBILE_NUMBER,FAX_NUMBER,E_MAIL," +
                    "u.ORGANISATION_ID" +
                    " from USER_T u, PERSON_T p" +
                    " where u.PERSON_ID=p.PERSON_ID" +
                    " and u.USER_NAME=?");
            stmt.setString(1, userName);

            rs = stmt.executeQuery();
            if (rs.next()) {
                ret = mapContactDetails(rs);
                if (ret != null && rs.getObject("ORGANISATION_ID") != null) {
                    Long parentId = rs.getLong("ORGANISATION_ID");
                    ret.setOrganisationName(getOrganisationName(connection, parentId));
                }
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    /**
     * Retrieves information about an organisation and its associated end-points.
     *
     * @param organisationName the (unique) organisation name
     * @return the organisation details if the organisation exists, <i>null</i>
     * otherwise
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    public Organisation getOrganisation(String organisationName) {
        Organisation ret = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("select ORGANISATION_ID,NAME,ISOA3CODE," +
                    "E_MAIL,STATUS,PARENT_ID,DESCRIPTION" +
                    " from ORGANISATION_T" +
                    " where NAME=?");
            stmt.setString(1, organisationName);

            rs = stmt.executeQuery();
            if (rs.next()) {
                ret = mapOrganisation(connection, rs);
            }
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    /**
     * Retrieves information about all organisations associated with the
     * provided nation.
     *
     * @param nation the nation
     * @return the possibly-empty list of Organisations and their associated
     * EndPoints
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    public List<Organisation> findOrganisations(String nation) {

        List<Organisation> ret = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("select ORGANISATION_ID,NAME,ISOA3CODE," +
                    "E_MAIL,STATUS,PARENT_ID,DESCRIPTION" +
                    " from ORGANISATION_T" +
                    " where ISOA3CODE=?");
            stmt.setString(1, nation);

            rs = stmt.executeQuery();
            ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(mapOrganisation(connection, rs));
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    private Organisation mapOrganisation(Connection co, ResultSet rs) throws SQLException {
        Organisation org = new Organisation();
        org.setName(rs.getString("NAME"));
        org.setNation(rs.getString("ISOA3CODE"));
        org.setEmail(rs.getString("E_MAIL"));
        org.setDescription(rs.getString("DESCRIPTION"));
        org.setEnabled(ENABLED.equals(rs.getString("STATUS")));
        Long organisationId = rs.getLong("ORGANISATION_ID");
        org.setEndPoints(getEndPoints(co, organisationId));
        org.setChildOrganisations(getChildOrganisationNames(co, organisationId));
        Long parentId = rs.getLong("PARENT_ID");
        if (parentId != null) {
            org.setParentOrganisation(getOrganisationName(co, parentId));
        }
        return org;
    }

    public boolean optionExists(String applicationName, String optionName) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean ret = false;
        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("select 1 from OPTION_T o, APPLICATION_T a" +
                    " where o.APPLICATION_ID=a.APPLICATION_ID" +
                    " and a.NAME=? and o.NAME=?");
            stmt.setString(1, applicationName);
            stmt.setString(2, optionName);

            rs = stmt.executeQuery();
            ret = rs.next();
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    public boolean userExists(String userName) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean ret = false;
        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("select * from USER_T where USER_NAME=?");
            stmt.setString(1, userName);

            rs = stmt.executeQuery();
            ret = rs.next();
        } catch (SQLException ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    public boolean userContextExists(String userName, Context ctx) {
        Connection connection = null;
        PreparedStatement stmt = null;
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

            connection = dataSource.getConnection();
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, ctx.getRole().getRoleName());
            if (ctx.getScope() != null) {
                stmt.setString(3, ctx.getScope().getScopeName());
            }

            rs = stmt.executeQuery();
            ret = rs.next();
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    /**
     * Updates the user preferences for the provided user and context.
     *
     * @param userName the name of the user to which the context applies
     * @param ctx      the context holding user preferences to be stored
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    public void updateUserContext(String userName, Context ctx) {
        LOGGER.info("updateUserContext(" + ctx + ") - (ENTER)");

        Connection connection = null;
        PreparedStatement stmt_delete = null;
        PreparedStatement stmt_insert = null;

        try {
            connection = dataSource.getConnection();

            // Delete all/any previously existing preferences
            StringBuilder deleteSB = new StringBuilder();

            deleteSB.append("delete from PREFERENCE_T p")
                    .append(" where p.USER_CONTEXT_ID=(select uc.USER_CONTEXT_ID")
                    .append(" from USER_CONTEXT_T uc")
                    .append(" inner join USER_T u on u.USER_ID=uc.USER_ID")
                    .append(" inner join ROLE_T r on r.ROLE_ID=uc.ROLE_ID");
            if (ctx.getScope() != null) {
                deleteSB.append(" inner join SCOPE_T s on s.SCOPE_ID=uc.SCOPE_ID");
            }
            deleteSB.append(" where u.USER_NAME=?").
                    append(" and r.NAME=?");
            if (ctx.getScope() != null) {
                deleteSB.append(" and s.NAME=?");
            }
            deleteSB.append(")");

            String deleteSQL = deleteSB.toString();

            LOGGER.info("updateUserContext: delSql " + deleteSQL);

            stmt_delete = connection.prepareStatement(deleteSQL);

            stmt_delete.setString(1, userName);
            stmt_delete.setString(2, ctx.getRole().getRoleName());
            if (ctx.getScope() != null) {
                stmt_delete.setString(3, ctx.getScope().getScopeName());
            }

            int cnt = stmt_delete.executeUpdate();
            LOGGER.info("updateUserContext: Deleted " + cnt + " preference records");

            // Create all/any provided preferences
            if (ctx.getPreferences() != null &&
                    ctx.getPreferences().getPreferences() != null &&
                    !ctx.getPreferences().getPreferences().isEmpty()) {

                StringBuilder insertSB = new StringBuilder();

                insertSB.append("insert into PREFERENCE_T ")
                        .append("(USER_CONTEXT_ID, OPTION_ID, OPTION_VALUE)")
                        .append(" select uc.USER_CONTEXT_ID, o.OPTION_ID, ?")
                        .append(" from APPLICATION_T a,OPTION_T o,USER_CONTEXT_T uc,USER_T u,")
                        .append(" ROLE_T r");
                if (ctx.getScope() != null) {
                    insertSB.append(", SCOPE_T s");
                }
                insertSB.append(" where a.NAME=?")
                        .append(" and o.NAME=?")
                        .append(" and u.USER_NAME=?")
                        .append(" and r.NAME=?")
                        .append(" and o.APPLICATION_ID=a.APPLICATION_ID ")
                        .append(" and uc.USER_ID=u.USER_ID")
                        .append(" and uc.ROLE_ID=r.ROLE_ID");
                if (ctx.getScope() != null) {
                    insertSB.append(" and s.NAME=?").
                            append(" and uc.SCOPE_ID=s.SCOPE_ID");
                }
                String insertSQL = insertSB.toString();

                LOGGER.info("updateUserContext: insSql " + insertSQL);

                stmt_insert = connection.prepareStatement(insertSQL);
                int cnt2 = 0;
                for (Preference item : ctx.getPreferences().getPreferences()) {
                    stmt_insert.setBytes(1, item.getOptionValue() != null ? item.getOptionValue().getBytes() : null);
                    stmt_insert.setString(2, item.getApplicationName());
                    stmt_insert.setString(3, item.getOptionName());
                    stmt_insert.setString(4, userName);
                    stmt_insert.setString(5, ctx.getRole().getRoleName());
                    if (ctx.getScope() != null) {
                        stmt_insert.setString(6, ctx.getScope().getScopeName());
                    }
                    cnt2 += stmt_insert.executeUpdate();
                }
                LOGGER.debug("updateUserContext: Created  " + cnt2 + " preference records");
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(stmt_delete);
            close(stmt_insert);
            close(connection);
        }
    }

    private List<EndPoint> getEndPoints(Connection connection, long organisationId) {
        List<EndPoint> ret = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select END_POINT_ID," +
                    "NAME,DESCRIPTION,URI,E_MAIL,STATUS" +
                    " from END_POINT_T " +
                    " where ORGANISATION_ID=?");
            stmt.setLong(1, organisationId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                EndPoint item = new EndPoint();
                item.setName(rs.getString("NAME"));
                item.setDescription(rs.getString("DESCRIPTION"));
                item.setUri(rs.getString("URI"));
                item.setEmail(rs.getString("E_MAIL"));
                item.setEnabled(ENABLED.equals(rs.getString("STATUS")));

                long endPointId = rs.getLong("END_POINT_ID");
                item.setChannels(getChannels(connection, endPointId));
                item.setContactDetails(getContactDetails(connection, endPointId));

                if (ret == null) {
                    ret = new ArrayList<>();
                }
                ret.add(item);
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private List<String> getChildOrganisationNames(Connection connection, Long parentId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> ret = null;

        try {
            stmt = connection.prepareStatement("select NAME from ORGANISATION_T o" +
                    " where o.PARENT_ID=?");
            stmt.setLong(1, parentId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                if (ret == null) {
                    ret = new ArrayList<>();
                }
                ret.add(rs.getString("NAME"));
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }

        return ret;
    }

    private String getOrganisationName(Connection connection, Long organisationId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String ret = null;

        try {
            stmt = connection.prepareStatement("select NAME from ORGANISATION_T o" +
                    " where o.ORGANISATION_ID=?");
            stmt.setLong(1, organisationId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getString("NAME");
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }
        return ret;
    }

    /**
     * Retrieves an end-user profile for a specific application.
     *
     * @param query the (unique) user and application names
     * @return the user profile if the user and application exist, <i>null</i>
     * otherwise
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    public UserContext getUserContext(UserContextQuery query) {
        UserContext ret = null;

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "select distinct ROLE_NAME,ROLE_ID,SCOPE_ID,USER_CONTEXT_ID" +
                " from active_user_role_v ar" +
                " where ar.user_name=?" +
                " and (exists (select 1 from role_t r, permission_t p, feature_t f, application_t a" +
                "              where r.role_id=ar.role_id" +
                "              and p.role_id=r.role_id" +
                "              and f.feature_id=p.feature_id" +
                "              and a.application_id=f.application_id";
        if (query.getApplicationName() != null) {
            sql += "         and a.name=?";
        }
        sql += ") or" +
                "     exists (select 1 from scope_t s,scope_dataset_t sd,dataset_t d,application_t a" +
                "              where s.scope_id=ar.scope_id" +
                "              and sd.scope_id=s.scope_id" +
                "              and d.dataset_id=sd.dataset_id" +
                "              and a.application_id=d.application_id";
        if (query.getApplicationName() != null) {
            sql += "         and a.name=?";
        }
        sql += "))";
        LOGGER.debug("getUserContext() - sql: " + sql);
        try {
            connection = dataSource.getConnection();

            stmt = connection.prepareStatement(sql);
            stmt.setString(1, query.getUserName());
            if (query.getApplicationName() != null) {
                stmt.setString(2, query.getApplicationName());  // Once for roles
                stmt.setString(3, query.getApplicationName());  // Once for scopes
            }

            rs = stmt.executeQuery();
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
                item.getRole().setFeatures(getFeatures(connection, rs.getLong("ROLE_ID")));

                if (rs.getObject("SCOPE_ID") != null) {
                    item.setScope(getScope(connection, rs.getLong("SCOPE_ID")));
                }

                Long userContextId = rs.getLong("USER_CONTEXT_ID");
                preferences = getPreferences(connection, query, userContextId);
                item.setPreferences(preferences);

                ret.getContextSet().getContexts().add(item);
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    public Set<Feature> getUserFeatures(String username) {
        Set<Feature> ret = new HashSet<>();

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "select distinct ROLE_ID" +
                " from active_user_role_v ar" +
                " where ar.user_name=?";
        try {
            connection = dataSource.getConnection();

            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.addAll(getFeatures(connection, rs.getLong("ROLE_ID")));
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return ret;
    }

    private Set<Feature> getFeatures(Connection connection, long roleId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Set<Feature> ret = new HashSet<>();

        try {
            stmt = connection.prepareStatement("select f.NAME as FEATURE_NAME, a.NAME" +
                    " as APPLICATION_NAME, f.FEATURE_ID as FEATURE_ID" +
                    " from permission_t p,feature_t f,application_t a" +
                    " where f.feature_id=p.feature_id" +
                    " and a.application_id=f.application_id" +
                    " and p.role_id=?");
            stmt.setLong(1, roleId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Feature item = new Feature();
                item.setApplicationName(rs.getString("APPLICATION_NAME"));
                item.setFeatureName(rs.getString("FEATURE_NAME"));
                item.setFeatureId(rs.getInt("FEATURE_ID"));

                ret.add(item);
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private Scope getScope(Connection connection, long scopeId) {
        Scope ret = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select s.NAME,s.ACTIVE_FROM,s.ACTIVE_TO,s.DATA_FROM,s.DATA_TO" +
                    " from scope_t s" +
                    " where s.scope_id=?");
            stmt.setLong(1, scopeId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                ret = new Scope();
                ret.setScopeName(rs.getString("NAME"));
                ret.setActiveFrom(rs.getTimestamp("ACTIVE_FROM"));
                ret.setActiveTo(rs.getTimestamp("ACTIVE_TO"));
                ret.setDataFrom(rs.getTimestamp("DATA_FROM"));
                ret.setDataTo(rs.getTimestamp("DATA_TO"));

                ret.setDatasets(getDataSets(connection, scopeId));
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }

        return ret;
    }

    private Set<DataSet> getDataSets(Connection connection, long scopeId) {
        Set<DataSet> ret = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select d.NAME as DATASET_NAME,d.CATEGORY,d.DISCRIMINATOR," +
                    " a.NAME as APPLICATION_NAME" +
                    " from scope_t s,scope_dataset_t sd,dataset_t d,application_t a" +
                    " where d.dataset_id=sd.dataset_id" +
                    " and a.application_id=d.application_id" +
                    " and sd.scope_id=?");
            stmt.setLong(1, scopeId);

            rs = stmt.executeQuery();
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
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private Preferences getPreferences(Connection connection, UserContextQuery query, Long userContextId) {
        Preferences ret = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select OPTION_NAME,OPTION_VALUE,APPLICATION_NAME" +
                    " from USER_PROFILE_V" +
                    " where USER_NAME=?" +
                    " and (IS_DEFAULT='Y' or USER_CONTEXT_ID=?)");
            if (query.getApplicationName() != null) {
                sb.append(" and APPLICATION_NAME=?");
            }
            String sql = sb.toString();
            LOGGER.debug("getPreferences() - sql: " + sql);
            stmt = connection.prepareStatement(sql);

            stmt.setString(1, query.getUserName());
            stmt.setLong(2, userContextId);
            if (query.getApplicationName() != null) {
                stmt.setString(3, query.getApplicationName());
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                if (ret == null) {
                    ret = new Preferences();
                    ret.setPreferences(new HashSet<Preference>());
                }
                Preference item = new Preference();
                item.setApplicationName(rs.getString("APPLICATION_NAME"));
                item.setOptionName(rs.getString("OPTION_NAME"));


                byte[] optionValue = rs.getBytes("OPTION_VALUE");

                item.setOptionValue(optionValue != null ? new String(optionValue) : null);
                ret.getPreferences().add(item);
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private List<ContactDetails> getContactDetails(Connection connection, long endPointId) {
        List<ContactDetails> ret = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select FIRST_NAME,LAST_NAME,PHONE_NUMBER," +
                    "MOBILE_NUMBER,FAX_NUMBER,E_MAIL" +
                    " from END_POINT_CONTACT_T u, PERSON_T p" +
                    " where u.PERSON_ID=p.PERSON_ID" +
                    " and u.END_POINT_ID=?");
            stmt.setLong(1, endPointId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                ContactDetails cd = mapContactDetails(rs);

                ret.add(cd);
            }
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private List<Channel> getChannels(Connection connection, long endPointId) {
        List<Channel> ret = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select DATAFLOW,SERVICE,PRIORITY" +
                    " from CHANNEL_T" +
                    " where END_POINT_ID=?");
            stmt.setLong(1, endPointId);

            rs = stmt.executeQuery();
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
            close(rs);
            close(stmt);
        }
        return ret;
    }

    private ContactDetails mapContactDetails(ResultSet rs) throws SQLException {
        ContactDetails ret = new ContactDetails();
        ret.setFirstName(rs.getString("FIRST_NAME"));
        ret.setLastName(rs.getString("LAST_NAME"));
        ret.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        ret.setMobileNumber(rs.getString("MOBILE_NUMBER"));
        ret.setFaxNumber(rs.getString("FAX_NUMBER"));
        ret.seteMail(rs.getString("E_MAIL"));

        return ret;
    }

    private void handleException(Exception ex) throws RuntimeException {
        String msg = FAILED_TO_EXECUTE_QUERY + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.debug("Error closing connection", ex);
            }
        }
    }

    private void close(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                LOGGER.debug("Error closing statement", ex);
            }
        }
    }

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOGGER.debug("Error closing ResultSet", ex);
            }
        }
    }

}
