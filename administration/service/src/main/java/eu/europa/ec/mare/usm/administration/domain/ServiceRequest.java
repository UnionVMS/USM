package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a service request for the management of business entities.
 *
 * @param <T> the service-request body (or payload)
 */
public class ServiceRequest<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requester;
    private String scopeName;
    private String roleName;
    private String password;
    private T body;

    public ServiceRequest() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "requester=" + requester +
                ", roleName=" + roleName +
                ", scopeName=" + scopeName +
                ", password=" + (password == null ? "null" : "******") +
                ", body=" + body +
                '}';
    }

}
