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
