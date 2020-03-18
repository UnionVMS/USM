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

    public User() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public Date getLastLogon() {
        return lastLogon;
    }

    public void setLastLogon(Date lastLogon) {
        this.lastLogon = lastLogon;
    }

    public Date getLockoutTo() {
        return lockoutTo;
    }

    public void setLockoutTo(Date lockoutTo) {
        this.lockoutTo = lockoutTo;
    }

    public Date getPasswordExpiry() {
        return passwordExpiry;
    }

    public void setPasswordExpiry(Date passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    public String getLockoutReason() {
        return lockoutReason;
    }

    public void setLockoutReason(String logoutReason) {
        this.lockoutReason = logoutReason;
    }

    public int getLogonFailure() {
        return logonFailure;
    }

    public void setLogonFailure(int logonFailure) {
        this.logonFailure = logonFailure;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String getOrganisation_parent() {
        return parentOrganisation;
    }

    public void setOrganisation_parent(String organisation_parent) {
        this.parentOrganisation = organisation_parent;
    }

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
