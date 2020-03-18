package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Base class for user retrieval requests.
 */
public class RequesterUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requesterUserName;

    public String getRequesterUserName() {
        return requesterUserName;
    }

    public void setRequesterUserName(String requesterUserName) {
        this.requesterUserName = requesterUserName;
    }

    @Override
    public String toString() {
        return "RequesterUser{" + "requestedUserName=" + requesterUserName + '}';
    }

}
