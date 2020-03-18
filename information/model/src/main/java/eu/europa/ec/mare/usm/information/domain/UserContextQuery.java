package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

public class UserContextQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String applicationName = null;

    public UserContextQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "UserContextQuery{" +
                "userName=" + userName +
                ", applicationName=" + applicationName +
                '}';
    }
}
