package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Feature details.
 */
public class Feature implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long featureId;
  private String name;
  private String description;
  private String group;
  private String applicationName;
  private List<Role> roles;

  /**
   * Creates a new instance.
   */
  public Feature() {
  }


  /**
   * Get the value of feature's name
   *
   * @return the value of feature's name
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
	   * Get the value of feature's description
	   *
	   * @return the value of feature's description
	   */
	public String getDescription() {
		return description;
	}


	/**
	   * Set the value of feature's description
	   *
	   * @param description new value of feature's description
	   */
	public void setDescription(String description) {
		this.description = description;
	}

  /**
	 * @return the featureId
	 */
	public Long getFeatureId() {
		return featureId;
	}


	/**
	 * @param featureId the featureId to set
	 */
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}


/**
	 * @return the feature
	 */
	public String getGroup() {
		return group;
	}


	/**
	 * @param feature the feature to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}


	/**
	 * Gets the application name.
	 *
	 * @return the application name
	 */
	public String getApplicationName() {
		return applicationName;
	}


	/**
	 * Sets the application name.
	 *
	 * @param applicationName the new application name
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}


	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
/**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Feature{" + 
            "featureId=" + featureId +
            "name=" + name + 
            ", description=" + description +
            ", group = "+group + 
            ", application = "+applicationName + 
            '}';
  }

}
