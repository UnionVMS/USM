package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query for the retrieval of a scope.
 */
public class GetScopeQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long scopeId;

	/**
	 * Creates a new instance.
	 */
	public GetScopeQuery() {
	}

	public GetScopeQuery(Long scopeId) {
		this.scopeId = scopeId;
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
	 * @param scopeId
	 *            new value of scope id
	 */
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	/**
	 * Formats a human-readable view of this instance.
	 *
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "GetScopeQuery{" + "scopeId=" + scopeId + '}';
	}

}
