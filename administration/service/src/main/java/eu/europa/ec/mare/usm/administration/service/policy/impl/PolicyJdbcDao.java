package eu.europa.ec.mare.usm.administration.service.policy.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJdbcDao;

/**
 * JDBC based data access object for the retrieval of user-related information.
 *
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class PolicyJdbcDao extends BaseJdbcDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);
	
    /**
     * Creates a new instance.
     */
    public PolicyJdbcDao() {
    }
    
    /**
     * Gets the list of all subjects.
     *
     * @return the possibly empty list of all user names
     */
    public List<String> getSubjects() 
    {
      LOGGER.info("getSubjects() - (ENTER)");

      Query query = new Query("select DISTINCT SUBJECT from POLICY_T order by 1");

      List<String> ret = queryForList(query, new StringMapper());

      LOGGER.info("getSubjects() - (LEAVE)");
      return ret;
    }    
}
