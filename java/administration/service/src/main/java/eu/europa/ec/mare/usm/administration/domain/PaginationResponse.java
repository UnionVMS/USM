package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a response that contains the response plus the total number of the results
 */
public class PaginationResponse<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<T> results;
  private int total;

	/**
	 * Creates a new instance
	 */
	public PaginationResponse() {
	}

	/**
	 * Get the results of the query
	 *
	 * @return the list of returned values
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Set the results of the query
	 *
	 * @param results
	 *            new list of returned values
	 */
	public void setResults(List<T> results) {
		this.results = results;
	}

	/**
	 * Get the value of total results
	 *
	 * @return the value of total results
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Set the value of total results
	 *
	 * @param total
	 *            value of total results
	 */
	public void setTotal(int total) {
		this.total = total;
	}

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "PaginationResponse{" + 
            "results=" + results + 
            "total=" + total + 
            '}';
  }

}

