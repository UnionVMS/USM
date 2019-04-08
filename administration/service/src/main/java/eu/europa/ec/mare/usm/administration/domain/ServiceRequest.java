package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds a service request for the management of business entities.
 * @param <T> the service-request body (or payload)
 */
public class ServiceRequest<T extends Serializable> implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String requester;
  private String scopeName;
  private String roleName;
  private String password;
  private T body;

  
  /**
   * Creates a new instance.
   */
  public ServiceRequest() 
  {
  }
  

  /**
   * Get the value of roleName
   *
   * @return the value of roleName
   */
  public String getRoleName() {
    return roleName;
  }

  /**
   * Set the value of roleName
   *
   * @param roleName new value of roleName
   */
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }


  /**
   * Get the value of scopeName
   *
   * @return the value of scopeName
   */
  public String getScopeName() {
    return scopeName;
  }

  /**
   * Set the value of scopeName
   *
   * @param scopeName new value of scopeName
   */
  public void setScopeName(String scopeName) {
    this.scopeName = scopeName;
  }

  /**
   * Gets the name of the service requester
   *
   * @return the name of the service requester
   */
  public String getRequester() 
  {
    return requester;
  }

  /**
   * Sets the name of the service requester
   *
   * @param requester the name of the service requester
   */
  public void setRequester(String requester) 
  {
    this.requester = requester;
  }

  /**
   * Gets the password of the service requester
   * 
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the service requester
   * 
   * @param password the password 
   */
  public void setPassword(String password) 
  {
    this.password = password;
  }
  
  /**
   * Gets the service request body (or payload)
   *
   * @return the service request body
   */
  public T getBody() 
  {
    return body;
  }

  /**
   * Sets the service request body (or payload)
   *
   * @param body the service request body
   */
  public void setBody(T body) 
  {
    this.body = body;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "ServiceRequest{" + 
            "requester=" + requester + 
            ", roleName=" + roleName + 
            ", scopeName=" + scopeName + 
            ", password=" + (password == null ? "null" : "******") + 
            ", body=" + body + 
            '}';
  }

}
