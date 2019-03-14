package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of role information.
 */
public class RoleQuery implements Serializable {
	private static final long serialVersionUID = 7398886843876847819L;

	/**
	 * Creates a new instance.
	 */
	public RoleQuery() {
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "RoleQuery{" + '}';
	}

}
