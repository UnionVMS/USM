package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class ChallengeInformation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long challengeId;
    
    private String challenge;
    
    private String response;
    
    /**
     * @return the challengeId
     */
    public Long getChallengeId() {
        return challengeId;
    }

    /**
     * @param challengeId the challengeId to set
     */
    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }



    /**
     * @return the challenge
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * @param challenge the challenge to set
     */
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }
}
