package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a user's security question list
 */
public class ResetPasswordResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> results;

    public ResetPasswordResponse() {
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ResetPasswordResponse{" +
                "results=" + results +
                '}';
    }

}
