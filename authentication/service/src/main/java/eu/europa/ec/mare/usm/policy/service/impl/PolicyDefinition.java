package eu.europa.ec.mare.usm.policy.service.impl;

import java.io.Serializable;
import java.util.Properties;

/**
 * Holds definition/configuration properties for a specific policy.
 */
public class PolicyDefinition implements Serializable {
  private String subject;
  private Properties properties;

  /**
   * Creates a new instance.
   */
  public PolicyDefinition() {
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
   * @param subject new value of subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Get the value of properties
   *
   * @return the value of properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * Set the value of properties
   *
   * @param properties new value of properties
   */
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "PolicyDefiniion{" + 
            "subject=" + subject + 
            ", properties=" + properties + 
            '}';
  }
  
}
