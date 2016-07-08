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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * JPA Mapping for the CHALLENGE_T table.
 */
@Entity
@SequenceGenerator(name = "challengeSequence", sequenceName = "SQ_CHALLENGE", allocationSize = 1)
@Table(name = "CHALLENGE_T")
@NamedQueries({
  @NamedQuery(name = "ChallengeEntity.findByChallengeId", query = "SELECT c FROM ChallengeEntity c WHERE c.challengeId = :challengeId"),
  @NamedQuery(name = "ChallengeEntity.findByUserName", query = "SELECT c FROM ChallengeEntity c WHERE c.user.userName = :userName ORDER BY c.challengeId ASC")
})
public class ChallengeEntity extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "CHALLENGE_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "challengeSequence")
  private Long challengeId;

  @Basic(optional = false)
  @Column(name = "CHALLENGE")
  private String challenge;

  @Basic(optional = false)
  @Column(name = "RESPONSE")
  private String response;

  @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
  @ManyToOne(optional = false)
  private UserEntity user;

  public ChallengeEntity() {
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public String getChallenge() {
    return challenge;
  }

  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "ChallengeEntity{" + 
            "challengeId=" + challengeId + 
            ", challenge=" + challenge + 
            ", response=" + (response == null ? "<null>" : "******") +
            '}';
  }
  
  
}