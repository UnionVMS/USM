package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

public class ChallengeInformationResponse implements Serializable {

    private static final long serialVersionUID = 1L;    
    
    private List<ChallengeInformation> results;
    
    private String userPassword;

    /**
     * @return the userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @param userPassword the userPassword to set
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * @return the results
     */
    public List<ChallengeInformation> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<ChallengeInformation> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ChallengeInformationResponse [results=" + results + "]";
    }
    
    
    
}
