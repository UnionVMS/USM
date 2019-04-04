package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Base class for user retrieval requests.
 */
public class RequesterUser implements Serializable {
  private static final long serialVersionUID = 1L;
  private String requesterUserName;

  /**
   * Gets the value of requestedUserName
   *
   * @return the value of requestedUserName
   */
  public String getRequesterUserName() {
    return requesterUserName;
  }

  /**
   * Sets the value of requestedUserName
   *
   * @param requesterUserName new value of requestedUserName
   */
  public void setRequesterUserName(String requesterUserName) {
    this.requesterUserName = requesterUserName;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "RequesterUser{" + "requestedUserName=" + requesterUserName + '}';
  }

}
