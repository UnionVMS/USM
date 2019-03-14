package eu.europa.ec.mare.usm.information.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * JPA Mapping for the USER_T table.
 */
@Entity
@SequenceGenerator(name = "userSequence", sequenceName = "SQ_USER", allocationSize = 1)
@Table(name = "USER_T")
@NamedQueries({
  @NamedQuery(name = "UserEntity.findByUserId", 
              query = "SELECT u FROM UserEntity u WHERE u.userId = :userId"),
  @NamedQuery(name = "UserEntity.findByUserName", 
              query = "SELECT u FROM UserEntity u WHERE u.userName = :userName"),
  @NamedQuery(name = "UserEntity.findByOrganisationId", 
              query = "Select u from UserEntity u where u.organisation.organisationId=:organisationId")})
public class UserEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "USER_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequence")
  private Long userId;
  
  @Basic(optional = false)
  @Column(name = "USER_NAME")
  private String userName;
  
  @Basic(optional = false)
  @Column(name = "STATUS")
  private String status;
  
  @Column(name = "ACTIVE_FROM")
  @Temporal(TemporalType.TIMESTAMP)
  private Date activeFrom;
  
  @Column(name = "ACTIVE_TO")
  @Temporal(TemporalType.TIMESTAMP)
  private Date activeTo;
  
  @Column(name = "LAST_LOGON")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastLogon;
  
  @Column(name = "LOCKOUT_TO")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lockoutTo;
  
  @Column(name = "PASSWORD_EXPIRY")
  @Temporal(TemporalType.TIMESTAMP)
  private Date passwordExpiry;
  
  @Column(name = "EXPIRY_NOTIFICATION")
  @Temporal(TemporalType.TIMESTAMP)
  private Date expiryNotification;
  
  @Column(name = "PASSWORD")
  private String password;
  
  @Column(name = "LOCKOUT_REASON")
  private String lockoutReason;
  
  @Column(name = "LOGON_FAILURE")
  private Integer logonFailure;
  
  @Column(name = "NOTES")
  private String notes;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<ChallengeEntity> challengeList;

  @JoinColumn(name = "PERSON_ID", referencedColumnName = "PERSON_ID")
  @ManyToOne
  private PersonEntity person;
  
  @JoinColumn(name = "ORGANISATION_ID", referencedColumnName = "ORGANISATION_ID")
  @ManyToOne
  private OrganisationEntity organisation;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<PasswordHistEntity> passwordHistList;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<UserContextEntity> userContextList;

  public UserEntity() {
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public Date getExpiryNotification() {
    return expiryNotification;
  }

  public void setExpiryNotification(Date expiryNotification) {
    this.expiryNotification = expiryNotification;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLockoutReason() {
    return lockoutReason;
  }

  public void setLockoutReason(String lockoutReason) {
    this.lockoutReason = lockoutReason;
  }

  public Integer getLogonFailure() {
    return logonFailure;
  }

  public void setLogonFailure(Integer logonFailure) {
    this.logonFailure = logonFailure;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public List<ChallengeEntity> getChallengeList() {
    return challengeList;
  }

  public void setChallengeList(List<ChallengeEntity> challengeList) {
    this.challengeList = challengeList;
  }

  public PersonEntity getPerson() {
    return person;
  }

  public void setPerson(PersonEntity person) {
    this.person = person;
  }

  public OrganisationEntity getOrganisation() {
    return organisation;
  }

  public void setOrganisation(OrganisationEntity organisation) {
    this.organisation = organisation;
  }

  public List<PasswordHistEntity> getPasswordHistList() {
    return passwordHistList;
  }

  public void setPasswordHistList(List<PasswordHistEntity> passwordHistList) {
    this.passwordHistList = passwordHistList;
  }

  public List<UserContextEntity> getUserContextList() {
    return userContextList;
  }

  public void setUserContextList(List<UserContextEntity> userContextList) {
    this.userContextList = userContextList;
  }

  @Override
  public String toString() {
    return "UserEntity{" + 
            "userId=" + userId + 
            ", userName=" + userName + 
            ", status=" + status + 
            ", activeFrom=" + activeFrom + 
            ", activeTo=" + activeTo + 
            ", lastLogon=" + lastLogon + 
            ", lockoutTo=" + lockoutTo + 
            ", passwordExpiry=" + passwordExpiry + 
            ", expiryNotification=" + expiryNotification +
            ", password=" + (password == null ? "<null>" : "******") +
            ", lockoutReason=" + lockoutReason + 
            ", logonFailure=" + logonFailure + 
            ", notes=" + notes + 
            ", person="+person+
            '}';
  }
}
