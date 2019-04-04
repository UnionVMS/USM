package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds User preferences details
 */
public class Preference implements Serializable {

	private static final long serialVersionUID = -7153152057236952470L;
	private String optionId;
	private byte[] optionValue;
	private String optionName;
	private String optionDescription;
	private String groupName;
  
	/**
	 * Get the value of optionId
	 *
	 * @return the value of optionId
	 */
	public String getOptionId() {
		return optionId;
	}

	/**
	 * Set the value of optionId
	 *
	 * @param optionId new value of optionId
	 */
	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	/**
	 * Get the value of optionValue
	 *
	 * @return the value of optionValue
	 */
	public byte[] getOptionValue() {
		return optionValue;
	}

	/**
	 * Set the value of optionValue
	 *
	 * @param optionValue new value of optionValue
	 */
	public void setOptionValue(byte[] optionValue) {
		this.optionValue = optionValue;
	}
	
	/**
	 * Get the value of optionName
	 *
	 * @return the value of optionName
	 */
	public String getOptionName() {
		return optionName;
	}

	/**
	 * Set the value of optionName
	 *
	 * @param optionName new value of optionName
	 */
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	/**
	 * Get the value of optionDescription
	 *
	 * @return the value of optionDescription
	 */
	public String getOptionDescription() {
		return optionDescription;
	}

	/**
	 * Set the value of optionDescription
	 *
	 * @param optionDescription new value of optionDescription
	 */
	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
	}
	
	/**
	 * Get the value of groupName
	 *
	 * @return the value of groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Set the value of groupName
	 *
	 * @param groupName new value of groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

  /**
   * Creates a new instance.
   */
  public Preference() {
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "UserContext{" + 
            ", optionId=" + optionId + 
            ", optionValue=" + optionValue +
            ", optionName=" + optionName +
            ", optionDescription=" + optionDescription +
            ", groupName=" + groupName +
            '}';
  }
  
}
