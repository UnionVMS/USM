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

    public DataSet() {
    }

    public DataSet(Long datasetId) {
        this.datasetId = datasetId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String toString() {
        return "DataSet [id=" + datasetId + ", name=" + name + ", category="
                + category + ", description=" + description + ", application="
                + application + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((datasetId == null) ? 0 : datasetId.hashCode());
        return result;
    }

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
