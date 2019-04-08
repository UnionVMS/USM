package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a user's security question list
 */
public class ResetPasswordResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<String> results;

	/**
	 * Creates a new instance
	 */
	public ResetPasswordResponse() {
	}

	/**
	 * Get the results of the query
	 *
	 * @return the list of user security questions
	 */
	public List<String> getResults() {
		return results;
	}

	/**
	 * Set the results of the query
	 *
	 * @param results
	 *            new list of user security questions
	 */
	public void setResults(List<String> results) {
		this.results = results;
	}

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ResetPasswordResponse{" + 
            "results=" + results + 
            '}';
  }

}