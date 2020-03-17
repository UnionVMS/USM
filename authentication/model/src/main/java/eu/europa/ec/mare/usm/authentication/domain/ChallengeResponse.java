package eu.europa.ec.mare.usm.authentication.domain;

/**
 * A challenge/response based authentication request.
 */
public class ChallengeResponse extends AuthenticationQuery {
    private static final long serialVersionUID = 1L;

    private String challenge;
    private String response;

    public ChallengeResponse() {
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
        return "ChallengeResponse{" +
                "userName=" + getUserName() +
                "challenge=" + challenge +
                ", response=" + (response == null ? null : "******") +
                '}';
    }

}
