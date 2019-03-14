package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a comprehensive scope info
 */
public class ComprehensiveScope implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String status;
	private Long scopeId;
	
	/**
	   * Gets the value of scope name
	   *
	   * @return the value of scope name
	   */
	public String getName() {
		return name;
	}

	/**
	   * Sets the value of scope name
	   *
	   * @param name new value of scope name
	   */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	   * Gets the value of status
	   *
	   * @return the value of status
	   */
	public String getStatus() {
		return status;
	}

	/**
	   * Sets the value of status
	   *
	   * @param status new value of status
	   */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	   * Gets the value of scope id
	   *
	   * @return the value of scope id
	   */
	public Long getScopeId() {
		return scopeId;
	}

	/**
	   * Sets the value of scope id
	   *
	   * @param scopeId new value of scope id
	   */
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

  /**
   * Creates a new instance.
   */
  public ComprehensiveScope() {
  }
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "ScopeQuery{" +
    		"name=" + name + 
    		", status=" + status +
            ", scopeId=" + scopeId +
            '}';
  }
  
}
