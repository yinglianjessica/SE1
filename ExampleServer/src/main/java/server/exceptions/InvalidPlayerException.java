package server.exceptions;

public class InvalidPlayerException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPlayerException(String errorMessage) {
		super("InvalidPlayerException", errorMessage);
	}

}
