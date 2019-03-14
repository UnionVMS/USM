package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query for the retrieval of a role.
 */
public class GetRoleQuery implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long roleId;

  /**
   * Creates a new instance.
   */
  public GetRoleQuery() {
  }
  
  public GetRoleQuery(Long roleId) {
	  this.roleId=roleId;
  }
  
  /**
   * Gets the value of role_id
   *
   * @return the value of role_id
   */
  public Long getRoleId() {
	return roleId;
  }

  /**
   * Sets the value of role_id
   *
   * @param roleId new value of role_id
   */
  public void setRoleId(Long roleId) {
	this.roleId = roleId;
  }

  /**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "GetRoleQuery{"
            + "roleId=" + roleId
            + '}';
  }

}
