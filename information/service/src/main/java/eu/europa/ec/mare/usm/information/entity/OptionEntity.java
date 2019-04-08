package eu.europa.ec.mare.usm.information.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * JPA Mapping for the OPTION_T table.
 */
@Entity
@SequenceGenerator(name = "optionSequence", sequenceName = "SQ_OPTION", allocationSize = 1)
@Table(name = "OPTION_T")
@NamedQueries({
  @NamedQuery(name = "OptionEntity.findByOptionId", query = "SELECT o FROM OptionEntity o WHERE o.optionId = :optionId"),
  @NamedQuery(name = "OptionEntity.findByApplicationId", query = "SELECT o FROM OptionEntity o WHERE o.application.applicationId = :applicationId")})
public class OptionEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "OPTION_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "optionSequence")
  private Long optionId;

  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "DATA_TYPE")
  private String dataType;

  
  @Column(name = "DEFAULT_VALUE")
  private byte[] defaultValue;
  
  @Column(name = "GROUP_NAME")
  private String groupName;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "option")
  private List<PreferenceEntity> preferenceList;

  @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
  @ManyToOne(optional = false)
  private ApplicationEntity application;

  public OptionEntity() {
  }

  public Long getOptionId() {
    return optionId;
  }

  public void setOptionId(Long optionId) {
    this.optionId = optionId;
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

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  
  
  public byte[] getDefaultValue() {
	return defaultValue;
}

public void setDefaultValue(byte[] defaultValue) {
	this.defaultValue = defaultValue;
}

public List<PreferenceEntity> getPreferenceList() {
    return preferenceList;
  }

  public void setPreferenceList(List<PreferenceEntity> preferenceList) {
    this.preferenceList = preferenceList;
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
    return "OptionEntity{" + 
            "optionId=" + optionId + 
            ", name=" + name + 
            ", description=" + description + 
            ", dataType=" + dataType + 
            ", defaultValue=" + defaultValue +
            ", groupName=" + groupName +
            '}';
  }
}
