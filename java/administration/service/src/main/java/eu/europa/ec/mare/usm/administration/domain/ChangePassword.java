package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a request to change the password of a user.
 */
public class ChangePassword implements Serializable {
  private static final long serialVersionUID = 1L;
  private String userName;
  private String newPassword;
  private String currentPassword;

  /**
   * Creates a new instance.
   */
  public ChangePassword() {
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
   * Get the value of new password
   *
   * @return the value of new password
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * Set the value of new password
   *
   * @param newPassword new value of new password
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
  
  /**
   * Get the value of current password
   *
   * @return the value of current password
   */
  public String getCurrentPassword() {
		return currentPassword;
	}

  /**
   * Set the value of current password
   *
   * @param currentPassword new value of current password
   */
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "ChangePassword{" + 
            "userName=" + userName + 
            ", newPassword=" + (newPassword == null ? "<null>" : "******") +
            ", currentPassword=" + (currentPassword == null ? "<null>" : "******") +
            '}';
  }
  
}
