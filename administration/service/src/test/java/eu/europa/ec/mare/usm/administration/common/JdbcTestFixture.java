package eu.europa.ec.mare.usm.administration.common;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JdbcTestFixture extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTestFixture.class);

    /**
     * Retrieves the JDBC data-source from the JNDI context.
     *
     * @throws RuntimeException in case the JNDI lookup fails
     */
    @PostConstruct
    public void postConstruct() throws RuntimeException {
        lookupDatasource();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(String sql) {
        update(sql, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(String sql, Object parameter) {
        Object[] parameters = null;

        if (parameter != null) {
            parameters = new Object[1];
            parameters[0] = parameter;
        }

        update(sql, parameters);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(String sql, Object[] parameters) {
        LOGGER.debug("update(" + sql + ", " + parameters + ") - (ENTER)");

        Connection co = null;
        PreparedStatement upd = null;

        try {
            co = getConnection();
            upd = co.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    bindParameter(upd, i + 1, parameters[i]);
                }
            }

            int cnt = upd.executeUpdate();
            LOGGER.debug("Row count: " + cnt);
        } catch (Exception ex) {
            handleException(ex);
        } finally {
            closeStatement(upd);
            closeConnection(co);
        }

        LOGGER.debug("update() - (LEAVE)");
    }

    protected void handleException(Exception ex)
            throws RuntimeException {
        String msg = "Failed to execute statement: " + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
