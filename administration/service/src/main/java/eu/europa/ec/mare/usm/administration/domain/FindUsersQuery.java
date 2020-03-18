package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Service request for the retrieval of a list of users.
 */
public class FindUsersQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nation;
    private String organisation;
    private String status;
    private String name;
    private Date activeFrom;
    private Date activeTo;
    private Paginator paginator;

    public FindUsersQuery() {
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the value of name(which could be the username or the first name or the second name)
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of name(which could be the username or the first name or the second name)
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    @Override
    public String toString() {
        return "FindUsersQuery{" +
                "nation=" + nation +
                ", organisation=" + organisation +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", status=" + status +
                ", name=" + name +
                ", paginator=" + paginator +
                '}';
    }

}
