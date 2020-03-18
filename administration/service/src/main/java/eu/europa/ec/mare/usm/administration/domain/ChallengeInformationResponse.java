package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

public class ChallengeInformationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChallengeInformation> results;

    private String userPassword;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<ChallengeInformation> getResults() {
        return results;
    }

    public void setResults(List<ChallengeInformation> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ChallengeInformationResponse [results=" + results + "]";
    }

}
