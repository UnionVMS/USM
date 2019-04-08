package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds identification information about a set of data managed by a business
 * application.
 */
public class DataSet implements Serializable {
	private static final long serialVersionUID = 2L;
	private Long datasetId;
	private String name;
	private String category;
	private String description;
	private String application;
	private String discriminator;
	private List<Scope> scopes;

	/**
	 * Creates a new instance.
	 */
	public DataSet() {
	}

	
	public DataSet(Long datasetId) {
		super();
		this.datasetId = datasetId;
	}


	/**
	 * Get the value of name
	 *
	 * @return the value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 *
	 * @param name
	 *            new value of name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application
	 *            the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return the datasetId
	 */
	public Long getDatasetId() {
		return datasetId;
	}

	/**
	 * @param datasetId
	 *            the datasetId to set
	 */
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}

	/**
	 * Gets the scopes.
	 *
	 * @return the scopes
	 */
	public List<Scope> getScopes() {
		return scopes;
	}

	/**
	 * Sets the scopes.
	 *
	 * @param scopes
	 *            the new scopes
	 */
	public void setScopes(List<Scope> scopes) {
		this.scopes = scopes;
	}

	
	/**
	 * @return the discriminator
	 */
	public String getDiscriminator() {
		return discriminator;
	}

	/**
	 * @param discriminator the discriminator to set
	 */
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataSet [id=" + datasetId + ", name=" + name + ", category="
				+ category + ", description=" + description + ", application="
				+ application + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSet other = (DataSet) obj;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		return true;
	}
	
	

}
