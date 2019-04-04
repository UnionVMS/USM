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
   * 
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

  
  /**
   * Creates a new instance.
   */
  public AuthenticationResponse() {
  }

  /**
   * Get the value of authenticated
   *
   * @return the value of authenticated
   */
  public boolean isAuthenticated() {
    return authenticated;
  }

  /**
   * Set the value of authenticated
   *
   * @param authenticated new value of authenticated
   */
  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  /**
   * Get the value of statusCode
   *
   * @return the value of statusCode
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Set the value of statusCode
   *
   * @param statusCode new value of statusCode
   */
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
	
  /**
   * Get the value of error description
   *
   * @return the value of errorDescription
   */
  public String getErrorDescription() {
    return errorDescription;
  }

  /**
   * Set the value of errorDescription
   *
   * @param desc new value of errorDescription
   */
  public void setErrorDescription(String desc) {
    this.errorDescription = desc;
  }

  public Map<String, Object> getUserMap() {
	return userMap;
  }

  public void setUserMap(Map<String, Object> userMap) {
	this.userMap = userMap;
  }
	
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "AuthenticationResponse{" +  
            "authenticated=" + authenticated + 
            ", statusCode=" + statusCode + 
			", errordesc=" + errorDescription + 
            '}';
  }

}
