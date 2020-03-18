package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A query which contains the criteria for policy search
 */
public class FindPoliciesQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String subject;
    private String name;

    public FindPoliciesQuery() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FindPolicyQuery{"
                + "subject=" + subject
                + ", name =" + name
                + "}";
    }

}
