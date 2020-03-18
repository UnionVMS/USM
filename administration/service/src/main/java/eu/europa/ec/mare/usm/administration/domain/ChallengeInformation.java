package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class ChallengeInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long challengeId;
    private String challenge;
    private String response;

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ChallengeInformation{" +
                "challengeId=" + challengeId +
                ", challenge='" + challenge + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
