package eu.europa.ec.mare.usm.session.service.impl;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds tracking information about a user session.
 */
public class UserSession  implements Serializable {
 	private static final long serialVersionUID = 1L;  
  
  private String uniqueId;
  private String userName;
  private String userSite;
  private Date creationTime;

  /**
   * Creates a new instance.
   */
  public UserSession() {
  }

  /**
   * Gets the session unique identifier
   *
   * @return the session unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Sets the session unique identifier
   *
   * @param uniqueId the session unique identifier
   */
  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
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
   * Gets the session creation time
   *
   * @return the session creation time
   */
  public Date getCreationTime() {
    return creationTime;
  }

  /**
   * Sets the session creation time
   *
   * @param creationTime the session creation time
   */
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "UserSession{" + 
            "uniqueId=" + uniqueId + 
            ", userName=" + userName + 
            ", userSite=" + userSite + 
            ", creationTime=" + creationTime + 
            '}';
  }
 
}
