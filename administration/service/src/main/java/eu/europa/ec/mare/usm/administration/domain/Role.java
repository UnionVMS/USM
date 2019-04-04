package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds Role details
 */
public class Role implements Serializable {

  private static final long serialVersionUID = 3916859100684925516L;
  private Long roleId;
  private String name;
  private String description;
  private String status;
  private List<Feature> features;
  private int activeUsers;

  /**
   * Creates a new instance.
   */
  public Role() {
  }


  /**
   * Gets the role id.
   *
   * @return the role id
   */
  public Long getRoleId() {
		return roleId;
	}


	/**
	 * Sets the role id.
	 *
	 * @param roleId the new role id
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
	public void setName(String name) {
		this.name = name;
	}

	/**
	   * Get the value of role status
	   *
	   * @return the value of role status
	   */
	public String getStatus() {
		return status;
	}

	/**
	   * Set the value of role status
	   *
	   * @param status new value of role status
	   */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	   * Get the value of role description
	   *
	   * @return the value of role description
	   */
	public String getDescription() {
		return description;
	}


	/**
	   * Set the value of role description
	   *
	   * @param description new value of role description
	   */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	   * Get the list of role features
	   *
	   * @return the list of role features
	   */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	   * Set the list of role features
	   *
	   * @param features new list of role features
	   */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	
  /**
	 * @return the activeUsers
	 */
	public int getActiveUsers() {
		return activeUsers;
	}


	/**
	 * @param activeUsers the activeUsers to set
	 */
	public void setActiveUsers(int activeUsers) {
		this.activeUsers = activeUsers;
	}


/**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Role{" + 
            "name=" + name + 
            ", status=" + status +
            ", description=" + description + 
            ", features=" + features +
            ", activeUsers="+activeUsers+
            '}';
  }

  
}
