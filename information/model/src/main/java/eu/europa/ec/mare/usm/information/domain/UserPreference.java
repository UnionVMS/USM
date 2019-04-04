package eu.europa.ec.mare.usm.information.domain;

public class UserPreference {

	private String userName;
	private String roleName;
	private String scopeName;
	private String applicationName;
	private String optionName;
	private byte[] optionValue;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	
	

	public byte[] getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(byte[] optionValue) {
		this.optionValue = optionValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "UserPreference{" + "userName=" + userName + 
				", roleName=" + roleName + 
				", scopeName=" + scopeName + 
				", roleName=" + roleName +
				", applicationName=" + applicationName +
				", optionName=" + optionName + 
				", optionValue=" + optionValue +'}';
	}

}
