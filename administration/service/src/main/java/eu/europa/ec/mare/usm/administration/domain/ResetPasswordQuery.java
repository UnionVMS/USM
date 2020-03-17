package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Service request for resetting password
 */
public class ResetPasswordQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private List<ChallengeInformation> challenges;
    private String password;
    private boolean isTemporaryPassword;

    public ResetPasswordQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ChallengeInformation> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<ChallengeInformation> challenges) {
        this.challenges = challenges;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isTemporaryPassword() {
        return isTemporaryPassword;
    }

    public void setIsTemporaryPassword(boolean isTemporaryPassword) {
        this.isTemporaryPassword = isTemporaryPassword;
    }

    @Override
    public String toString() {
        return "NotificationQuery{"
                + "userName=" + userName
                + ",challenges=" + challenges
                + '}';
    }

}
