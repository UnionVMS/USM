package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds identification information about an organisation.
 */
public class Organisation implements Serializable {
	private static final long serialVersionUID = 2L;
	private String name;
	private String nation;
	private String email;
	private String description;
	private boolean enabled;
	private String parentOrganisation;
	private List<String> childOrganisations;
	private List<EndPoint> endPoints;

  /**
   * Creates a new instance.
   */
  public Organisation() {
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
   * Get the value of nation
   *
   * @return the value of nation
   */
  public String getNation() {
    return nation;
  }

  /**
   * Set the value of nation
   *
   * @param nation new value of nation
   */
  public void setNation(String nation) {
    this.nation = nation;
  }

  /**
   * Get the value of email
   *
   * @return the value of email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Set the value of email
   *
   * @param email new value of email
   */
  public void setEmail(String email) {
    this.email = email;
  }


  /**
   * Get the value of enabled
   *
   * @return the value of enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set the value of enabled
   *
   * @param enabled new value of enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Get the value of parentOrganisation
   *
   * @return the value of parentOrganisation
   */
  public String getParentOrganisation() {
    return parentOrganisation;
  }

  /**
   * Set the value of parentOrganisation
   *
   * @param parentOrganisation new value of parentOrganisation
   */
  public void setParentOrganisation(String parentOrganisation) {
    this.parentOrganisation = parentOrganisation;
  }
  
  /**
   * Get the value of description
   *
   * @return the value of description
   */
  public String getDescription() {
	return description;
  }

  /**
   * Set the value of description
   *
   * @param description new value of description
   */
  public void setDescription(String description) {
	this.description = description;
  }


  /**
   * Get the value of childOrganisations
   *
   * @return the value of childOrganisations
   */
  public List<String> getChildOrganisations() {
    return childOrganisations;
  }

  /**
   * Set the value of childOrganisations
   *
   * @param childOrganisations new value of childOrganisations
   */
  public void setChildOrganisations(List<String> childOrganisations) {
    this.childOrganisations = childOrganisations;
  }

  /**
   * Get the value of endPoints
   *
   * @return the value of endPoints
   */
  public List<EndPoint> getEndPoints() {
    return endPoints;
  }

  /**
   * Set the value of endPoints
   *
   * @param endPoints new value of endPoints
   */
  public void setEndPoints(List<EndPoint> endPoints) {
    this.endPoints = endPoints;
  }


  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Organisation{" + 
            "name=" + name + 
            ", nation=" + nation + 
            ", email=" + email + 
            ", enabled=" + enabled +
            ", description=" + description +
            ", parentOrganisation=" + parentOrganisation +
            ", childOrganisations=" + childOrganisations +
            ", endPoints=" + endPoints + 
            '}';
  }
}
