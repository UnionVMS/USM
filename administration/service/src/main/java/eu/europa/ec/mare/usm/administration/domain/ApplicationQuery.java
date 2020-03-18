package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of Application information.
 */
public class ApplicationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    public ApplicationQuery() {
    }

    @Override
    public String toString() {
        return "ApplicationQuery{" + '}';
    }

}
