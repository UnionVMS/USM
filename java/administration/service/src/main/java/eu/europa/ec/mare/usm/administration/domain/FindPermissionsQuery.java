package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class FindPermissionsQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	private String application;
	private String group;

	public FindPermissionsQuery() {
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "FindUsersQuery{" + 
	            ", applicationName=" + application +
	            ", group=" + group + 
	            '}';
	}

}
