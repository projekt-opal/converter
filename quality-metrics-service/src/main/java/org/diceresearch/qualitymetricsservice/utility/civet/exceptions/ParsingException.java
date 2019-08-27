package org.diceresearch.qualitymetricsservice.utility.civet.exceptions;

/**
 * Thrown on errors while parsing.
 * 
 * Additionally to {@link java.text.ParseException}, the constructors of this
 * exception can take throwables.
 *
 * @author Adrian Wilke
 */
public class ParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParsingException() {
		super();
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

}