package eu.europa.ec.mare.usm.administration.service.policy.impl;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class PolicyJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

    public PolicyJdbcDao() {
    }

    /**
     * Gets the list of all subjects.
     *
     * @return the possibly empty list of all user names
     */
    public List<String> getSubjects() {
        LOGGER.debug("getSubjects() - (ENTER)");

        Query query = new Query("select DISTINCT SUBJECT from POLICY_T order by 1");

        List<String> ret = queryForList(query, new StringMapper());

        LOGGER.debug("getSubjects() - (LEAVE)");
        return ret;
    }
}
