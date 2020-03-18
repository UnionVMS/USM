package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds interconnection information about an information system
 * operated by an organisation.
 */
public class EndPoint implements Serializable {
    private static final long serialVersionUID = 2L;

    private String name;
    private String description;
    private String uri;
    private String email;
    private boolean enabled;
    private List<Channel> channels;
    private List<ContactDetails> contactDetails;

    public EndPoint() {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<ContactDetails> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<ContactDetails> contactDetails) {
        this.contactDetails = contactDetails;
    }

    @Override
    public String toString() {
        return "EndPoint{" +
                "name=" + name +
                ", description=" + description +
                ", uri=" + uri +
                ", email=" + email +
                ", enabled=" + enabled +
                ", channel=" + channels +
                ", contactDetails=" + contactDetails +
                '}';
    }
}
