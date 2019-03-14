package eu.europa.ec.mare.usm.administration.domain;


/**
 * Contact details pending review by an administrator.
 */
public class PendingContactDetails extends ContactDetails {
	private static final long serialVersionUID = 1L;
  private String userName;
  

  /**
   * Creates a new instance.
   */
  public PendingContactDetails() {
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
    return "PendingContactDetails{" + 
            "userName=" + userName + 
            ", firstName=" + getFirstName() + 
            ", lastName=" + getLastName() + 
            ", phoneNumber=" + getPhoneNumber() + 
            ", mobileNumber=" + getMobileNumber() + 
            ", faxNumber=" + getFaxNumber() + 
            ", email=" + getEmail() + 
            '}';
  }
}
