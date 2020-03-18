package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the user's notification
 */
public class NotificationQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;

    public NotificationQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "NotificationQuery{"
                + "userName=" + userName
                + '}';
    }

}
