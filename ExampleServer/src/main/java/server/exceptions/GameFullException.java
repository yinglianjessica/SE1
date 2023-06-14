package server.exceptions;

public class GameFullException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameFullException(String errorMessage) {
		super("GameFullException", errorMessage);
	}

}
