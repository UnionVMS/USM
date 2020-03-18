package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a list of user preferences.
 */
public class FindUserPreferenceQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String groupName;

    public FindUserPreferenceQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "FindUserPreferenceQuery{" +
                "userName=" + userName +
                ", groupName=" + groupName +
                '}';
    }

}
