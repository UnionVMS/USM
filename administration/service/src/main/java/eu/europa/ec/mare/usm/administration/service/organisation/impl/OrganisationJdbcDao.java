package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.common.jdbc.Query;
import eu.europa.ec.mare.usm.administration.common.jdbc.RowMapper;
import eu.europa.ec.mare.usm.administration.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of Organisation related
 * information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
public class OrganisationJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationJdbcDao.class);

    public OrganisationJdbcDao() {
    }

    /**
     * Gets the name of all possible parent organisations for a given
     * organisation.
     *
     * @param childOrgId the id of the candidate child organisation, or null in
     *                   case of the new (transient) organisation
     * @return the possibly empty list of organisation names
     */
    public List<String> getOrganisationParentNames(Long childOrgId) {
        LOGGER.info("getOrganisationParentNames(" + childOrgId + ") - (ENTER)");

        Query query = new Query("select name from organisation_t where 1=1");
        if (childOrgId != null) {
            // Exclude the potential child
            query.append(" and organisation_id<>?").add(childOrgId);
            // and all its children
            query.append(" and organisation_id not in (select organisation_id"
                    + " from organisation_t where parent_id=?)").
                    add(childOrgId);
        }
        query.append(" order by 1");

        List<String> ret = queryForList(query, new StringMapper());

        LOGGER.info("getOrganisationParentNames() - (LEAVE)");
        return ret;
    }

    /**
     * Gets the name of all organisations.
     *
     * @return the possibly empty list of organisation names
     */
    public List<OrganisationNameResponse> getOrganisationNames() {
        LOGGER.info("getOrganistionNames() - (ENTER)");

        Query query = new Query("SELECT COALESCE ((SELECT org2.NAME || ' / ' "
                + "FROM organisation_t org2 "
                + "WHERE org1.parent_id=org2.organisation_id), '') "
                + "|| org1.NAME ELEMENT, org1.ISOA3CODE, org1.STATUS "
                + "FROM organisation_t org1 "
                + "ORDER BY 1");

        List<OrganisationNameResponse> ret = queryForList(query,
                new OrganisationNameMapper());

        LOGGER.info("getOrganistionNames() - (LEAVE)");
        return ret;
    }

    private static class OrganisationNameMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs)
                throws SQLException {
            OrganisationNameResponse ret = new OrganisationNameResponse();

            ret.setParentOrgName(rs.getString("ELEMENT"));
            ret.setNation(rs.getString("ISOA3CODE"));
            ret.setStatus(rs.getString("STATUS"));
            return ret;
        }
    }

    /**
     * Retrieves the (distinct) names of nations for which organisations exist.
     *
     * @return the possibly empty list of nations
     */
    public List<String> getNationNames() {
        LOGGER.info("getNationNames() - (ENTER)");

        Query query = new Query("SELECT DISTINCT(ISOA3CODE) "
                + " FROM ORGANISATION_T ORDER BY 1");

        List<String> ret = queryForList(query, new StringMapper());

        LOGGER.info("getNationNames() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a list of organisations according to the request
     *
     * @param request a FindOrganisationsQuery request.
     * @return the possibly-empty list of organisations plus the total number of
     * results
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    public PaginationResponse<Organisation> findOrganisations(FindOrganisationsQuery request) {
        LOGGER.info("findOrganisations(" + request + ") - (ENTER)");

        Paginator rPaginator = request.getPaginator();

        String paginationQuery = "select * from (select ROW_NUMBER() over(order by "
                + appendColumnNames(rPaginator.getSortColumn()) + " " + rPaginator.getSortDirection()
                + ") as rn, du.* from(";

        Query queryForTotalRecords = getFindOrganisationsQuery(null, null, request);
        Query queryForFindOrganisation = getFindOrganisationsQuery(paginationQuery, rPaginator, request);

        int totalRecords = queryForTotalRecords(queryForTotalRecords, new TotalRecordsMapper());
        LOGGER.info("The query is " + queryForFindOrganisation.getStatement());
        List<Organisation> scopesList = queryForList(queryForFindOrganisation, new OrganisationMapper());
        PaginationResponse<Organisation> ret = new PaginationResponse<>();
        ret.setResults(scopesList);
        ret.setTotal(totalRecords);

        LOGGER.info("findOrganisations() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Checks whether a organisation exists with the provided name for the
     * specific application
     *
     * @param orgName the organisation name
     * @return <i>true</i> if the organisation exists, <i>false</i> otherwise
     */
    public boolean organisationNameExists(String orgName) {
        LOGGER.info("organisationNameExists(" + orgName + ") - (ENTER)");

        Query query = new Query("select 1 from ORGANISATION_T o"
                + " where o.NAME=?");
        query.add(orgName);

        boolean ret = queryForExistence(query);

        LOGGER.info("organisationNameExists() - (LEAVE): " + ret);
        return ret;
    }

    private static class TotalRecordsMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs)
                throws SQLException {
            return rs.getInt(1);
        }
    }

    private static class OrganisationMapper implements RowMapper {

        @Override
        public Organisation mapRow(ResultSet rs)
                throws SQLException {
            Organisation ret = new Organisation();

            ret.setOrganisationId(rs.getLong("ORGANISATION_ID"));
            ret.setName(rs.getString("NAME"));
            ret.setDescription(rs.getString("DESCRIPTION"));
            ret.setStatus(rs.getString("STATUS"));
            ret.setNation(rs.getString("ISOA3CODE"));
            ret.setEmail(rs.getString("E_MAIL"));
            ret.setParent(rs.getString("PARENT_NAME"));
            return ret;
        }
    }

    private Query getFindOrganisationsQuery(String pagination, Paginator rPaginator, FindOrganisationsQuery request) {
        Query query = new Query();
        String selection;

        if (pagination != null) {
            query.append(pagination);
            selection = "select "
                    + "DISTINCT o.ORGANISATION_ID,o.NAME,o.DESCRIPTION,o.STATUS, o.E_MAIL, p.NAME as PARENT_NAME, "
                    + " o.ISOA3CODE ";
        } else {
            selection = "select count(*) from (select DISTINCT o.NAME ";
        }

        String basicQuery = selection
                + "from ORGANISATION_T o left join ORGANISATION_T  p on "
                + "o.PARENT_ID = p.ORGANISATION_ID where 1=1 ";

        query.append(basicQuery);

        if (request.getName() != null) {
            query.append("and lower(o.name) like lower(?) ").add("%" + request.getName() + "%");
        }
        if (request.getNation() != null) {
            query.append("and o.isoa3code=? ").add(request.getNation());
        }
        if (request.getStatus() != null) {
            query.append("and o.status=? ").add(request.getStatus());
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

    public int getAssociatedUsers(long organisationId) {
        LOGGER.info("getAssociatedUsers(" + organisationId + ") - (ENTER)");

        Query query = new Query("select count(*) from USER_T u"
                + " where  u.organisation_id=?");
        query.add(organisationId);

        int ret = queryForCount(query);

        LOGGER.info("getAssociatedUsers() - (LEAVE): " + ret);
        return ret;
    }

}
