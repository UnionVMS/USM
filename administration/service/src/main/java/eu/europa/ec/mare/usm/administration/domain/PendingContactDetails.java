package eu.europa.ec.mare.usm.administration.domain;


/**
 * Contact details pending review by an administrator.
 */
public class PendingContactDetails extends ContactDetails {
    private static final long serialVersionUID = 1L;
    private String userName;

    public PendingContactDetails() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
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
