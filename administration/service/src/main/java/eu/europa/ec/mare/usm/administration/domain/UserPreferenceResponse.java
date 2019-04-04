package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a user's preference response
 */
public class UserPreferenceResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Preference> results;

	/**
	 * Creates a new instance
	 */
	public UserPreferenceResponse() {
	}

	/**
	 * Get the results of the query
	 *
	 * @return the list of user preferences
	 */
	public List<Preference> getResults() {
		return results;
	}

	/**
	 * Set the results of the query
	 *
	 * @param results
	 *            new list of user preferences
	 */
	public void setResults(List<Preference> results) {
		this.results = results;
	}

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "UserPreferenceResponse{" + 
            "results=" + results + 
            '}';
  }

}

