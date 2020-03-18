package eu.europa.ec.mare.usm.authentication.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Holds a status response
 */
@XmlRootElement
public class StatusResponse implements Serializable {
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
