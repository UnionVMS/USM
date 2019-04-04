package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class FindOrganisationsQuery implements Serializable{
    private static final long serialVersionUID = 7199118791716052294L;

    private String name;
    private String nation;
    private String status;
    private Paginator paginator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Paginator getPaginator() {
        return paginator;
    }

     public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    /**
     * Formats a human-readable view of this instance.
     *
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "FindOrganisationsQuery{" +
                "name='" + name + '\'' +
                ", nation='" + nation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
