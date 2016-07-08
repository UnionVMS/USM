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

/**
 * JPA Mapping for the ROLE_T table.
 */
@Entity
@SequenceGenerator(name = "roleSequence", sequenceName = "SQ_ROLE", allocationSize = 1)
@Table(name = "ROLE_T")
@NamedQueries({
  @NamedQuery(name = "RoleEntity.findByRoleId", query = "SELECT r FROM RoleEntity r LEFT JOIN FETCH r.featureList WHERE r.roleId = :roleId"),
  @NamedQuery(name = "RoleEntity.findByName", query = "SELECT r FROM RoleEntity r WHERE r.name = :name")})
public class RoleEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "ROLE_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleSequence")
  private Long roleId;
  
  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;
  
  @Column(name = "DESCRIPTION")
  private String description;
  
  @Basic(optional = false)
  @Column(name = "STATUS")
  private String status;
  
 
  @ManyToMany
  @JoinTable(name = "PERMISSION_T", 
		  joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
		  inverseJoinColumns = {@JoinColumn(name = "FEATURE_ID", referencedColumnName = "FEATURE_ID")})
  private List<FeatureEntity> featureList;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
  private List<UserContextEntity> userContextList;

  public RoleEntity() {
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
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

  public List<FeatureEntity> getFeatureList() {
    return featureList;
  }

  public void setFeatureList(List<FeatureEntity> featureList) {
    this.featureList = featureList;
  }

  public List<UserContextEntity> getUserContextList() {
    return userContextList;
  }

  public void setUserContextList(List<UserContextEntity> userContextList) {
    this.userContextList = userContextList;
  }

  @Override
  public String toString() {
    return "RoleEntity{" + 
            "roleId=" + roleId + 
            ", name=" + name + 
            ", description=" + description + 
            ", status=" + status + 
            '}';
  }
}