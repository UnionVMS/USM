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
package eu.europa.ec.mare.usm.administration.service.userPreference.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.Preference;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;
import eu.europa.ec.mare.usm.information.entity.OptionEntity;
import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class PreferenceJdbcDao extends BaseJdbcDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceJdbcDao.class.getName());

	/**
	 * Creates a new instance.
	 */
	public PreferenceJdbcDao() {
	}

	private Query getContextPreferencesQuery(Long contextId) {
		Query query = new Query();
		String selection;

		selection = "SELECT OPTION_ID, OPTION_VALUE from PREFERENCE_T where 1=1 ";
		query.append(selection);

		if (contextId != null) {
			query.append(" and USER_CONTEXT_ID=" + contextId);
		}
		return query;
	}

	/**
	 * Find context preferences list.
	 * 
	 * @param contextId
	 *            the identifier of the context
	 * @return null if there is no preference associated with this context, or
	 *         the retrieved list otherwise
	 */
	public List<PreferenceEntity> getContextPreferences(Long contextId) {

		LOGGER.info("getContextPreferences(" + contextId + ") - (ENTER)");

		Query queryContextPreferencesQuery = getContextPreferencesQuery(contextId);

		LOGGER.info("query: " + queryContextPreferencesQuery);

		List<PreferenceEntity> prefList = queryForList(
				queryContextPreferencesQuery, new PreferencesMapper());

		LOGGER.info("getContextPreferences() - (LEAVE): " + prefList);
		return prefList;

	}

	private static class PreferencesMapper implements RowMapper {

		public PreferencesMapper() {
		}

		@Override
		public Object mapRow(ResultSet rs) throws SQLException {
			PreferenceEntity ret = new PreferenceEntity();
			ret.setOptionValue(rs.getBytes("OPTION_VALUE"));

			OptionEntity option = new OptionEntity();
			option.setOptionId(rs.getLong("OPTION_ID"));

			ret.setOption(option);
			return ret;
		}
	}
	
	/**
	 * Find user's preferences list.
	 * 
	 * @param userName
	 * 		the identifier of the user
	 * @param groupName
	 * 		the identifier of the group
	 * 
	 * @return null if there is no preference associated with this user, or
	 *         the retrieved list of preferences
	 */
	public UserPreferenceResponse getUserPreferences(String userName, String groupName) {

		LOGGER.info("getUserPreferences(" + userName + ") - (ENTER)");

		Query userPreferencesQuery = getUserPreferencesQuery(userName, groupName);

		LOGGER.info("query: " + userPreferencesQuery);

		List<Preference> prefList = queryForList(userPreferencesQuery, new UserPreferencesMapper());
		UserPreferenceResponse ret = new UserPreferenceResponse();
	    ret.setResults(prefList);
		
		LOGGER.info("getUserPreferences() - (LEAVE): " + prefList);
		return ret;

	}
	
	private Query getUserPreferencesQuery(String userName, String groupName) {
		Query query = new Query();
		String selection;

		selection = "select OPTION_VALUE,NAME,DESCRIPTION,GROUP_NAME " +
				"from USER_T u " +
				"inner join USER_CONTEXT_T c on u.USER_ID = c.USER_ID " +
				"inner join PREFERENCE_T p on p.USER_CONTEXT_ID = c.USER_CONTEXT_ID " +
				"inner join OPTION_T o on o.OPTION_ID = p.OPTION_ID " +
				"where 1=1";

		query.append(selection);

		if (userName != null) {
			query.append(" and USER_NAME=?").add(userName);
		}
		
		if (groupName != null) {
			query.append(" and GROUP_NAME=?").add(groupName);
		}
		
		return query;
	}
	
	private static class UserPreferencesMapper implements RowMapper {

		public UserPreferencesMapper() {
		}

		@Override
		public Object mapRow(ResultSet rs) throws SQLException {
			Preference ret = new Preference();
			ret.setOptionName(rs.getString("NAME"));
			ret.setOptionDescription(rs.getString("DESCRIPTION"));
			ret.setOptionValue(rs.getBytes("OPTION_VALUE"));
			ret.setGroupName(rs.getString("GROUP_NAME"));
			return ret;
		}
	}
}