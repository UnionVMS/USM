package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

public class Preference implements Serializable {
    private static final long serialVersionUID = 1L;
    private String applicationName;
    private String optionName;
    private String optionValue;

    public Preference() {
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

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    @Override
    public String toString() {
        return "Preference{" +
                "applicationName=" + applicationName +
                ", optionName=" + optionName +
                ", optionValue=" + optionValue +
                '}';
    }

}
