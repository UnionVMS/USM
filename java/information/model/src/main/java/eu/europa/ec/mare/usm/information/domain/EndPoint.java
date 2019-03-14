package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds interconnection information about an information system 
 * operated by an organisation.
 */
public class EndPoint implements Serializable {
	private static final long serialVersionUID = 2L;
	private String name;
	private String description;
	private String uri;
  private String email;
	private boolean enabled;
  private List<Channel> channels;
	private List<ContactDetails> contactDetails;

  /**
   * Creates a new instance.
   */
  public EndPoint() {
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
   * Get the value of uri
   *
   * @return the value of uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Set the value of uri
   *
   * @param uri new value of uri
   */
  public void setUri(String uri) {
    this.uri = uri;
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
   * Get the value of channels
   *
   * @return the value of channels
   */
  public List<Channel> getChannels() {
    return channels;
  }

  /**
   * Set the value of channels
   *
   * @param channels new value of channels
   */
  public void setChannels(List<Channel> channels) {
    this.channels = channels;
  }
  /**
   * Get the value of contactDetails
   *
   * @return the value of contactDetails
   */
  public List<ContactDetails> getContactDetails() {
    return contactDetails;
  }

  /**
   * Set the value of contactDetails
   *
   * @param contactDetails new value of contactDetails
   */
  public void setContactDetails(List<ContactDetails> contactDetails) {
    this.contactDetails = contactDetails;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "EndPoint{" + 
            "name=" + name + 
            ", description=" + description + 
            ", uri=" + uri + 
            ", email=" + email + 
            ", enabled=" + enabled + 
            ", channel=" + channels + 
            ", contactDetails=" + contactDetails + 
            '}';
  }
}
