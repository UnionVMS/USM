package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds User details
 */
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private String userName;
  private String status;
  private String lockoutReason;
  private String notes;
  private int logonFailure;
  private Date activeFrom;
  private Date activeTo;
  private Date lastLogon;
  private Date lockoutTo;
  private Date passwordExpiry;
  private Organisation organisation;
  private String parentOrganisation;

  /**
   * Creates a new instance.
   */
  public User() {
  }

  /**
   * Get the value of user's status
   *
   * @return the value of user's status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the value of user's status
   *
   * @param status new value of user's status
   */
  public void setStatus(String status) {
    this.status = status;
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
   * Get the value of activeFrom date
   *
   * @return the value of activeFrom date
   */
  public Date getActiveFrom() {
    return activeFrom;
  }

  /**
   * Set the value of activeFrom date
   *
   * @param activeFrom new value of activeFrom date
   */
  public void setActiveFrom(Date activeFrom) {
    this.activeFrom = activeFrom;
  }

  /**
   * Get the value of activeTo date
   *
   * @return the value of activeTo date
   */
  public Date getActiveTo() {
    return activeTo;
  }

  /**
   * Set the value of activeTo date
   *
   * @param activeTo new value of activeTo date
   */
  public void setActiveTo(Date activeTo) {
    this.activeTo = activeTo;
  }

  /**
   * Get the value of lastLogon date
   *
   * @return the value of lastLogon date
   */
  public Date getLastLogon() {
    return lastLogon;
  }

  /**
   * Set the value of lastLogon date
   *
   * @param lastLogon new value of lastLogon date
   */
  public void setLastLogon(Date lastLogon) {
    this.lastLogon = lastLogon;
  }

  /**
   * Get the value of lockoutTo date
   *
   * @return the value of lockoutTo date
   */
  public Date getLockoutTo() {
    return lockoutTo;
  }

  /**
   * Set the value of lockoutTo date
   *
   * @param lockoutTo new value of lockoutTo date
   */
  public void setLockoutTo(Date lockoutTo) {
    this.lockoutTo = lockoutTo;
  }

  /**
   * Get the value of passwordExpiry date
   *
   * @return the value of passwordExpiry date
   */
  public Date getPasswordExpiry() {
    return passwordExpiry;
  }

  /**
   * Set the value of passwordExpiry date
   *
   * @param passwordExpiry new value of passwordExpiry date
   */
  public void setPasswordExpiry(Date passwordExpiry) {
    this.passwordExpiry = passwordExpiry;
  }

  /**
   * Get the value of lockoutReason
   *
   * @return the value of lockoutReason
   */
  public String getLockoutReason() {
    return lockoutReason;
  }

  /**
   * Set the value of lockoutReason
   *
   * @param logoutReason new value of lockoutReason
   */
  public void setLockoutReason(String logoutReason) {
    this.lockoutReason = logoutReason;
  }

  /**
   * Get the value of logonFailure
   *
   * @return the value of logonFailure
   */
  public int getLogonFailure() {
    return logonFailure;
  }

  /**
   * Set the value of logonFailure
   *
   * @param logonFailure new value of logonFailure
   */
  public void setLogonFailure(int logonFailure) {
    this.logonFailure = logonFailure;
  }

  /**
   * Get the value of notes
   *
   * @return the value of notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Set the value of notes
   *
   * @param notes new value of notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Get the value of organisation
   *
   * @return the value of organisation
   */
  public Organisation getOrganisation() {
    return organisation;
  }

  /**
   * Set the value of organisation
   *
   * @param organisation new value of organisation
   */
  public void setOrganisation(Organisation organisation) {
    this.organisation = organisation;
  }

  /**
   * @return the parentOrganisation
   */
  public String getOrganisation_parent() {
    return parentOrganisation;
  }

  /**
   * @param organisation_parent the parentOrganisation to set
   */
  public void setOrganisation_parent(String organisation_parent) {
    this.parentOrganisation = organisation_parent;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "User{"
            + "userName=" + userName
            + ", status=" + status
            + ", activeFrom=" + activeFrom
            + ", activeTo=" + activeTo
            + ", lastLogon=" + lastLogon
            + ", lockoutTo=" + lockoutTo
            + ", passwordExpiry=" + passwordExpiry
            + ", lockoutReason=" + lockoutReason
            + ", logonFailure=" + logonFailure
            + ", notes=" + notes
            + ", organisation=" + organisation
            + ", parentOrganisation=" + parentOrganisation
            + '}';
  }
}
