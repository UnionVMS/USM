package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of Application information.
 */
public class FindApplicationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String parentName;
    private Paginator paginator;

	public FindApplicationQuery() {
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public String toString() {
        return "ApplicationQuery{" +
                "name=" + name +
                ", parentName=" + parentName +
                ", paginator=" + paginator + '}';
    }

}
