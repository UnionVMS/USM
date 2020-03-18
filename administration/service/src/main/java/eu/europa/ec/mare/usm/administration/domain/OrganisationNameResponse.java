package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class OrganisationNameResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String parentOrgName;
    private String nation;
    private String status;

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	@Override
	public String toString() {
		return "OrganisationNameResponse [parentOrgName=" + parentOrgName + ", nation=" + nation + ", status=" + status + "]";
	}

}
