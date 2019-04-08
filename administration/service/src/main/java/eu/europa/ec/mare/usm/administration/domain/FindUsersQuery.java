package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Service request for the retrieval of a list of users.
 */
public class FindUsersQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nation;
	private String organisation;
	private String status;
	private String name;
	private Date activeFrom;
	private Date activeTo;
	private Paginator paginator;

	/**
	 * Creates a new instance.
	 */
	public FindUsersQuery() {
	}

	/**
	 * Gets the value of nation
	 *
	 * @return the value of nation
	 */
	public String getNation() {
		return nation;
	}

	/**
	 * Sets the value of nation
	 *
	 * @param nation new value of nation
	 */
	public void setNation(String nation) {
		this.nation = nation;
	}

	/**
	 * Gets the value of organisation
	 *
	 * @return the value of organisation
	 */
	public String getOrganisation() {
		return organisation;
	}

	/**
	 * Sets the value of organisation
	 *
	 * @param organisation
	 *            new value of organisation
	 */
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	/**
	 * Gets the value of activeFrom date
	 *
	 * @return the value of activeFrom date
	 */
	public Date getActiveFrom() {
		return activeFrom;
	}

	/**
	 * Sets the value of activeFrom date
	 *
	 * @param activeFrom new value of activeFrom date
	 */
	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}

	/**
	 * Gets the value of activeTo date
	 *
	 * @return the value of activeTo date
	 */
	public Date getActiveTo() {
		return activeTo;
	}

	/**
	 * Sets the value of activeTo date
	 *
	 * @param activeTo new value of activeTo date
	 */
	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	/**
	 * Gets the value of user's status
	 *
	 * @return the value of user's status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the value of user's status
	 *
	 * @param status new value of user's status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the value of name(which could be the username or the first name or the second name)
	 *
	 * @return the value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of name(which could be the username or the first name or the second name)
	 *
	 * @param name new value of name
	 */
	public void setName(String searchUser) {
		this.name = searchUser;
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
            "nation=" + nation + 
            ", organisation=" + organisation + 
            ", activeFrom=" + activeFrom + 
            ", activeTo=" + activeTo +  
            ", status=" + status + 
            ", name=" + name +
            ", paginator=" + paginator +
            '}';
	}

}
