package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds one only definition/configuration property for a specific policy.
 */
public class Policy implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long policyId;
    private String name;
    private String description;
    private String subject;
    private String value;

    public Policy() {
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
