package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for policy search
 */
public class FindPoliciesQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private String subject;
	private String name;
	
    /**
     * Creates a new instance.
     */
	public FindPoliciesQuery() {
	}
	
	/**
 	 * Gets the subject
 	 *
 	 * @return the subject
 	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Sets the subject
	 *
	 * @param subject subject 
	 */
	public void setSubject(String subject) {
	  this.subject = subject;
	}	
	
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
     * Formats a human-readable view of this instance.
     *
     * @return a human-readable view
     */
    @Override
    public String toString() {
      return "FindPolicyQuery{"
              + "subject=" + subject
              + ", name =" + name
              + "}";
    }
	
}
