package eu.europa.ec.mare.usm.policy.service.impl;

import java.io.Serializable;
import java.util.Properties;

/**
 * Holds definition/configuration properties for a specific policy.
 */
public class PolicyDefinition implements Serializable {
    private String subject;
    private Properties properties;

    public PolicyDefinition() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "PolicyDefiniion{" +
                "subject=" + subject +
                ", properties=" + properties +
                '}';
    }

}
