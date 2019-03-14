package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

/**
 * A query/request for a UserContext.
 */
public class UserContextQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String applicationName = null;

  /**
   * Creates a new instance.
   */
  public UserContextQuery() {
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
   * Get the value of applicationName
   *
   * @return the value of applicationName
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * Set the value of applicationName
   *
   * @param applicationName new value of applicationName
   */
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "UserContextQuery{" + 
            "userName=" + userName + 
            ", applicationName=" + applicationName + 
            '}';
  }
}
