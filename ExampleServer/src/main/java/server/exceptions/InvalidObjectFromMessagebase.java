package server.exceptions;

public class InvalidObjectFromMessagebase extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidObjectFromMessagebase(String errorMessage) {
		super("InvalidObjectFromMessagebase", errorMessage);
	}

}
