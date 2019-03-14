package eu.europa.ec.mare.usm.information.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import eu.europa.ec.mare.usm.information.domain.DataSet;


/**
 * JPA Mapping for the DATASET_T table.
 */
@Entity
@SequenceGenerator(name = "datasetSequence", sequenceName = "SQ_DATASET", allocationSize = 1)
@Table(name = "DATASET_T")
@NamedQueries({
  @NamedQuery(name = "DatasetEntity.findByDatasetId", query = "SELECT d FROM DatasetEntity d WHERE d.datasetId = :datasetId"),
  @NamedQuery(name = "DatasetEntity.findByApplicationId", query = "SELECT o FROM DatasetEntity o WHERE o.application.applicationId = :applicationId"),
  @NamedQuery(name = "DatasetEntity.findByName", query = "SELECT d FROM DatasetEntity d WHERE d.name = :name")})
public class DatasetEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "DATASET_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datasetSequence")
  private Long datasetId;
  
  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;
  
  @Column(name = "DESCRIPTION")
  private String description;
  
  @Basic(optional = false)
  @Column(name = "CATEGORY")
  private String category;

  @Column(name = "DISCRIMINATOR")
  private String discriminator;

  
  @ManyToMany(mappedBy = "datasetList")
  private List<ScopeEntity> scopeList;
  
  @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
  @ManyToOne(optional = false)
  private ApplicationEntity application;

  public DatasetEntity() {
  }

  public Long getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(Long datasetId) {
    this.datasetId = datasetId;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public void setDiscriminator(String discriminator) {
    this.discriminator = discriminator;
  }

  public List<ScopeEntity> getScopeList() {
    return scopeList;
  }

  public void setScopeList(List<ScopeEntity> scopeList) {
    this.scopeList = scopeList;
  }

  public ApplicationEntity getApplication() {
    return application;
  }

  public void setApplication(ApplicationEntity application) {
    this.application = application;
  }

  @Override
  public String toString() {
    return "DatasetEntity{" + 
            "datasetId=" + datasetId + 
            ", name=" + name + 
            ", description=" + description + 
            ", category=" + category + 
            ", discriminator=" + discriminator + 
            '}';
  }

/*  public boolean isSameDataset(DataSet dataset){
	  if (!this.datasetId.equals(dataset.getDatasetId())){
		  return false;
	  }
	  else return true;
  }*/
}
