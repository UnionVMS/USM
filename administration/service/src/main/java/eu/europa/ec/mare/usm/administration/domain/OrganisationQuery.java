package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of Organisation information.
 */
public class OrganisationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    public OrganisationQuery() {
    }

    @Override
    public String toString() {
        return "OrganisationQuery{" + '}';
    }

}
