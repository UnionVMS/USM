package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@SequenceGenerator(name = "featureSequence", sequenceName = "SQ_FEATURE", allocationSize = 1)
@Table(name = "FEATURE_T")
@NamedQueries({
        @NamedQuery(name = "FeatureEntity.findAll", query = "SELECT distinct f FROM FeatureEntity f left join fetch f.roleList"),
        @NamedQuery(name = "FeatureEntity.findByFeatureId", query = "SELECT f FROM FeatureEntity f WHERE f.featureId = :featureId"),
        @NamedQuery(name = "FeatureEntity.findByApplicationId", query = "SELECT o FROM FeatureEntity o WHERE o.application.applicationId = :applicationId"),
        @NamedQuery(name = "FeatureEntity.findByName", query = "SELECT f FROM FeatureEntity f WHERE f.name = :name"),
        @NamedQuery(name = "FeatureEntity.findByApplicationName", query = "SELECT distinct f FROM FeatureEntity f left join fetch f.roleList WHERE f.application.name = :appName")})
public class FeatureEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "FEATURE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "featureSequence")
    private Long featureId;

    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(mappedBy = "featureList")
    private List<RoleEntity> roleList;

    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
    @ManyToOne(optional = false)
    private ApplicationEntity application;

    @Column(name = "GROUP_NAME")
    private String groupName;

    public FeatureEntity() {
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
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

    public List<RoleEntity> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleEntity> roleList) {
        this.roleList = roleList;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "FeatureEntity{" +
                "featureId=" + featureId +
                ", name=" + name +
                ", description=" + description +
                ", groupName = " + groupName +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((application == null) ? 0 : application.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((featureId == null) ? 0 : featureId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FeatureEntity other = (FeatureEntity) obj;
        if (application == null) {
            if (other.application != null)
                return false;
        } else if (!application.equals(other.application))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (featureId == null) {
            if (other.featureId != null)
                return false;
        } else if (!featureId.equals(other.featureId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        return true;
    }

}
