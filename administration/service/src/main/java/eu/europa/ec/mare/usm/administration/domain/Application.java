package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Application details.
 */
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String parent;

    public Application() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Application{" +
                "name=" + name +
                ", description=" + description +
                ", parent=" + parent +
                '}';
    }

}
