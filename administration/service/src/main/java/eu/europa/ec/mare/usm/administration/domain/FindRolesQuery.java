package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for role search 
 */
public class FindRolesQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String roleName;
	private String applicationName;
	private String status;
	private Paginator paginator;

	/**
	 * Creates a new instance.
	 */
	public FindRolesQuery() {
	}
	
	/**
	 * Gets the value of status
	 *
	 * @return the value of status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the value of status
	 *
	 * @param status new value of status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the value of role's name
	 *
	 * @return the value of role's name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Sets the value of role's name
	 *
	 * @param roleName new value of role's name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * Gets the value of application's name
	 *
	 * @return the value of application's name
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Sets the value of application's name
	 *
	 * @param applicationName new value of application's name
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	/**
	 * Gets the value of paginator
	 *
	 * @return the value of paginator
	 */
	public Paginator getPaginator() {
		return paginator;
	}

	/**
	 * Sets the value of paginator
	 *
	 * @param paginator new value of paginator
	 */
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}


	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "FindUsersQuery{" + 
            "roleName=" + roleName +
            ", applicationName=" + applicationName +
            ", status=" + status + 
            ", paginator=" + paginator +
            '}';
	}

}
