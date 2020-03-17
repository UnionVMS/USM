package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a user's preference response
 */
public class UserPreferenceResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Preference> results;

    public UserPreferenceResponse() {
    }

    public List<Preference> getResults() {
        return results;
    }

    public void setResults(List<Preference> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "UserPreferenceResponse{" +
                "results=" + results +
                '}';
    }

}

