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
 * JPA Mapping for CHANNEL_T table
 *
 */
@Entity
@Table(name="CHANNEL_T")
@SequenceGenerator(name = "channelSequence", sequenceName = "SQ_CHANNEL", allocationSize = 1)
@NamedQueries({
	  @NamedQuery(name = "ChannelEntity.findByChannelId", 
                query = "SELECT c FROM ChannelEntity c WHERE c.channelId = :channelId"),
    @NamedQuery(name = "ChannelEntity.findByEndPointId", 
                query = "SELECT c FROM ChannelEntity c WHERE c.endPoint.endPointId = :endPointId"),
	  @NamedQuery(name = "ChannelEntity.findByDataFlowServiceEndPoint", 
                query = "SELECT c FROM ChannelEntity c WHERE c.dataflow = :dataflow" + 
                        " and c.service=:service and c.endPoint.endPointId=:endPointId")})
public class ChannelEntity extends AbstractAuditedEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="channelSequence")
	@Column(name="CHANNEL_ID")
	private Long channelId;
	
	@Basic(optional=false)
	@Column(name="DATAFLOW")
	private String dataflow;
	
	@Basic(optional=false)
	@Column(name="SERVICE")
	private String service;
	

	@Basic(optional = false)
	@Column(name = "PRIORITY")
	private Integer priority;

	@JoinColumn(name="END_POINT_ID", referencedColumnName="END_POINT_ID")
	@ManyToOne
	private EndPointEntity endPoint;
	
	
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the channelId
	 */
	public Long getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the dataflow
	 */
	public String getDataflow() {
		return dataflow;
	}

	/**
	 * @param dataflow the dataflow to set
	 */
	public void setDataflow(String dataflow) {
		this.dataflow = dataflow;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the endPoint
	 */
	public EndPointEntity getEndPoint() {
		return endPoint;
	}

	/**
	 * @param endPoint the endPoint to set
	 */
	public void setEndPoint(EndPointEntity endPoint) {
		this.endPoint = endPoint;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelEntity [channelId=" + channelId + ", dataflow="
				+ dataflow + ", service=" + service + ", priority=" + priority
				+ ", endPoint=" + endPoint + "]";
	}
	
}