package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@SequenceGenerator(name = "organisationSequence", sequenceName = "SQ_ORGANISATION", allocationSize = 1)
@Table(name = "ORGANISATION_T")
@NamedQueries({
        @NamedQuery(name = "OrganisationEntity.findByOrganisationId", query = "SELECT o FROM OrganisationEntity o join fetch o.endPointList WHERE o.organisationId = :organisationId"),
        @NamedQuery(name = "OrganisationEntity.findByName", query = "SELECT o FROM OrganisationEntity o WHERE o.name = :name"),
        @NamedQuery(name = "OrganisationEntity.findByIsoa3code", query = "SELECT o FROM OrganisationEntity o WHERE o.isoa3code = :isoa3code"),
        @NamedQuery(name = "OrganisationEntity.findByStatus", query = "SELECT o FROM OrganisationEntity o WHERE o.status = :status")})
public class OrganisationEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ORGANISATION_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisationSequence")
    private Long organisationId;

    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;

    @Basic(optional = false)
    @Column(name = "ISOA3CODE")
    private String isoa3code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private String status;

    @OneToMany(mappedBy = "parentOrganisation")
    private List<OrganisationEntity> childOrganisationList;

    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ORGANISATION_ID")
    @ManyToOne
    private OrganisationEntity parentOrganisation;

    @OneToMany(mappedBy = "organisation")
    private List<UserEntity> userList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", targetEntity = EndPointEntity.class)
    private List<EndPointEntity> endPointList;

    @Column(name = "E_MAIL")
    private String email;

    public OrganisationEntity() {
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoa3code() {
        return isoa3code;
    }

    public void setIsoa3code(String isoa3code) {
        this.isoa3code = isoa3code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrganisationEntity> getChildOrganisationList() {
        return childOrganisationList;
    }

    public void setChildOrganisationList(List<OrganisationEntity> childOrganisationList) {
        this.childOrganisationList = childOrganisationList;
    }

    public OrganisationEntity getParentOrganisation() {
        return parentOrganisation;
    }

    public void setParentOrganisation(OrganisationEntity parentOrganisation) {
        this.parentOrganisation = parentOrganisation;
    }

    public List<UserEntity> getUserList() {
        return userList;
    }

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
    }

    public List<EndPointEntity> getEndPointList() {
        return endPointList;
    }

    public void setEndPointList(List<EndPointEntity> endPointList) {
        this.endPointList = endPointList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "OrganisationEntity{" +
                "organisationId=" + organisationId +
                ", name=" + name +
                ", isoa3code=" + isoa3code +
                ", description=" + description +
                ", status=" + status +
                ", email=" + email +
                '}';
    }

}
