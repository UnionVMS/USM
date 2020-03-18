package eu.europa.ec.mare.usm.session.service.impl;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds tracking information about a user session.
 */
public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uniqueId;
    private String userName;
    private String userSite;
    private Date creationTime;

    public UserSession() {
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUserName() {
        return userName;
    }

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

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

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
