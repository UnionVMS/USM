package eu.europa.ec.mare.usm.information.entity;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * JPA Mapping for the PERSON_T table.
 */
@Entity
@SequenceGenerator(name = "personSequence", sequenceName = "SQ_PERSON", allocationSize = 1)
@Table(name = "PERSON_T")
@NamedQueries({
  @NamedQuery(name = "PersonEntity.findByPersonId", query = "SELECT p FROM PersonEntity p WHERE p.personId = :personId"),
  @NamedQuery(name = "PersonEntity.findAll", query = "Select p from PersonEntity p")})
public class PersonEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "PERSON_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personSequence")
  private Long personId;

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

  @OneToMany(mappedBy="person")
  private List<UserEntity> userList;

  @OneToMany(mappedBy="person")
  private List<EndPointContactEntity> endPointContactList;

  public PersonEntity() {
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

  public String getEMail() {
    return eMail;
  }

  public void setEMail(String eMail) {
    this.eMail = eMail;
  }

  public List<UserEntity> getUserList() {
    return userList;
  }

  public void setUserList(List<UserEntity> userList) {
    this.userList = userList;
  }

  public List<EndPointContactEntity> getEndPointContactList() {
    return endPointContactList;
  }

  public void setEndPointList(List<EndPointContactEntity> endPointContactList) {
    this.endPointContactList = endPointContactList;
  }

  @Override
  public String toString() {
    return "PersonEntity{" + 
            "personId=" + personId + 
            ", firstName=" + firstName + 
            ", lastName=" + lastName + 
            ", phoneNumber=" + phoneNumber + 
            ", mobileNumber=" + mobileNumber + 
            ", faxNumber=" + faxNumber + 
            ", eMail=" + eMail + 
            '}';
  }

}
