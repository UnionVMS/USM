package eu.europa.ec.mare.usm.session.domain;

import java.io.Serializable;

/**
 * Wraps a Session Unique Identifier.
 */
public class SessionIdWrapper implements Serializable {
    private String sessionId;

    public SessionIdWrapper() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
