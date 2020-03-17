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

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public byte[] getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(byte[] optionValue) {
        this.optionValue = optionValue;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Preference() {
    }

    @Override
    public String toString() {
        return "UserContext{" +
                ", optionId=" + optionId +
                ", optionValue=" + optionValue +
                ", optionName=" + optionName +
                ", optionDescription=" + optionDescription +
                ", groupName=" + groupName +
                '}';
    }

}
