package eu.europa.ec.mare.usm.administration.rest;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * class to be used as wrapper class in case
 * we need to wrap json array into json object
 * for the service response
 *
 * 
 */
public class ServiceArrayResponse<T> implements Serializable {
	private static final long serialVersionUID = -3797882871515664905L;
	private List<T> results;

	/**
	 * Get the value of results
	 *
	 * @return the value of results
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Set the value of results
	 *
	 * @param results new value of results
	 */
	public void setResults(List<T> results) {
		this.results = results;
	}

}
