package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EndPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String status;
    private String uri;
    private String email;
    private Long endpointId;
    private List<Channel> channelList = new ArrayList<>();
    private String organisationName;
    private List<EndPointContact> persons = new ArrayList<>();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<EndPointContact> getPersons() {
        return persons;
    }

    public void setPersons(List<EndPointContact> persons) {
        this.persons = persons;
    }

    @Override
    public String toString() {
        return "EndPoint{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", uri='" + uri + '\'' +
                ", email='" + email + '\'' +
                ", endpointId=" + endpointId +
                ", channelList=" + channelList +
                ", organisationName='" + organisationName + '\'' +
                ", persons=" + persons +
                '}';
    }
}
