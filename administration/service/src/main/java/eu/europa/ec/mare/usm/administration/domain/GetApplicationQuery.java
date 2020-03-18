package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * A very simple query for retrieval of an Application information.
 */
public class GetApplicationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long applicationId;


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public GetApplicationQuery() {
    }

    @Override
    public String toString() {
        return "ApplicationQuery{" +
                "applicationId=" + applicationId + '}';
    }

}
