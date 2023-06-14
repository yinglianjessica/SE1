package server.exceptions;

public class ElementNotFoundException extends GenericExampleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElementNotFoundException(String errorMessage) {
		super("ElementNotFoundException", errorMessage);
	}

}
