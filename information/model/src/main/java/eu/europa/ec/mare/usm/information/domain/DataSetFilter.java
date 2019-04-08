package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;


public class DataSetFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	private String applicationName;
	private String name;
	private String category;
	private String discriminator;

	/**
	 * Creates a new instance
	 */
	public DataSetFilter() {
	}

	/**
	 * Get the value of applicationName
	 *
	 * @return the value of applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Set the value of applicationName
	 *
	 * @param applicationName
	 *            new value of applicationName
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	/**
	 * Formats a human-readable view of this instance.
	 * 
	 * @return a human-readable view
	 */
	@Override
	public String toString() {
		return "DataSetFilter{" + "applicationName=" + applicationName + ", name=" + name + ", category="
				+ category + ", discriminator=" + discriminator + '}';
	}

}
