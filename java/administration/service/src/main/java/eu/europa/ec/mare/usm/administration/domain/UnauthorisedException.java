package eu.europa.ec.mare.usm.administration.domain;

/**
 * Exception used to report the fact that a user is not 
 * authorised to perform the requested operation.
 */
public class UnauthorisedException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
    public static final String USER_UNAUTHORISED = "User is not authorized to perform the requested operation";	
	
  /**
   * Creates a new instance with the provided message.
   * 
   * @param message the message
   */
	public UnauthorisedException(String message)
  {
		super(message);
	}

}
