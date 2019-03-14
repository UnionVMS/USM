package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a user details from an LDAP 
 * compatible Identity Management system.
 */
public class GetUserQuery implements Serializable {
  private static final long serialVersionUID = 1L;
  private String userName;

  /**
   * Creates a new instance.
   */
  public GetUserQuery() {
  }

  /**
   * Gets the UsersName
   *
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the userName
   *
   * @param userName new userName
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
  public String toString() {
    return "GetUserQuery{"
            + ",userName=" + userName
            + '}';
  }

}
