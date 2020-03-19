package eu.europa.ec.mare.usm.administration.service.application.impl;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.Application;
import eu.europa.ec.mare.usm.administration.domain.FindApplicationQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of Application related
 * information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class ApplicationJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationJdbcDao.class);

    public ApplicationJdbcDao() {
    }

    /**
     * Gets the name of all applications.
     *
     * @return the application names
     */
    public List<String> getApplicationNames() {
        LOGGER.debug("getApplicationNames() - (ENTER)");

        Query query = new Query("select NAME from APPLICATION_T order by NAME");
        List<String> ret = queryForList(query, new StringMapper());

        LOGGER.debug("getApplicationNames() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a list of applications according to the request
     *
     * @param request an ApplicationQuery request.
     * @return PaginationResponse<Application> which contains the possibly-empty list
     * of Applications plus the total number of results
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    @SuppressWarnings("unchecked")
    public PaginationResponse<Application> findApplications(FindApplicationQuery request) {
        LOGGER.debug("findApplications(" + request + ") - (ENTER)");

        Paginator rPaginator = request.getPaginator();

        String paginationQuery = "select * from (select ROW_NUMBER() over(order by NAME "
                + rPaginator.getSortDirection() + ") as rn, du.* from(";

        Query queryForTotalRecords = getFindApplicationsQuery(null, null, request);
        Query queryForFindApplications = getFindApplicationsQuery(paginationQuery,
                rPaginator, request);

        int totalRecords = queryForTotalRecords(queryForTotalRecords,
                new TotalRecordsMapper());
        List<Application> applicationsList = queryForList(queryForFindApplications,
                new ApplicationsMapper());
        PaginationResponse<Application> ret = new PaginationResponse<>();
        ret.setResults(applicationsList);
        ret.setTotal(totalRecords);

        LOGGER.debug("findApplications() - (LEAVE): " + ret);
        return ret;
    }

    private Query getFindApplicationsQuery(String paginationQuery, Paginator rPaginator, FindApplicationQuery request) {
        Query query = new Query();
        String selection;

        if (paginationQuery != null) {
            query.append(paginationQuery);
            selection = "select " +
                    "DISTINCT a.NAME,a.DESCRIPTION,p.NAME AS PARENT ";
        } else {
            selection = "select count(*) from (select DISTINCT a.NAME ";
        }

        String basicQuery = selection +
                "from APPLICATION_T a left outer join APPLICATION_T p on a.PARENT_ID=p.APPLICATION_ID where 1=1 ";

        query.append(basicQuery);

        if (request.getName() != null) {
            query.append("and lower(a.name) like lower(?) ").add("%" + request.getName() + "%");
        }

        if (request.getParentName() != null) {
            query.append("and p.name = ?").add(request.getParentName());
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

    private static class TotalRecordsMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs) throws SQLException {
            return rs.getInt(1);
        }
    }

    private static class ApplicationsMapper implements RowMapper {
        @Override
        public Application mapRow(ResultSet rs)
                throws SQLException {
            Application ret = new Application();
            ret.setName(rs.getString("NAME"));
            ret.setDescription(rs.getString("DESCRIPTION"));
            ret.setParent(rs.getString("PARENT"));
            return ret;
        }
    }

    /**
     * Gets the names of all applications that have children.
     *
     * @return the application names
     */
    public List<String> getParentApplicationNames() {
        LOGGER.debug("getParentApplicationNames() - (ENTER)");

        Query query = new Query("select distinct p.NAME from APPLICATION_T a,APPLICATION_T p " +
                "where a.PARENT_ID=p.APPLICATION_ID");
        List<String> ret = queryForList(query, new StringMapper());

        LOGGER.debug("getParentApplicationNames() - (LEAVE)");
        return ret;
    }
}
