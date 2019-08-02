package org.diceresearch.qualitymetricsservice.utility.civet.access;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.access.IoUtils;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.SparqlEndpointRuntimeException;

/**
 * Data accessor for SPARQL endpoints.
 * 
 * To close the used RDF connection, call
 * {@link SparqlEndpointAccessor#close()}.
 * 
 * @author Adrian Wilke
 */
public class SparqlEndpointAccessor {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected String queryEndpoint;
	protected String updateEndpoint;
	protected RDFConnection rdfQueryConnection;
	protected RDFConnection rdfUpdateConnection;

	public SparqlEndpointAccessor(String queryEndpoint) throws NullPointerException {
		if (queryEndpoint == null) {
			throw new NullPointerException("No SPARQL query endpoint specified.");
		}
		this.queryEndpoint = queryEndpoint;
	}

	public SparqlEndpointAccessor(String queryEndpoint, String updateEndpoint) throws NullPointerException {
		if (queryEndpoint == null) {
			throw new NullPointerException("No SPARQL query endpoint specified.");
		}
		this.queryEndpoint = queryEndpoint;

		if (updateEndpoint == null) {
			LOGGER.warn("No SPARQL update endpoint specified.");
		} else {
			this.updateEndpoint = updateEndpoint;
		}

	}

	/**
	 * Connects socket to the endpoint with a specified timeout value.
	 *
	 * @throws SparqlEndpointRuntimeException on invalid URI
	 */
	public boolean pingEndpoint(int timeoutMillis) throws SparqlEndpointRuntimeException {
		try {
			URI uri = new URI("http://opalpro.cs.upb.ded:8890/sparqlXXXXXXXXX");
			return IoUtils.pingHost(uri.getHost(), uri.getPort(), timeoutMillis);
		} catch (URISyntaxException e) {
			throw new SparqlEndpointRuntimeException("SPARQL endpoint URI not valid" + e);
		}
	}

	public SparqlEndpointAccessor connectQueryEndpoint() throws SparqlEndpointRuntimeException {
		LOGGER.info("Setting query connection to " + queryEndpoint);
		rdfQueryConnection = RDFConnectionRemote.create().destination(queryEndpoint).build();
		return this;
	}

	public SparqlEndpointAccessor connectUpdateEndpoint() throws SparqlEndpointRuntimeException {
		LOGGER.info("Setting update connection to " + updateEndpoint);
		rdfUpdateConnection = RDFConnectionRemote.create().destination(updateEndpoint).build();
		return this;
	}

	public void close() {
		if (rdfQueryConnection != null && !rdfQueryConnection.isClosed()) {
			rdfQueryConnection.close();
		}
		if (rdfUpdateConnection != null && !rdfUpdateConnection.isClosed()) {
			rdfUpdateConnection.close();
		}
	}

	public boolean isQueryEndpointConnected() {
		if (rdfQueryConnection == null || rdfQueryConnection.isClosed()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isUpdateEndpointConnected() {
		if (rdfUpdateConnection == null || rdfUpdateConnection.isClosed()) {
			return false;
		} else {
			return true;
		}
	}
}