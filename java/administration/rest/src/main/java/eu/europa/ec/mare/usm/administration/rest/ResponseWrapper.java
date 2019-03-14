package eu.europa.ec.mare.usm.administration.rest;

import java.io.Serializable;

/**
 * 
 * class to be used as wrapper class in case
 * we need to wrap a single class into json object for the service response
 *
 */
public class ResponseWrapper<T> implements Serializable {
	private static final long serialVersionUID = 1;
	private T result;

	/**
	 * Get the value of result
	 *
	 * @return the value of result
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Set the value of result
	 *
	 * @param result new value of result
	 */
	public void setResult(T result) {
		this.result = result;
	}

}
