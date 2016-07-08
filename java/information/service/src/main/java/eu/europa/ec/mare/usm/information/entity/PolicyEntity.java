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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * JPA Mapping for the POLICY_T table.
 */
@Entity
@SequenceGenerator(name = "policySequence", sequenceName = "SQ_POLICY", allocationSize = 1)
@Table(name = "POLICY_T")
@NamedQueries({
  @NamedQuery(name = "PolicyEntity.findByPolicyId", query = "SELECT p FROM PolicyEntity p WHERE p.policyId = :policyId"),
  @NamedQuery(name = "PolicyEntity.findBySubject", query = "SELECT p FROM PolicyEntity p WHERE p.subject = :subject")})
public class PolicyEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "POLICY_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policySequence")
  private Long policyId;
  
  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Basic(optional = false)
  @Column(name = "SUBJECT")
  private String subject;
  
  @Basic(optional = false)
  @Column(name = "VALUE")
  private String value;
  

  public PolicyEntity() {
  }

  public Long getPolicyId() {
    return policyId;
  }

  public void setPolicyId(Long policyId) {
    this.policyId = policyId;
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

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "PolicyEntity{" + 
            "policyId=" + policyId + 
            ", name=" + name + 
            ", description=" + description + 
            ", subject=" + subject + 
            ", value=" + value + 
            '}';
  }

}