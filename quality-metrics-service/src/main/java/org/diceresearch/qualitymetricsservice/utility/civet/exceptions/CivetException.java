package org.diceresearch.qualitymetricsservice.utility.civet.exceptions;

/**
 * Thrown by Civet API
 *
 * @author Adrian Wilke
 */
public class CivetException extends Exception {

	private static final long serialVersionUID = 1L;

	public CivetException() {
		super();
	}

	public CivetException(String message) {
		super(message);
	}

	public CivetException(String message, Throwable cause) {
		super(message, cause);
	}

	public CivetException(Throwable cause) {
		super(cause);
	}

}