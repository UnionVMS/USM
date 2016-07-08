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
package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
/**
 *  Holds the details of a channel
 *
 */
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long channelId;
	private String dataflow;
	private String service;
	private Integer priority;
	private Long endpointId;
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
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	/**
	 * @return the endpointId
	 */
	public Long getEndpointId() {
		return endpointId;
	}
	/**
	 * @param endpointId the endpointId to set
	 */
	public void setEndpointId(Long endpointId) {
		this.endpointId = endpointId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Channel [channelId=" + channelId + ", dataflow=" + dataflow
				+ ", service=" + service + ", priority=" + priority
				+ ", endpointId=" + endpointId + "]";
	}
	
	
}