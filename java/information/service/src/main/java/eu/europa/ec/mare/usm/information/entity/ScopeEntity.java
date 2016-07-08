/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * JPA Mapping for the SCOPE_T table.
 */
@Entity
@SequenceGenerator(name = "scopeSequence", sequenceName = "SQ_SCOPE", allocationSize = 1)
@Table(name = "SCOPE_T")
@NamedQueries({
	@NamedQuery(name = "ScopeEntity.findByScopeId", 
			query = "SELECT s FROM ScopeEntity s WHERE s.scopeId = :scopeId")})
public class ScopeEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "SCOPE_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scopeSequence")
  private Long scopeId;
  
  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;
  
  @Column(name = "DESCRIPTION")
  private String description;
  
  @Basic(optional = false)
  @Column(name = "STATUS")
  private String status;
  
  @Column(name = "ACTIVE_FROM")
  @Temporal(TemporalType.TIMESTAMP)
  private Date activeFrom;
  
  @Column(name = "ACTIVE_TO")
  @Temporal(TemporalType.TIMESTAMP)
  private Date activeTo;
  
  @Column(name = "DATA_FROM")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dataFrom;
  
  @Column(name = "DATA_TO")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dataTo;
  
  @JoinTable(name = "SCOPE_DATASET_T", joinColumns = {
		    @JoinColumn(name = "SCOPE_ID", referencedColumnName = "SCOPE_ID")}, inverseJoinColumns = {
		    @JoinColumn(name = "DATASET_ID", referencedColumnName = "DATASET_ID")})
  @ManyToMany
  private List<DatasetEntity> datasetList;

  @OneToMany(cascade=CascadeType.ALL,mappedBy = "scope")
  private List<UserContextEntity> userContextList;

  public ScopeEntity() {
  }

  public Long getScopeId() {
    return scopeId;
  }

  public void setScopeId(Long scopeId) {
    this.scopeId = scopeId;
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

  public Date getDataFrom() {
    return dataFrom;
  }

  public void setDataFrom(Date dataFrom) {
    this.dataFrom = dataFrom;
  }

  public Date getDataTo() {
    return dataTo;
  }

  public void setDataTo(Date dataTo) {
    this.dataTo = dataTo;
  }

  public List<DatasetEntity> getDatasetList() {
    return datasetList;
  }

  public void setDatasetList(List<DatasetEntity> datasetList) {
    this.datasetList = datasetList;
  }

  public List<UserContextEntity> getUserContextList() {
    return userContextList;
  }

  public void setUserContextList(List<UserContextEntity> userContextList) {
    this.userContextList = userContextList;
  }

  @Override
  public String toString() {
    return "ScopeEntity{" + 
            "scopeId=" + scopeId + 
            ", name=" + name + 
            ", description=" + description + 
            ", status=" + status + 
            ", activeFrom=" + activeFrom + 
            ", activeTo=" + activeTo + 
            ", dataFrom=" + dataFrom + 
            ", dataTo=" + dataTo + 
            '}';
  }
}