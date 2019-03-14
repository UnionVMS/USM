package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of Organisation information.
 */
public class OrganisationQuery implements Serializable {
	private static final long serialVersionUID = 1L;

  /**
   * Creates a new instance.
   */
  public OrganisationQuery() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "OrganisationQuery{" + '}';
  }

  
}
