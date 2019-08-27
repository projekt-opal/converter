package org.diceresearch.qualitymetricsservice.utility.civet.access;

import org.apache.jena.query.ResultSetFormatter;
import org.diceresearch.qualitymetricsservice.utility.civet.access.SparqlEndpointAccessor;

/**
 * Prints statistics of SPARQL endpoint. This is used for development issues and
 * independent of other Civet components.
 * 
 * @author Adrian Wilke
 */
public class EndpointStatistics extends SparqlEndpointAccessor {

	public static final String GRAPH = " FROM <http://projekt-opal.de> ";

	public EndpointStatistics(String endpoint) {
		super(endpoint);
	}

	@Override
	public EndpointStatistics connectQueryEndpoint() {
		super.connectQueryEndpoint();
		return this;
	}

	public void print(String query) {
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();
	}

	public void print() {
		String query;

		// General

		// Print all named graphs
		query = "SELECT DISTINCT ?g WHERE { GRAPH ?g {} }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?concept" + GRAPH + "WHERE { [] a ?concept} ORDER BY ?concept";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT (COUNT(?s) AS ?triples) " + GRAPH + "WHERE { ?s ?p ?o }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?predicate " + GRAPH + "WHERE { ?s ?predicate ?o } ORDER BY ?predicate";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		// Catalog

		query = "SELECT DISTINCT ?catalog " + GRAPH
				+ "WHERE { ?catalog a <http://www.w3.org/ns/dcat#Catalog> } LIMIT 5";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		System.out.println();
		System.out.println();

		// Distribution

		query = "SELECT DISTINCT (COUNT(?distribution) as ?distributions) " + GRAPH
				+ "WHERE { ?distribution a <http://www.w3.org/ns/dcat#Distribution> }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?predicate " + GRAPH
				+ "WHERE { ?s a <http://www.w3.org/ns/dcat#Distribution> . ?s ?predicate ?o } ORDER BY ?predicate";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?distribution " + GRAPH
				+ "WHERE { ?distribution a <http://www.w3.org/ns/dcat#Distribution> } LIMIT 5";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		System.out.println();
		System.out.println();

		// Dataset

		query = "SELECT DISTINCT (COUNT(?dataset) as ?datasets) " + GRAPH
				+ "WHERE { ?dataset a <http://www.w3.org/ns/dcat#Dataset> }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?predicate " + GRAPH
				+ "WHERE { ?s a <http://www.w3.org/ns/dcat#Dataset> . ?s ?predicate ?o } ORDER BY ?predicate";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?dataset " + GRAPH
				+ "WHERE { ?dataset a <http://www.w3.org/ns/dcat#Dataset> } LIMIT 5";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		System.out.println();
		System.out.println();

		// Publisher/agent

		query = "SELECT DISTINCT ?predicate " + GRAPH
				+ "WHERE { ?s a <http://xmlns.com/foaf/0.1/Agent> . ?s ?predicate ?o } ORDER BY ?predicate";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT (COUNT(?agent) as ?agents) " + GRAPH
				+ "WHERE { ?agent a <http://xmlns.com/foaf/0.1/Agent> }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		System.out.println();
		System.out.println();

		// Combinations

		query = "SELECT DISTINCT ?dataset (COUNT(?distribution) as ?distributions) " + GRAPH
				+ "WHERE { ?dataset a <http://www.w3.org/ns/dcat#Dataset> . ?dataset <dcat:distribution> ?distribution . ?distribution a <http://www.w3.org/ns/dcat#Distribution> } "
				+ "GROUP BY ?dataset " + "ORDER BY DESC(?distributions) LIMIT 5";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		// URLs of a dataset

		String dataset = "<http://europeandataportal.projekt-opal.de/dataset/donnees-temps-reel-de-mesure-des-concentrations-de-polluants-atmospheriques-reglementes-1>";
		query = "SELECT DISTINCT ?accessURL ?downloadURL " + GRAPH + "WHERE { "
				+ "?dataset a <http://www.w3.org/ns/dcat#Dataset> . " + "?dataset <dcat:distribution> ?distribution . "
				+ dataset + " <dcat:distribution> ?distribution . "
				+ "OPTIONAL { ?distribution <http://www.w3.org/ns/dcat#accessURL> ?accessURL } . "
				+ "OPTIONAL { ?distribution <http://www.w3.org/ns/dcat#downloadURL> ?downloadURL } " + "} LIMIT 5 ";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		// Catalogs

		query = "SELECT DISTINCT (COUNT(?catalog) as ?catalogs) " + GRAPH
				+ "WHERE { ?catalog a <http://www.w3.org/ns/dcat#Catalog> }";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?catalog " + GRAPH
				+ "WHERE { ?catalog a <http://www.w3.org/ns/dcat#Catalog> } LIMIT 5";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

		query = "SELECT DISTINCT ?predicate " + GRAPH
				+ "WHERE { ?s a <http://www.w3.org/ns/dcat#Catalog> . ?s ?predicate ?o } ORDER BY ?predicate";
		System.out.println(query);
		rdfQueryConnection.queryResultSet(query, ResultSetFormatter::out);
		System.out.println();

	}

	/**
	 * Main entry point.
	 * 
	 * @param args [0] SPARQL endpoint
	 * 
	 *             [1] (optional): SPARQL query
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Please provide a SPARQL endpoint.");
			System.exit(1);
		}

		String query = null;

		// Define a query here in Java to just execute it.

		if (args.length > 1) {
			query = args[1];
		}

		EndpointStatistics statistics = new EndpointStatistics(args[0]).connectQueryEndpoint();
		if (query == null) {
			statistics.print();
		} else {
			statistics.print(query);
		}
		statistics.close();
	}
}