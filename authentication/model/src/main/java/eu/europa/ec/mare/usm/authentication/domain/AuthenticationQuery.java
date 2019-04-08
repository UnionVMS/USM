package eu.europa.ec.mare.usm.authentication.domain;

import java.io.Serializable;

/**
 * Base class for authentication requests.
 */
public class AuthenticationQuery implements Serializable {
	
	private static final long serialVersionUID = -9095642037256734250L;
	private String userName;

  /**
   * Creates a new instance.
   */
  public AuthenticationQuery() {
  }

  
  /**
   * Get the value of userName
   *
   * @return the value of userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set the value of userName
   *
   * @param userName new value of userName
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "AuthenticationQuery{" + "userName=" + userName + '}';
  }

  
}
