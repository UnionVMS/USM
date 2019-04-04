package eu.europa.ec.mare.usm.authentication.domain;

/**
 * A user-id/password based authentication request.
 */
public class AuthenticationRequest extends AuthenticationQuery {

	private static final long serialVersionUID = -8491581342090637677L;
	private String password;

  /**
   * Creates a new instance.
   */
  public AuthenticationRequest() {
  }

  /**
   * Get the value of password
   *
   * @return the value of password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the value of password
   *
   * @param password new value of password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "AuthenticationRequest{" +  
            "userName=" + getUserName() + 
            ", password=" + (password == null ? null : "******") + 
            '}';
  }

  
}
