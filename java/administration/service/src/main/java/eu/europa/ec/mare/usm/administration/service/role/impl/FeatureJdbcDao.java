package eu.europa.ec.mare.usm.administration.service.role.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;

/**
 * JDBC based data access object for the retrieval of Feature related
 * information.
 * 
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class FeatureJdbcDao extends BaseJdbcDao {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FeatureJdbcDao.class);

	/**
	 * Creates a new instance
	 */
	public FeatureJdbcDao() {
	}

	/**
	 * Gets the name of all roles.
	 * 
	 * @return the possibly-empty list of role names
	 */
	@SuppressWarnings("unchecked")
	public List<String> getGroupNames() {
		LOGGER.info("getGroupNames() - (ENTER)");

		Query query = new Query(
				"SELECT DISTINCT group_name FROM feature_t WHERE group_name is not null ORDER BY group_name ASC");
		List<String> names = queryForList(query, new StringMapper());

		LOGGER.info("getGroupNames() - (LEAVE)");
		return names;
	}


}
