package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

public class UserContext implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String applicationName;
    private ContextSet contextSet;

    public UserContext() {
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

    public ContextSet getContextSet() {
        return contextSet;
    }

    public void setContextSet(ContextSet contextSet) {
        this.contextSet = contextSet;
    }

}
