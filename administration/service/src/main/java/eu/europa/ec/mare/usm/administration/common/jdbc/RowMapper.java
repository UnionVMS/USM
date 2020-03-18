package eu.europa.ec.mare.usm.administration.common.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps rows form a ResultSet to Objects
 */
public interface RowMapper {
    /**
     * Maps one row form a ResultSet to an Object
     *
     * @param rs the ResultSet to be mapped
     * @return the mapped object
     * @throws SQLException in case of mapping error
     */
    public Object mapRow(ResultSet rs)
            throws SQLException;
}
