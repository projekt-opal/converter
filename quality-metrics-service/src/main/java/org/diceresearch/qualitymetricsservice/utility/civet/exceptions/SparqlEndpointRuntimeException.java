package org.diceresearch.qualitymetricsservice.utility.civet.exceptions;

/**
 * Thrown on errors while connecting to SPARQL endpoint.
 *
 * @author Adrian Wilke
 */
public class SparqlEndpointRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SparqlEndpointRuntimeException() {
		super();
	}

	public SparqlEndpointRuntimeException(String message) {
		super(message);
	}

	public SparqlEndpointRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SparqlEndpointRuntimeException(Throwable cause) {
		super(cause);
	}

}