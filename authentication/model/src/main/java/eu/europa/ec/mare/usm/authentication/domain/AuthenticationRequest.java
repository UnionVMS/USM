package eu.europa.ec.mare.usm.authentication.domain;

import java.io.Serializable;

/**
 * A user-id/password based authentication request.
 */
public class AuthenticationRequest extends AuthenticationQuery implements Serializable {

    private static final long serialVersionUID = -8491581342090637677L;
    private String password;

    public AuthenticationRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "userName=" + getUserName() +
                ", password=" + (password == null ? null : "******") +
                '}';
    }

}
