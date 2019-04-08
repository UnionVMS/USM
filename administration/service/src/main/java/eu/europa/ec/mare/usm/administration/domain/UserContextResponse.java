package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a comprehensive user role response
 */
public class UserContextResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<ComprehensiveUserContext> results;

	/**
	 * Creates a new instance
	 */
	public UserContextResponse() {
	}

	/**
	 * Get the results of the query
	 *
	 * @return the list of user roles
	 */
	public List<ComprehensiveUserContext> getResults() {
		return results;
	}

	/**
	 * Set the results of the query
	 *
	 * @param results
	 *            new list of user roles
	 */
	public void setResults(List<ComprehensiveUserContext> results) {
		this.results = results;
	}

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "UserContextResponse{" + 
            "results=" + results + 
            '}';
  }

}

