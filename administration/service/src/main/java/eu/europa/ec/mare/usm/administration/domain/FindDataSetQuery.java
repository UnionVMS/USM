package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class FindDataSetQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String applicationName;
    private String category;

    public FindDataSetQuery() {
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "FindDataSetQuery [applicationName=" + applicationName
                + ", category=" + category + "]";
    }

}
