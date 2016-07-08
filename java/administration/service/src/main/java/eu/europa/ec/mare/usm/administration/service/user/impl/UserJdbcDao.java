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
package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import java.util.Date;

/**
 * JDBC based data access object for the retrieval of user-related information.
 *
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class UserJdbcDao extends BaseJdbcDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

  /**
   * Creates a new instance.
   */
  public UserJdbcDao() {
  }

  /**
   * Retrieves a list of users according to the request plus the total records
   *
   * @param request a FindUsersQuery request.
   *
   * @return a PaginationResponse which includes a list of ComprehensiveUser and
   * the total number of results
   *
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public PaginationResponse<UserAccount> findUsers(FindUsersQuery request) 
  {
    LOGGER.info("findUsers(" + request + ") - (ENTER)");

    Paginator rPaginator = request.getPaginator();

    String paginationQuery = "select * from (select ROW_NUMBER() over(order by "
            + appendColumnNames(rPaginator.getSortColumn()) + " " + rPaginator.getSortDirection()
            + ") as rn, du.* from(";

    Query queryForTotalRecords = getFindUsersQuery(null, null, request);
    Query queryForFindUsers = getFindUsersQuery(paginationQuery, rPaginator, request);

    int totalRecords = queryForTotalRecords(queryForTotalRecords, new TotalRecordsMapper());
    List<UserAccount> cuList = queryForList(queryForFindUsers, new ComprehensiveUserMapper());
    PaginationResponse<UserAccount> ret = new PaginationResponse<>();
    ret.setResults(cuList);
    ret.setTotal(totalRecords);

    LOGGER.info("findUsers() - (LEAVE): " + ret);
    return ret;
  }

  /**
   * Retrieves a specific user.
   *
   * @param request a GetUserQuery request.
   *
   * @return a specific user
   *
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public UserAccount getUser(GetUserQuery request) 
  {
    LOGGER.info("getUser(" + request + ") - (ENTER)");

    Query query = new Query("select USER_NAME,ACTIVE_FROM,ACTIVE_TO,"
            + "u.STATUS,LAST_LOGON,LOCKOUT_TO,PASSWORD_EXPIRY,LOCKOUT_REASON,"
            + "LOGON_FAILURE,NOTES,FIRST_NAME,LAST_NAME,PHONE_NUMBER,"
            + "MOBILE_NUMBER,FAX_NUMBER,p.E_MAIL,o.NAME,o.ORGANISATION_ID,o.isoa3code as NATION,p.PERSON_ID, o.PARENT_ID, po.name parent_name "
            + "from USER_T u "
            + "left outer join PERSON_T p on u.PERSON_ID=p.PERSON_ID "
            + "left outer join ORGANISATION_T o on u.ORGANISATION_ID=o.ORGANISATION_ID "
            + "left outer join ORGANISATION_T po on o.parent_id=po.organisation_id "
            + " where u.user_name=?");
    query.add(request.getUserName());

    UserAccount ret = (UserAccount) queryForObject(query, new UserMapper());

    LOGGER.info("getUser() - (LEAVE): " + ret);
    return ret;
  }


  /**
   * Gets the list of all userNames, regardless of the users status
   * (active/inactive, enabled/disabled/locked-out)
   *
   * @return the possibly empty list of all user names
   */
  public List<String> getUsersNames() 
  {
    LOGGER.info("getUsersNames() - (ENTER)");

    Query query = new Query("select USER_NAME from USER_T order by 1");

    List<String> ret = queryForList(query, new StringMapper());

    LOGGER.info("getUsersNames() - (LEAVE)");
    return ret;
  }

  /**
   * Finds the userName of all active (and enabled) users whose 
   * password will expire before (or at) the provided date.
   *
   * @param expiringBefore the password expiry upper limit
   * 
   * @return the possibly empty list of the matching user names
   */
  public List<String> findByPasswordExpiry(Date expiringBefore) 
  {
    LOGGER.info("findByPasswordExpiry() - (ENTER)");

    Query query = new Query("select u.user_name" +
                            " from active_user_v a,user_t u,person_t p" +
                            " where u.user_name=a.user_name" +
                            "   and u.person_id=p.person_id" +
                            "   and p.e_mail is not null" +
                            "   and u.expiry_notification is null" +
                            "   and u.password_expiry <=?");
    query.add(expiringBefore);
    
    List<String> ret = queryForList(query, new StringMapper());

    LOGGER.info("findByPasswordExpiry() - (LEAVE)");
    return ret;
  }

  private String appendColumnNames(String column) {
    switch (column) {
      case "userName":
        return "USER_NAME";
      case "firstName":
        return "FIRST_NAME";
      case "lastName":
        return "LAST_NAME";
      case "status":
        return column;
      case "organisation":
        return "NAME";
      case "nation":
        return "ISOA3CODE";
      case "parent":
        return column;
      case "activeFrom":
        return "ACTIVE_FROM";
      case "activeTo":
        return "ACTIVE_TO";
      default:
        return "USER_NAME";
    }

  }

  private Query getFindUsersQuery(String pagination, Paginator rPaginator, FindUsersQuery request) {
    Query query = new Query();
    String selection;

    if (pagination != null) {
      query.append(pagination);
      selection = "select "
              + "USER_NAME,ACTIVE_FROM,ACTIVE_TO,u.STATUS,FIRST_NAME,LAST_NAME,o.NAME,"
              + "o.ISOA3CODE,po.NAME as PARENT,p.e_mail as EMAIL, p.phone_number, p.mobile_number, p.fax_number ";
    } else {
      selection = "select count(*) from (select USER_NAME ";
    }

    String basicQuery = selection
            + "from USER_T u "
            + "left outer join PERSON_T p on p.PERSON_ID=u.PERSON_ID "
            + "left outer join ORGANISATION_T o on u.organisation_id=o.organisation_id "
            + "left outer join ORGANISATION_T po on o.parent_id=po.organisation_id "
            + "where 1=1 ";

    query.append(basicQuery);

    if (request.getNation() != null) {
      query.append("and o.isoa3code=? ").
              add(request.getNation());
    }
    if (request.getOrganisation() != null) {
      query.append("and o.name=? ").
              add(request.getOrganisation());
    }
    if (request.getActiveFrom() != null) {
      query.append("and (u.active_from is null or u.active_from>=?) ").
              add(request.getActiveFrom());
    }
    if (request.getActiveTo() != null) {
      query.append("and (u.active_to is null or u.active_to<=?) ").
              add(request.getActiveTo());
    }
    if (request.getStatus() != null) {
      query.append("and u.status=? ").add(request.getStatus());
    }
    if (request.getName() != null) {
      query.append("and (lower(u.user_name) like lower (?) or lower(p.first_name) like (?) "
              + "or lower(p.last_name) like (?))")
              .add("%" + request.getName() + "%")
              .add("%" + request.getName() + "%")
              .add("%" + request.getName() + "%");
    }

    if (rPaginator != null) {
      query.append(") du) us ");
      int limit = rPaginator.getLimit();
      if (limit != -1) {
        query.append("where rn between ? and ? order by rn");
        query.add(rPaginator.getOffset() + 1);
        query.add(rPaginator.getOffset() + limit);
      }
    } else {
      query.append(") du");
    }

    return query;
  }

  private static class ComprehensiveUserMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs)
            throws SQLException {
      UserAccount ret = new UserAccount();

      ret.setUserName(rs.getString("USER_NAME"));
      ret.setActiveFrom(rs.getTimestamp("ACTIVE_FROM"));
      ret.setActiveTo(rs.getTimestamp("ACTIVE_TO"));
      ret.setStatus(rs.getString("STATUS"));
      Person person = new Person();
      ret.setPerson(person);
      person.setFirstName(rs.getString("FIRST_NAME"));
      person.setLastName(rs.getString("LAST_NAME"));
      person.setEmail(rs.getString("EMAIL"));
      person.setPhoneNumber(rs.getString("PHONE_NUMBER"));
      person.setMobileNumber(rs.getString("MOBILE_NUMBER"));
      person.setFaxNumber(rs.getString("FAX_NUMBER"));
      
      Organisation org = new Organisation();
      org.setName(rs.getString("NAME"));
      org.setNation(rs.getString("ISOA3CODE"));
      org.setParent(rs.getString("PARENT"));
      ret.setOrganisation(org);

      if (org.getParent() != null) {
        ret.setOrganisation_parent(org.getParent() + " / " + org.getName());
      } else {
        ret.setOrganisation_parent(org.getName());
      }

      return ret;
    }
  }

  private static class UserMapper implements RowMapper {

    public UserMapper() {
    }

    @Override
    public Object mapRow(ResultSet rs)
            throws SQLException {
      UserAccount ret = new UserAccount();

      ret.setUserName(rs.getString("USER_NAME"));
      ret.setActiveFrom(rs.getTimestamp("ACTIVE_FROM"));
      ret.setActiveTo(rs.getTimestamp("ACTIVE_TO"));
      ret.setLastLogon(rs.getTimestamp("LAST_LOGON"));
      ret.setLockoutTo(rs.getTimestamp("LOCKOUT_TO"));
      ret.setPasswordExpiry(rs.getTimestamp("PASSWORD_EXPIRY"));
      ret.setStatus(rs.getString("STATUS"));
      ret.setLockoutReason(rs.getString("LOCKOUT_REASON"));
      ret.setLogonFailure(rs.getInt("LOGON_FAILURE"));
      ret.setNotes(rs.getString("NOTES"));

      Person details = new Person();
      details.setEmail(rs.getString("E_MAIL"));
      details.setFaxNumber(rs.getString("FAX_NUMBER"));
      details.setFirstName(rs.getString("FIRST_NAME"));
      details.setLastName(rs.getString("LAST_NAME"));
      details.setMobileNumber(rs.getString("MOBILE_NUMBER"));
      details.setPhoneNumber(rs.getString("PHONE_NUMBER"));
      details.setPersonId(rs.getLong("PERSON_ID"));
      ret.setPerson(details);

      Organisation org = new Organisation();
      ret.setOrganisation(org);
      org.setNation(rs.getString("NATION"));
      org.setName(rs.getString("NAME"));

      if (rs.getString("PARENT_NAME") != null) {
        org.setParent(rs.getString("PARENT_NAME"));
        ret.setOrganisation_parent(org.getParent() + " / " + org.getName());
      } else {
        ret.setOrganisation_parent(org.getName());
      }
      return ret;
    }
  }

  private static class TotalRecordsMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs) throws SQLException {
      return rs.getInt(1);
    }
  }


}