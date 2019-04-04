package eu.europa.ec.mare.usm.administration.domain;

/**
 * Defines all known User Statuses
 */
public enum UserStatus {

  /**
   * The User is enabled and may hence use the system
   */
  ENABLED("E"),
  /**
   * The User is (permanently) disabled and may not use the system
   */
  DISABLED("D"),
  /**
   * The User is (temporarily) locked, probably due to consecutive
   * authentication failures *
   */
  LOCKED("L");

  private final String value;

  UserStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
