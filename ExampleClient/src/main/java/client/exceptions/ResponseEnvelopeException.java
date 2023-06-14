package client.exceptions;

public class ResponseEnvelopeException extends RuntimeException {
	
	/**
	 * This exception is thrown when an error is received 
	 * an error occurs while networking with the server
	 * or does not adhere to the expected format.
	 */
	private static final long serialVersionUID = 1L;

	public ResponseEnvelopeException() {
		super();	}

    public ResponseEnvelopeException(String message) {
        super(message);
    }

    public ResponseEnvelopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseEnvelopeException(Throwable cause) {
        super(cause);
    }

    protected ResponseEnvelopeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
