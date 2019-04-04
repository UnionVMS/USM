package eu.europa.ec.mare.usm.session.domain;

import java.io.Serializable;

/**
 * Holds identification information for a user session.
 */
public class SessionInfo implements Serializable {
 	private static final long serialVersionUID = 1L;  
  
  private String userName;
  private String userSite;

  /**
   * Creates a new instance.
   */
  public SessionInfo() {
  }

  /**
   * Gets the identification of the User accessing the 
   * system/application/
   *
   * @return the identification of the User
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the identification of the User accessing the 
   * system/application
   *
   * @param userName the identification of the User
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }


  /**
   * Gets the identification of site from which the User is 
   * accessing the system/application. 
   * Typically the host-name or IP address of the workstation/device used by 
   * the user
   *
   * @return the site identification
   */
  public String getUserSite() {
    return userSite;
  }

  /**
   * Sets the identification of the site from which the User is 
   * accessing the system/application.
   * Typically the host-name or IP address of the workstation/device used by 
   * the user
   *
   * @param userSite the site identification
   */
  public void setUserSite(String userSite) {
    this.userSite = userSite;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "SessionIdentification{" + 
            "userName=" + userName + 
            ", userSite=" + userSite + 
            '}';
  }
 
}
