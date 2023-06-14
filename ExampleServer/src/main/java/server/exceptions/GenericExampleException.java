package server.exceptions;

public class GenericExampleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String errorName;

	public GenericExampleException(String errorName, String errorMessage) {
		super(errorMessage);
		this.errorName = errorName;		
	}

	public String getErrorName() {
		return errorName;
	}
}
