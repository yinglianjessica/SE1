package server.exceptions;

public class InvalidTurnActionException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidTurnActionException(String errorMessage) {
		super("InvalidTurnActionException", errorMessage);
	}

}
