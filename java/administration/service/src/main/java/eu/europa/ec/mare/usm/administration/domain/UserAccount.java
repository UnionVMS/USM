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
import java.sql.Timestamp;
import java.util.Date;

/**
 * Transfer the data for the new or updated user.
 */
public class UserAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userName;
	private String status;
	private Date activeFrom;
	private Date activeTo;
	private Date lockoutTo;
	private Date lastLogon;
	private String lockoutReason;
	private String notes;
	private Person person;
	private Organisation organisation;
	private Date passwordExpiry;
	private int logonFailure;
	//private ContactDetails contactDetails;

	// this field is only for display purposes
	private String organisation_parent;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the activeFrom
	 */
	public Date getActiveFrom() {
		return activeFrom;
	}

	/**
	 * @param activeFrom
	 *            the activeFrom to set
	 */
	public void setActiveFrom(Timestamp activeFrom) {
		this.activeFrom = activeFrom;
	}

	/**
	 * @return the activeTo
	 */
	public Date getActiveTo() {
		return activeTo;
	}

	/**
	 * @param activeTo
	 *            the activeTo to set
	 */
	public void setActiveTo(Timestamp activeTo) {
		this.activeTo = activeTo;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param activeFrom
	 *            the activeFrom to set
	 */
	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	/**
	 * @param activeTo
	 *            the activeTo to set
	 */
	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	/**
	 * @return the lockoutTo
	 */
	public Date getLockoutTo() {
		return lockoutTo;
	}

	/**
	 * @param lockoutTo
	 *            the lockoutTo to set
	 */
	public void setLockoutTo(Date lockoutTo) {
		this.lockoutTo = lockoutTo;
	}

	/**
	 * @return the lockoutReason
	 */
	public String getLockoutReason() {
		return lockoutReason;
	}

	/**
	 * @param lockoutReason
	 *            the lockoutReason to set
	 */
	public void setLockoutReason(String lockoutReason) {
		this.lockoutReason = lockoutReason;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getLastLogon() {
		return lastLogon;
	}

	public void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}

	/**
	 * @return the organisation_parent
	 */
	public String getOrganisation_parent() {
		return organisation_parent;
	}

	/**
	 * @param organisation_parent
	 *            the organisation_parent to set
	 */
	public void setOrganisation_parent(String organisation_parent) {
		this.organisation_parent = organisation_parent;
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the organisation
	 */
	public Organisation getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation
	 *            the organisation to set
	 */
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	/**
	 * @return the passwordExpiry
	 */
	public Date getPasswordExpiry() {
		return passwordExpiry;
	}

	/**
	 * @param passwordExpiry the passwordExpiry to set
	 */
	public void setPasswordExpiry(Date passwordExpiry) {
		this.passwordExpiry = passwordExpiry;
	}

	/**
	 * @return the logonFailure
	 */
	public int getLogonFailure() {
		return logonFailure;
	}

	/**
	 * @param logonFailure the logonFailure to set
	 */
	public void setLogonFailure(int logonFailure) {
		this.logonFailure = logonFailure;
	}

	/**
	 * @return the contactDetails
	 *//*
	public ContactDetails getContactDetails() {
		return contactDetails;
	}

	*//**
	 * @param contactDetails the contactDetails to set
	 *//*
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
*/
	@Override
	public String toString() {
		return "UserAccount [userName=" + userName + ", activeFrom=" + activeFrom + ", activeTo="
				+ activeTo + ", organisation=" + organisation + ", status=" + status + ", lockoutTo=" + lockoutTo
				+ ", lockoutReason=" + lockoutReason + ", notes=" + notes + ", logonFailure="+ logonFailure
				+", passwordExpiry="+passwordExpiry+", person="+person+"]";
	}

}