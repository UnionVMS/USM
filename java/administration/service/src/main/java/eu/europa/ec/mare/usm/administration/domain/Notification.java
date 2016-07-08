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
 * Holds details of a notification e-mail
 */
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sender;
	private String recipient;
	private String subject;
	private String content;

	/**
	 * Creates a new instance.
	 */
	public Notification() {
	}

	/**
	 * Get the value of sender
	 *
	 * @return the value of sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Set the value of sender
	 *
	 * @param sender
	 *            new value of sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * Get the value of recipient
	 *
	 * @return the value of recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Set the value of recipient
	 *
	 * @param recipient
	 *            new value of recipient
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Get the value of subject
	 *
	 * @return the value of subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Set the value of subject
	 *
	 * @param subject
	 *            new value of subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Get the value of content
	 *
	 * @return the value of content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the value of content
	 *
	 * @param content
	 *            new value of content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "Notification{" + "sender=" + sender + ", recipient="
				+ recipient + ", subject=" + subject + ", content=" + content
				+ '}';
	}

}