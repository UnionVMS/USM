package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class FindDataSetQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String applicationName;
	private String category;
	
	public FindDataSetQuery() {
		super();
	}
	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}
	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FindDataSetQuery [applicationName=" + applicationName
				+ ", category=" + category + "]";
	}
	
	
	
}
