package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a list of user roles.
 */
public class FindUserContextsQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userName;

	/**
	 * Creates a new instance.
	 */
	public FindUserContextsQuery() {
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
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "FindUserContextsQuery{" +   
            "userName=" + userName + 
        '}';
	}

}
