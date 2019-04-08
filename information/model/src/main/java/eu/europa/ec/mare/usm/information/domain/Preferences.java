package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Holds a set of Preferences
 */
public class Preferences implements Serializable {
	private static final long serialVersionUID = 1L;
  private Set<Preference> preferences;

  /**
   * Creates a new instance
   */
  public Preferences() {
  }

  
  /**
   * Get the value of preferences
   *
   * @return the value of preferences
   */
  public Set<Preference> getPreferences() {
    return preferences;
  }

  /**
   * Set the value of preferences
   *
   * @param preferences new value of preferences
   */
  public void setPreferences(Set<Preference> preferences) {
    this.preferences = preferences;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "Preferences{" + "preferences=" + preferences + '}';
  }
}
