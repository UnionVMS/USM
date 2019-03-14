package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of an Application information.
 */
public class GetApplicationQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long applicationId;
	
	/**
	 * Gets the value of applicationId
	 *
	 * @return the value of applicationId
	 */
	public Long getApplicationId() {
		return applicationId;
	}

	/**
	 * Sets the value of applicationId
	 *
	 * @param applicationId new value of applicationId
	 */
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

  /**
   * Creates a new instance.
   */
  public GetApplicationQuery() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ApplicationQuery{" +
    		"applicationId=" + applicationId + '}';
  }

  
}
