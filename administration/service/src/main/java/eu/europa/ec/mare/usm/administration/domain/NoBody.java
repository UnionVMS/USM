package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A no-body service request. 
 */
public class NoBody implements Serializable {

	private static final long serialVersionUID = 1L;

/**
   * Creates a new instance.
   */
  public NoBody() {
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "NoBody{}";
  }
  
}