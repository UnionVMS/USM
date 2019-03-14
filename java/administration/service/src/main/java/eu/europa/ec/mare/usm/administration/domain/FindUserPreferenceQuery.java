package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a list of user preferences.
 */
public class FindUserPreferenceQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;
	private String groupName;

	/**
	 * Creates a new instance.
	 */
	public FindUserPreferenceQuery() {
	}

	/**
	 * Gets the value of userName
	 *
	 * @return the value of userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the value of userName
	 *
	 * @param nation new value of userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Gets the value of groupName
	 *
	 * @return the value of groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the value of groupName
	 *
	 * @param groupName new value of groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "FindUserPreferenceQuery{" +   
            "userName=" + userName + 
            ", groupName=" + groupName +
        '}';
	}

}
