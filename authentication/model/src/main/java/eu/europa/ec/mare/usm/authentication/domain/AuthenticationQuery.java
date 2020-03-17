package eu.europa.ec.mare.usm.authentication.domain;

import java.io.Serializable;

/**
 * Base class for authentication requests.
 */
public class AuthenticationQuery implements Serializable {

    private static final long serialVersionUID = -9095642037256734250L;
    private String userName;

    public AuthenticationQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AuthenticationQuery{" + "userName=" + userName + '}';
    }

}
