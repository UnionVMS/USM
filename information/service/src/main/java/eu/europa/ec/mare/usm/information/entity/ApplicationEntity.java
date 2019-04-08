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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * JPA Mapping for the APPLICATION_T table.
 */
@Entity
@SequenceGenerator(name = "applicationSequence", sequenceName = "SQ_APPLICATION", allocationSize = 1)
@Table(name = "APPLICATION_T")
@NamedQueries({
  @NamedQuery(name = "ApplicationEntity.findByApplicationId",
              query = "SELECT a FROM ApplicationEntity a" +
                      " WHERE a.applicationId = :applicationId"),
  @NamedQuery(name = "ApplicationEntity.findByName",
              query = "SELECT a FROM ApplicationEntity a" +
                      " WHERE a.name = :name")})
public class ApplicationEntity extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @Column(name = "APPLICATION_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicationSequence")
  private Long applicationId;

  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
  private List<DatasetEntity> datasetList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
  private List<FeatureEntity> featureList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
  private List<OptionEntity> optionList;



  @OneToMany(mappedBy = "parentApplication")
  private List<ApplicationEntity> childApplicationList;

  @JoinColumn(name = "PARENT_ID", referencedColumnName = "APPLICATION_ID")
  @ManyToOne
  private ApplicationEntity parentApplication;

  public ApplicationEntity() {
  }

  public Long getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Long applicationId) {
    this.applicationId = applicationId;
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

  public List<DatasetEntity> getDatasetList() {
    return datasetList;
  }

  public void setDatasetList(List<DatasetEntity> datasetList) {
    this.datasetList = datasetList;
  }

  public List<FeatureEntity> getFeatureList() {
    return featureList;
  }

  public void setFeatureList(List<FeatureEntity> featureList) {
    this.featureList = featureList;
  }

  public List<OptionEntity> getOptionList() {
    return optionList;
  }

  public void setOptionList(List<OptionEntity> optionList) {
    this.optionList = optionList;
  }

  public ApplicationEntity getParentApplication() {
	return parentApplication;
  }

  public void setParentApplication(ApplicationEntity parentApplication) {
	this.parentApplication = parentApplication;
  }

  public List<ApplicationEntity> getChildApplicationList() {
	return childApplicationList;
}

public void setChildApplicationList(List<ApplicationEntity> childApplicationList) {
	this.childApplicationList = childApplicationList;
}

  @Override
  public String toString() {
    return "ApplicationEntity{" +
              "applicationId=" + applicationId +
              ", name=" + name +
              ", description=" + description +
              '}';
  }
}
