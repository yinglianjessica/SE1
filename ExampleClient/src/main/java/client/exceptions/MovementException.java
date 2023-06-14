
package client.exceptions;

public class MovementException extends Exception {

    /**
	 * This exception is thrown when an there is an error while Pathfinding
	 * generating the next move or does not adhere to the expected format.
	 */
	
	private static final long serialVersionUID = 1L;

	public MovementException() {
        super();
    }

    public MovementException(String message) {
        super(message);
    }

    public MovementException(String message, Throwable cause) {
        super(message, cause);
    }

    public MovementException(Throwable cause) {
        super(cause);
    }

    protected MovementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
