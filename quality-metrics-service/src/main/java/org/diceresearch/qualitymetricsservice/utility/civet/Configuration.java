package org.diceresearch.qualitymetricsservice.utility.civet;

/**
 * Container for configuration variables.
 * 
 * @author Adrian Wilke
 */
public class Configuration {

	private String sparqlQueryEndpoint;
	private String sparqlUpdateEndpoint;
	private String namedGraph;

	public String getSparqlQueryEndpoint() {
		return sparqlQueryEndpoint;
	}

	public String getSparqlUpdateEndpoint() {
		return sparqlUpdateEndpoint;
	}

	/**
	 * Sets the endpoint for SPARQL queries.
	 * 
	 * @param endpoint a URL, e.g. http://example.com:8890/sparql
	 * 
	 * @throws NullPointerException if endpoint is null
	 */
	public void setSparqlQueryEndpoint(String endpoint) {
		if (endpoint == null) {
			throw new NullPointerException("No SPARQL query endpoint specified.");
		}
		this.sparqlQueryEndpoint = endpoint;
	}

	/**
	 * Sets the endpoint for SPARQL updates.
	 * 
	 * @param endpoint a URL, e.g. http://example.com:8890/update
	 * 
	 * @throws NullPointerException if endpoint is null
	 */
	public void setSparqlUpdateEndpoint(String endpoint) {
		if (endpoint == null) {
			throw new NullPointerException("No SPARQL update endpoint specified.");
		}
		this.sparqlUpdateEndpoint = endpoint;
	}

	public String getNamedGraph() {
		return namedGraph;
	}

	/**
	 * Sets a named graph for data access.
	 * 
	 * @param namedGraph Name of the graph or null, if default graph has to be used
	 */
	public void setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
	}
}