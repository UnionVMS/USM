package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of an Application's parent info.
 */
public class GetParentApplicationQuery implements Serializable {
	private static final long serialVersionUID = 1L;

  /**
   * Creates a new instance.
   */
  public GetParentApplicationQuery() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ApplicationQuery{" + '}';
  }

  
}
