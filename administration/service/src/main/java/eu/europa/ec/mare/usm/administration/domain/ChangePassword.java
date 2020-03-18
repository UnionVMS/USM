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

    public ChangePassword() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @Override
    public String toString() {
        return "ChangePassword{" +
                "userName=" + userName +
                ", newPassword=" + (newPassword == null ? "<null>" : "******") +
                ", currentPassword=" + (currentPassword == null ? "<null>" : "******") +
                '}';
    }

}
