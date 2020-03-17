package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a status response
 */
public class StatusResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private int statusCode;
    private String statusMessage;

    public StatusResponse() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return statusMessage;
    }

    public void setMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "statusCode=" + statusCode +
                "message=" + statusMessage +
                '}';
    }

}
