package eu.europa.ec.mare.usm.session.domain;

import java.io.Serializable;

/**
 * Wraps a Session Unique Identifier.
 */
public class SessionIdWrapper implements Serializable {
  private String sessionId;

  /**
   * Creates a new instance.
   */
  public SessionIdWrapper() {
  }
  
  /**
   * Get the value of sessionId
   *
   * @return the value of sessionId
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Set the value of sessionId
   *
   * @param sessionId new value of sessionId
   */
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

}
