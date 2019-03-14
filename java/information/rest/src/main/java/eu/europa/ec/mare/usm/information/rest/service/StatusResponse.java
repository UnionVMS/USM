package eu.europa.ec.mare.usm.information.rest.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds a status response
 */
@XmlRootElement
public class StatusResponse implements Serializable {
  private int statusCode;
  private String statusMessage;

  /**
   * Creates a new instance
   */
  public StatusResponse() {
  }

  /**
   * Get the value of statusCode
   *
   * @return the value of statusCode
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Set the value of statusCode
   *
   * @param statusCode new value of statusCode
   */
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Get the value of statusMessage
   *
   * @return the value of statusMessage
   */
  public String getMessage() {
    return statusMessage;
  }

  /**
   * Set the value of statusMessage
   *
   * @param statusMessage new value of statusMessage
   */
  public void setMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "StatusResponse{" + 
            "statusCode=" + statusCode + 
            "message=" + statusMessage + 
            '}';
  }

}
