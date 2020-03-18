package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds details of an organisation.
 */
public class Organisation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String nation;
    private String status;
    private Long organisationId;
    private String parent;
    private List<EndPoint> endpoints;
    private String email;
    private int assignedUsers;

    public Organisation() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public List<EndPoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndPoint> endpoints) {
        this.endpoints = endpoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(int assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "name=" + name +
                ", description=" + description +
                ", nation=" + nation +
                ", status=" + status +
                ", organisationId=" + organisationId +
                ", parent=" + parent +
                ", email=" + email +
                ", enpoints=" + endpoints +
                ", assignedUsers=" + assignedUsers +
                '}';
    }

}
