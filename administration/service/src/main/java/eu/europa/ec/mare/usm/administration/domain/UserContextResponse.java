package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a comprehensive user role response
 */
public class UserContextResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<ComprehensiveUserContext> results;

    public UserContextResponse() {
    }

    public List<ComprehensiveUserContext> getResults() {
        return results;
    }

    public void setResults(List<ComprehensiveUserContext> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "UserContextResponse{results=" + results + '}';
    }

}
