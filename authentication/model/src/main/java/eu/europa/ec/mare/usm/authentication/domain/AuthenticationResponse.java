package eu.europa.ec.mare.usm.authentication.domain;

import java.util.Map;

/**
 * Holds a response to an authentication request.
 */
public class AuthenticationResponse {
    private boolean authenticated = false;
    private int statusCode = OTHER;
    private String errorDescription = "";
    private Map<String, Object> userMap;

    /**
     * Indicates that the requested operation completed successfully.
     **/
    public static final int SUCCESS = 0;
    /**
     * Indicates an internal error.
     * The server is unable to respond with a more specific error and is also
     * unable to properly respond to a request. It does not indicate that the
     * client has sent an erroneous message.
     **/
    public static final int INTERNAL_ERROR = 1;
    /**
     * Indicates that the client passed either an incorrect user name or password.
     */
    public static final int INVALID_CREDENTIALS = 49;
    /**
     * Indicates an unknown error condition.
     */
    public static final int OTHER = 80;
    /**
     * Indicates that the login time is invalid.
     */
    public static final int INVALID_TIME = 530;
    /**
     * Indicates that the Account is disabled.
     */
    public static final int ACCOUNT_DISABLED = 533;
    /**
     * Indicates that the password has expired.
     */
    public static final int PASSWORD_EXPIRED = 701;
    /**
     * Indicates that the Account is locked.
     */
    public static final int ACCOUNT_LOCKED = 775;
    /**
     * Indicates that the user must change password.
     */
    public static final int MUST_CHANGE_PASSWORD = 773;

    public static final int MAXIMUM_SESSION_NUMBER_EXCEEDED = 774;

    public AuthenticationResponse() {
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String desc) {
        this.errorDescription = desc;
    }

    public Map<String, Object> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, Object> userMap) {
        this.userMap = userMap;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "authenticated=" + authenticated +
                ", statusCode=" + statusCode +
                ", errordesc=" + errorDescription +
                '}';
    }

}
