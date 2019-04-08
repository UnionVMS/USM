package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class Permission implements Serializable {
  private static final long serialVersionUID = 2L;

  private Long roleId;
  private Long featureId;

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public Long getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Long featureId) {
    this.featureId = featureId;
  }

  @Override
  public String toString() {
    return "Permission ["
            + "roleId=" + roleId
            + ", featureId=" + featureId
            + "]";
  }

}
