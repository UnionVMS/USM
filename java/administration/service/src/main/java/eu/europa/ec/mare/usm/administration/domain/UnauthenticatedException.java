package eu.europa.ec.mare.usm.administration.domain;

/**
 * Exception used to report the fact that the requested operation
 * may only be executed by authenticated users.
 */
public class UnauthenticatedException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
  /**
   * Creates a new instance with the provided message.
   * 
   * @param message the message
   */
	public UnauthenticatedException(String message)
  {
		super(message);
	}

}
