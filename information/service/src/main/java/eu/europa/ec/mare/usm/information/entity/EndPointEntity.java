package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;
import java.util.List;

/**
 * JPA Mapping for the END_POINT_T table.
 */
@Entity
@SequenceGenerator(name = "endPointSequence", sequenceName = "SQ_END_POINT", allocationSize = 1)
@Table(name = "END_POINT_T")
@NamedQueries({
  @NamedQuery(name = "EndPointEntity.findByEndPointId", query = "SELECT e FROM EndPointEntity e left join fetch e.channel WHERE e.endPointId = :endPointId"),
  @NamedQuery(name = "EndPointEntity.findByOrganisationId", query = "SELECT e FROM EndPointEntity e WHERE e.organisation.organisationId = :organisationId"),
  @NamedQuery(name = "EndPointEntity.findByOrganisationName",query = "SELECT e FROM EndPointEntity e WHERE e.name=:endpointName and e.organisation.name = :organisationName")})
public class EndPointEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "END_POINT_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endPointSequence")
  private Long endPointId;

  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Basic(optional = false)
  @Column(name = "URI")
  private String uri;

  @Basic(optional = false)
  @Column(name = "STATUS")
  private String status;
  
  @Column(name="e_mail")
  private String email;

  @OneToMany(mappedBy="endPoint")
  private List<EndPointContactEntity> endPointContact;
  
  @JoinColumn(name = "ORGANISATION_ID", referencedColumnName = "ORGANISATION_ID")
  @ManyToOne(optional = false)
  private OrganisationEntity organisation;
  
  @OneToMany(mappedBy="endPoint")
  private List<ChannelEntity> channel;

  public EndPointEntity() {
  }

  public Long getEndPointId() {
    return endPointId;
  }

  public void setEndPointId(Long endPointId) {
    this.endPointId = endPointId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

   public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public OrganisationEntity getOrganisation() {
    return organisation;
  }

  public void setOrganisation(OrganisationEntity organisation) {
    this.organisation = organisation;
  }
  
	  /**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	
	/**
	 * @return the endPointContact
	 */
	public List<EndPointContactEntity> getEndPointContact() {
		return endPointContact;
	}

	/**
	 * @param endPointContact the endPointContact to set
	 */
	public void setEndPointContact(List<EndPointContactEntity> endPointContact) {
		this.endPointContact = endPointContact;
	}

	/**
	 * @return the channel
	 */
	public List<ChannelEntity> getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(List<ChannelEntity> channel) {
		this.channel = channel;
	}

@Override
  public String toString() {
    return "EndPointEntity{" + 
            "endPointId=" + endPointId + 
            ", name=" + name + 
            ", description=" + description + 
            ", uri=" + uri + 
            ", status=" + status + 
            ", organisation=" + organisation + 
            ", email="+email+
            '}';
  }
}
