package eu.europa.ec.mare.usm.administration.rest.service.person;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;

/**
 * Holds a request to change a user contact details
 */
public class ContactDetailsRequest extends ContactDetails {
  private String userName;
  private String password;

  /**
   * Creates a new instance
   */
  public ContactDetailsRequest() {
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
   * Get the value of password
   *
   * @return the value of password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the value of password
   *
   * @param password new value of password
   */
  public void setPassword(String password) {
    this.password = password;
  }

}
