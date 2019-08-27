package org.diceresearch.qualitymetricsservice.utility.civet.exceptions;

/**
 * Thrown, if is tried to use an ID, which has not been defined.
 *
 * @author Adrian Wilke
 */
public class UnknownIdRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnknownIdRuntimeException() {
		super();
	}

	public UnknownIdRuntimeException(String message) {
		super(message);
	}

	public UnknownIdRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownIdRuntimeException(Throwable cause) {
		super(cause);
	}

}