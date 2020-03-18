package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a user details from an LDAP
 * compatible Identity Management system.
 */
public class GetUserQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;

    public GetUserQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "GetUserQuery{"
                + ",userName=" + userName
                + '}';
    }

}
