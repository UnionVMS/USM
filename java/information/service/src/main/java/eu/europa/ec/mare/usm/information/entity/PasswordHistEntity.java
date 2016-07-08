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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * JPA Mapping for the PASSWORD_HIST_T table.
 */
@Entity
@SequenceGenerator(name = "passwordHistSequence", sequenceName = "SQ_PASSWORD_HIST", allocationSize = 1)
@Table(name = "PASSWORD_HIST_T")
@NamedQueries({
  @NamedQuery(name = "PasswordHistEntity.findByPasswordHistId", 
              query = "SELECT p FROM PasswordHistEntity p WHERE p.passwordHistId = :passwordHistId"),
  @NamedQuery(name = "PasswordHistEntity.findByUserName", 
              query = "SELECT p FROM PasswordHistEntity p WHERE p.user.userName = :userName ORDER BY p.changedOn DESC")})
public class PasswordHistEntity  extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "PASSWORD_HIST_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordHistSequence")
  private Long passwordHistId;
  
  @Basic(optional = false)
  @Column(name = "PASSWORD")
  private String password;

  @Basic(optional = false)
  @Column(name = "CHANGED_ON")
  @Temporal(TemporalType.TIMESTAMP)
  private Date changedOn;

  @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
  @ManyToOne(optional = false)
  private UserEntity user;

  public PasswordHistEntity() {
  }

  public Long getPasswordHistId() {
    return passwordHistId;
  }

  public void setPasswordHistId(Long passwordHistId) {
    this.passwordHistId = passwordHistId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getChangedOn() {
    return changedOn;
  }

  public void setChangedOn(Date changedOn) {
    this.changedOn = changedOn;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "PasswordHistEntity{" + 
            "passwordHistId=" + passwordHistId + 
            ", changedOn=" + changedOn + 
            '}';
  }
}