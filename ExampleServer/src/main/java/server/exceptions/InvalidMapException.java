package server.exceptions;

public class InvalidMapException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMapException(String errorMessage) {
		super("InvalidMapException", errorMessage);
	}

}
