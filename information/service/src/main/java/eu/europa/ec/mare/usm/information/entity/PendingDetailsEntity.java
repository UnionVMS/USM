package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * JPA Mapping for the PENDING_DETAILS_T table.
 */
@Entity
@SequenceGenerator(name = "pendingDetailsSequence", sequenceName = "SQ_PENDING_DETAILS", allocationSize = 1)
@Table(name = "PENDING_DETAILS_T")
@NamedQueries({
  @NamedQuery(name = "PendingDetailsEntity.findByPendingDetailsId", 
              query = "SELECT p FROM PendingDetailsEntity p WHERE p.pendingDetailsId = :pendingDetailsId"),
  @NamedQuery(name = "PendingDetailsEntity.findByUserName", 
              query = "SELECT p FROM PendingDetailsEntity p WHERE p.userName = :userName"),
  @NamedQuery(name = "PendingDetailsEntity.findAll", 
              query = "Select p from PendingDetailsEntity p")})
public class PendingDetailsEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "PENDING_DETAILS_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pendingDetailsSequence")
  private Long pendingDetailsId;

  @Basic(optional = false)
  @Column(name = "USER_NAME")
  private String userName;
  
  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "PHONE_NUMBER")
  private String phoneNumber;

  @Column(name = "MOBILE_NUMBER")
  private String mobileNumber;

  @Column(name = "FAX_NUMBER")
  private String faxNumber;

  @Column(name = "E_MAIL")
  private String eMail;

  public PendingDetailsEntity() {
  }

  public Long getPendingDetailsId() {
    return pendingDetailsId;
  }

  public void setPendingDetailsId(Long pendingDetailsId) {
    this.pendingDetailsId = pendingDetailsId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
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

  public String getEMail() {
    return eMail;
  }

  public void setEMail(String eMail) {
    this.eMail = eMail;
  }

  @Override
  public String toString() {
    return "PendingDetailsEntity{" + 
            "pendingDetailsId=" + pendingDetailsId + 
            ", userName=" + userName + 
            ", firstName=" + firstName + 
            ", lastName=" + lastName + 
            ", phoneNumber=" + phoneNumber + 
            ", mobileNumber=" + mobileNumber + 
            ", faxNumber=" + faxNumber + 
            ", eMail=" + eMail + 
            '}';
  }

}
