package eu.europa.ec.mare.usm.administration.service.scope.impl;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of Scope related
 * information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class ScopeJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeJdbcDao.class);

    public ScopeJdbcDao() {
    }

    /**
     * Retrieves a list of scopes according to the request
     *
     * @param request a FindScopesQuery request.
     * @return PaginationResponse<Scope> which contains the possibly-empty list of Scope
     * plus the total number of results
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    @SuppressWarnings("unchecked")
    public PaginationResponse<Scope> findScopes(FindScopesQuery request) {
        LOGGER.debug("findScopes(" + request + ") - (ENTER)");

        Paginator rPaginator = request.getPaginator();

        String paginationQuery = "select * from (select ROW_NUMBER() over(order by " +
                appendColumnNames(rPaginator.getSortColumn()) + " " + rPaginator.getSortDirection() +
                ") as rn, du.* from(";

        Query queryForTotalRecords = getFindScopesQuery(null, null, request);
        Query queryForFindScopes = getFindScopesQuery(paginationQuery, rPaginator, request);

        int totalRecords = queryForTotalRecords(queryForTotalRecords, new TotalRecordsMapper());
        List<Scope> scopesList = queryForList(queryForFindScopes, new ScopeMapper());
        PaginationResponse<Scope> ret = new PaginationResponse<>();
        ret.setResults(scopesList);
        ret.setTotal(totalRecords);

        LOGGER.debug("findScopes() - (LEAVE): " + ret);
        return ret;
    }

    public List<DataSet> findDataSets(FindDataSetQuery request) {
        LOGGER.debug("findDataSets(" + request + ") - (ENTER)");

        Query query = new Query("select DISTINCT d.DATASET_ID ,d.NAME,d.DESCRIPTION,d.DISCRIMINATOR,d.CATEGORY, a.NAME as APPLICATION_NAME"
                + " from DATASET_T d "
                + " inner join APPLICATION_T  a on a.APPLICATION_ID=d.APPLICATION_ID "
                + " where 1=1 ");

        if (request.getApplicationName() != null) {
            query.append("and a.name=? ").add(request.getApplicationName());
        }
        if (request.getCategory() != null) {
            query.append("and d.category=? ").add(request.getCategory());
        }

        List<DataSet> ret = queryForList(query, new DataSetMapper());

        LOGGER.debug("findDataSets() - (LEAVE): " + ret.size());
        return ret;
    }

    /**
     * Retrieves a specific scope
     *
     * @param request a GetUserQuery request.
     * @return a specific scope
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    public Scope getScope(GetScopeQuery request) {
        LOGGER.debug("getScope(" + request + ") - (ENTER)");

        Long scopeId = request.getScopeId();

        Query scopeQuery = new Query("select SCOPE_ID,NAME,DESCRIPTION,ACTIVE_FROM,ACTIVE_TO," +
                "STATUS,DATA_FROM,DATA_TO,(select count(DISTINCT USER_NAME) from active_user_scope_v v " +
                "where v.SCOPE_ID=s.SCOPE_ID) as USERS  from SCOPE_T s where s.scope_id=?");

        scopeQuery.add(scopeId);

		Scope ret = (Scope) queryForObject(scopeQuery, new ScopeMapper());

        Query datasetQuery = new Query("select d.DATASET_ID,d.NAME,d.DESCRIPTION,CATEGORY, d.DISCRIMINATOR,a.NAME as APPLICATION_NAME " +
                "from SCOPE_DATASET_T ds left outer join DATASET_T d on d.DATASET_ID=ds.DATASET_ID " +
                "left outer join APPLICATION_T a on a.APPLICATION_ID=d.APPLICATION_ID " +
                "where ds.SCOPE_ID=?");

        datasetQuery.add(scopeId);

        List<DataSet> dataSets = queryForList(datasetQuery, new DataSetMapper());
        if (dataSets.size() > 0) {
            ret.setDataSets(dataSets);
        }

        LOGGER.debug("getScope() - (LEAVE): " + ret);
        return ret;
    }

    private static class ScopeMapper implements RowMapper {
        @Override
        public Scope mapRow(ResultSet rs)
                throws SQLException {
            Scope ret = new Scope();

            ret.setScopeId(rs.getLong("SCOPE_ID"));
            ret.setName(rs.getString("NAME"));
            ret.setDescription(rs.getString("DESCRIPTION"));
            ret.setStatus(rs.getString("STATUS"));
            ret.setActiveFrom(rs.getDate("ACTIVE_FROM"));
            ret.setActiveTo(rs.getDate("ACTIVE_TO"));
            ret.setDataFrom(rs.getDate("DATA_FROM"));
            ret.setDataTo(rs.getDate("DATA_TO"));
            ret.setActiveUsers(rs.getInt("USERS"));
            return ret;
        }
    }

    private static class DataSetMapper implements RowMapper {
        @Override
        public DataSet mapRow(ResultSet rs) throws SQLException {
            DataSet ret = new DataSet();

            ret.setName(rs.getString("NAME"));
            ret.setDescription(rs.getString("DESCRIPTION"));
            ret.setCategory(rs.getString("CATEGORY"));
            ret.setApplication(rs.getString("APPLICATION_NAME"));
            ret.setDatasetId(rs.getLong("DATASET_ID"));
            ret.setDiscriminator(rs.getString("DISCRIMINATOR"));
            return ret;
        }
    }

    /**
     * Checks whether a scope exists with the provided name for the
     * specific application
     *
     * @param scopeName the scope name
     * @return <i>true</i> if the scope exists, <i>false</i> otherwise
     */
    public boolean scopeExists(String scopeName) {
        LOGGER.debug("scopeExists(" + scopeName + ") - (ENTER)");

        Query query = new Query("select 1 from SCOPE_T s" +
                " where  s.NAME=?");
        query.add(scopeName);

        boolean ret = queryForExistence(query);

        LOGGER.debug("scopeExists() - (LEAVE): " + ret);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public List<String> getCategoryNames() {
        LOGGER.debug("getCategoryNames() - (ENTER)");

        Query query = new Query(
                "SELECT DISTINCT category FROM dataset_t WHERE category is not null ORDER BY category ASC");
        List<String> names = queryForList(query, new StringMapper());

        LOGGER.debug("getCategoryNames() - (LEAVE)");
        return names;
    }

    private Query getFindScopesQuery(String pagination, Paginator rPaginator, FindScopesQuery request) {
        Query query = new Query();
        String selection;

        if (pagination != null) {
            query.append(pagination);
            selection = "select " +
                    "DISTINCT s.SCOPE_ID,s.NAME,s.DESCRIPTION,STATUS,ACTIVE_FROM, " +
                    "ACTIVE_TO,DATA_FROM,DATA_TO,(select count(DISTINCT USER_NAME) " +
                    "from active_user_scope_v v where v.SCOPE_ID=s.SCOPE_ID) as USERS ";
        } else {
            selection = "select count(*) from (select DISTINCT s.NAME ";
        }

        String basicQuery = selection +
                "from SCOPE_T s left join SCOPE_DATASET_T  sd on " +
                "s.SCOPE_ID = sd.SCOPE_ID left join DATASET_T d on " +
                "sd.DATASET_ID = d.DATASET_ID left join APPLICATION_T a on " +
                "a.APPLICATION_ID=d.APPLICATION_ID where 1=1 ";

        query.append(basicQuery);

        if (request.getScopeName() != null) {
            query.append("and lower(s.name) like lower(?) ").add("%" + request.getScopeName() + "%");
        }
        if (request.getStatus() != null) {
            query.append("and status=? ").add(request.getStatus());
        }
        if (request.getApplicationName() != null) {
            query.append("and a.name=? ").add(request.getApplicationName());
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

    private String appendColumnNames(String column) {
        switch (column) {
			case "description":
            case "status":
                return column;
            case "activeFrom":
                return "ACTIVE_FROM";
            case "activeTo":
                return "ACTIVE_TO";
            case "dataFrom":
                return "DATA_FROM";
            case "dataTo":
                return "DATA_TO";
            default:
                return "NAME";
        }
    }

    private static class TotalRecordsMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs) throws SQLException {
            return rs.getInt(1);
        }
    }

    /**
     * Retrieves a list of comprehensive scopes
     *
     * @return a list of comprehensive scopes
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    public List<ComprehensiveScope> getScopes() {
        LOGGER.debug("getScopes() - (ENTER)");

        Query query = new Query("select SCOPE_ID,NAME,STATUS from SCOPE_T order by NAME");
        List<ComprehensiveScope> scopeNames = queryForList(query, new ScopesMapper());

        LOGGER.debug("getScopes() - (LEAVE)");
        return scopeNames;
    }

    private static class ScopesMapper implements RowMapper {
        @Override
        public ComprehensiveScope mapRow(ResultSet rs)
                throws SQLException {
            ComprehensiveScope ret = new ComprehensiveScope();

            ret.setScopeId(rs.getLong("SCOPE_ID"));
            ret.setName(rs.getString("NAME"));
            ret.setStatus(rs.getString("STATUS"));
            return ret;
        }
    }

}
