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
 * JPA Mapping for the PREFERENCE_T table.
 */
@Entity
@SequenceGenerator(name = "preferenceSequence", sequenceName = "SQ_PREFERENCE", allocationSize = 1)
@Table(name = "PREFERENCE_T")
@NamedQueries({
  @NamedQuery(name = "PreferenceEntity.findByContextId", 
		  query = "SELECT p FROM PreferenceEntity p WHERE p.userContext.userContextId = :contextId")
  })

public class PreferenceEntity extends AbstractAuditedEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Basic(optional = false)
  @Column(name = "PREFERENCE_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preferenceSequence")
  private Long preferenceId;
  

  @Basic(optional = false)
  @Column(name = "OPTION_VALUE")
  private byte[] optionValue;

  @JoinColumn(name = "USER_CONTEXT_ID", referencedColumnName = "USER_CONTEXT_ID")
  @ManyToOne(optional = false)
  private UserContextEntity userContext;
  

  @JoinColumn(name = "OPTION_ID", referencedColumnName = "OPTION_ID")
  @ManyToOne(optional = false)
  private OptionEntity option;

  public PreferenceEntity() {
  }

  public Long getPreferenceId() {
    return preferenceId;
  }

  public void setPreferenceId(Long preferenceId) {
    this.preferenceId = preferenceId;
  }

  
  
  public byte[] getOptionValue() {
	return optionValue;
}

public void setOptionValue(byte[] optionValue) {
	this.optionValue = optionValue;
}

public UserContextEntity getUserContext() {
    return userContext;
  }

  public void setUserContext(UserContextEntity userContext) {
    this.userContext = userContext;
  }

  public OptionEntity getOption() {
    return option;
  }

  public void setOption(OptionEntity option) {
    this.option = option;
  }

  @Override
  public String toString() {
    return "PreferenceEntity{" + 
            "preferenceId=" + preferenceId + 
            ", optionValue=" + optionValue + 
            ", userContext="+userContext+
            '}';
  }
}