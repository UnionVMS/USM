package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class EndPointContact implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long endPointContactId;
    private Long endPointId;
    private Long personId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String email;

    public Long getEndPointContactId() {
        return endPointContactId;
    }

    public void setEndPointContactId(Long endPointContactId) {
        this.endPointContactId = endPointContactId;
    }

    public Long getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(Long endPointId) {
        this.endPointId = endPointId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EndPointContact{" +
                "endPointContactId=" + endPointContactId +
                ", endPointId=" + endPointId +
                ", personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", faxNumber='" + faxNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
