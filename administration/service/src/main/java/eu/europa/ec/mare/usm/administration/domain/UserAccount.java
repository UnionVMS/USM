package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Transfer the data for the new or updated user.
 */
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String status;
    private Date activeFrom;
    private Date activeTo;
    private Date lockoutTo;
    private Date lastLogon;
    private String lockoutReason;
    private String notes;
    private Person person;
    private Organisation organisation;
    private Date passwordExpiry;
    private int logonFailure;

    // this field is only for display purposes
    private String organisation_parent;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public Date getLockoutTo() {
        return lockoutTo;
    }

    public void setLockoutTo(Date lockoutTo) {
        this.lockoutTo = lockoutTo;
    }

    public String getLockoutReason() {
        return lockoutReason;
    }

    public void setLockoutReason(String lockoutReason) {
        this.lockoutReason = lockoutReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getLastLogon() {
        return lastLogon;
    }

    public void setLastLogon(Date lastLogon) {
        this.lastLogon = lastLogon;
    }

    public String getOrganisation_parent() {
        return organisation_parent;
    }

    public void setOrganisation_parent(String organisation_parent) {
        this.organisation_parent = organisation_parent;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Date getPasswordExpiry() {
        return passwordExpiry;
    }

    public void setPasswordExpiry(Date passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    public int getLogonFailure() {
        return logonFailure;
    }

    public void setLogonFailure(int logonFailure) {
        this.logonFailure = logonFailure;
    }

    @Override
    public String toString() {
        return "UserAccount [userName=" + userName + ", activeFrom=" + activeFrom + ", activeTo="
                + activeTo + ", organisation=" + organisation + ", status=" + status + ", lockoutTo=" + lockoutTo
                + ", lockoutReason=" + lockoutReason + ", notes=" + notes + ", logonFailure=" + logonFailure
                + ", passwordExpiry=" + passwordExpiry + ", person=" + person + "]";
    }

}
