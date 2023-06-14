package client.exceptions;

public class InvalidMapException extends Exception {

    /**
	 * This exception is thrown when an invalid map is detected.
	 * An invalid map could be one that is missing required data, has inconsistent data,
	 * or does not adhere to the expected format
	 */
	
	private static final long serialVersionUID = 1L;

	public InvalidMapException() {
        super();
    }

    public InvalidMapException(String message) {
        super(message);
    }

    public InvalidMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMapException(Throwable cause) {
        super(cause);
    }

    protected InvalidMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

