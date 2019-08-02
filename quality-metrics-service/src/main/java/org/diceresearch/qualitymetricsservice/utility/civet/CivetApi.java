package org.diceresearch.qualitymetricsservice.utility.civet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.jena.rdf.model.Model;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.CivetException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metrics;

/**
 * Java API of Civet - Quality framework of the Open Data Portal Germany (OPAL).
 * 
 * @author Adrian Wilke
 */
public class CivetApi {

	private Orchestration orchestration = new Orchestration();
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * Sets the endpoint for SPARQL queries.
	 * 
	 * @param endpoint an URL, e.g. http://example.com:8890/sparql
	 * 
	 * @throws CivetException if endpoint is null
	 */
	public CivetApi setSparqlQueryEndpoint(String endpoint) throws CivetException {
		if (endpoint == null) {
			throw new CivetException("No SPARQL query endpoint specified.");
		}
		this.orchestration.getConfiguration().setSparqlQueryEndpoint(endpoint);
		return this;
	}

	/**
	 * Sets the endpoint for SPARQL updates.
	 * 
	 * @param endpoint an URL, e.g. http://example.com:8890/update
	 * 
	 * @throws CivetException if endpoint is null
	 */
	public CivetApi setSparqlUpdateEndpoint(String endpoint) throws CivetException {
		if (endpoint == null) {
			throw new CivetException("No SPARQL update endpoint specified.");
		}
		this.orchestration.getConfiguration().setSparqlUpdateEndpoint(endpoint);
		return this;
	}

	/**
	 * Sets a named graph for data access.
	 * 
	 * @param namedGraph Name of the graph or null, if default graph has to be used
	 */
	public CivetApi setNamedGraph(String namedGraph) {
		this.orchestration.getConfiguration().setNamedGraph(namedGraph);
		return this;
	}

	/**
	 * Gets IDs of all quality metrics.
	 */
	public Collection<String> getAllMetricIds() {
		return Metrics.getMetrics().keySet();
	}

// TODO: Outdated	
//	/**
//	 * Computes metrics for a dataset.
//	 * 
//	 * Available metric IDs can be accessed by {@link #getAllMetricIds()}.
//	 * 
//	 * @param dataset the URI of the dataset
//	 * @param metrics the metric IDs to compute
//	 * @return metric IDs mapped to the resulting scores
//	 * 
//	 * @throws IllegalArgumentException if the metrics parameter is empty
//	 * @throws NullPointerException     if one of the parameters is null or the
//	 *                                  SPARQL query endpoint was not set
//	 */
//	public Map<String, Float> compute(URI dataset, Collection<String> metrics)
//			throws NullPointerException, IllegalArgumentException {
//		if (this.orchestration.getConfiguration().getSparqlQueryEndpoint() == null) {
//			throw new NullPointerException("No SPARQL query endpoint specified.");
//		} else if (dataset == null) {
//			throw new NullPointerException("No dataset URI specified.");
//		} else if (metrics == null) {
//			throw new NullPointerException("No metrics specified.");
//		} else if (metrics.isEmpty()) {
//			throw new IllegalArgumentException("No metrics specified.");
//		}
//
//		return this.orchestration.compute(dataset, metrics);
//	}

	/**
	 * Computes all metrics.
	 * 
	 * Note: limit should be smaller than 1000, as
	 * {@link OpalAccessor#writeMetricResults(Map)} tends to throw
	 * {@link StackOverflowError}.
	 * 
	 * @param offset    Starting number (number of results, not datasets)
	 * @param endOffset Ending number (number of results, not datasets). Use -1 to
	 *                  process all results.
	 * @param limit     Number of items per request
	 * 
	 * @throws CivetException if any exception occurs on computation.
	 */
	public void computeAll(int offset, int endOffset, int limit) throws CivetException {

		// Check
		if (this.orchestration.getConfiguration().getSparqlQueryEndpoint() == null) {
			throw new CivetException("No SPARQL query endpoint specified.");
		} else if (this.orchestration.getConfiguration().getSparqlUpdateEndpoint() == null) {
			throw new CivetException("No SPARQL update endpoint specified.");
		}

		// Run
		try {
			this.orchestration.compute(offset, endOffset, limit, Metrics.getMetrics().keySet());
		} catch (Exception e) {
			throw new CivetException(e);
		}
	}

	/**
	 * Computes specified metrics.
	 * 
	 * Note: limit should be smaller than 1000, as
	 * {@link OpalAccessor#writeMetricResults(Map)} tends to throw
	 * {@link StackOverflowError}.
	 * 
	 * @param offset    Starting number (number of results, not datasets)
	 * @param endOffset Ending number (number of results, not datasets). Use -1 to
	 *                  process all results.
	 * @param limit     Number of items per request
	 * @param metrics   Set of metric-Ids to compute
	 * 
	 * @throws CivetException if any exception occurs on computation.
	 */
	public void compute(int offset, int endOffset, int limit, Set<String> metrics) throws CivetException {

		// Check
		if (this.orchestration.getConfiguration().getSparqlQueryEndpoint() == null) {
			throw new CivetException("No SPARQL query endpoint specified.");
		} else if (this.orchestration.getConfiguration().getSparqlUpdateEndpoint() == null) {
			throw new CivetException("No SPARQL update endpoint specified.");
		} else if (metrics == null) {
			throw new CivetException("No metric-IDs specified.");
		}

		// Run
		try {
			this.orchestration.compute(offset, endOffset, limit, metrics);
		} catch (Exception e) {
			throw new CivetException(e);
		}
	}

	/**
	 * Computes metric result values for a dcat:Dataset in the given Model.
	 * 
	 * @param model A model containing a dcat:Dataset and related concepts like
	 *              dcat:Distribution.
	 * 
	 * @return A new model with added result values.
	 * 
	 * @throws CivetException if any exception occurs on computation.
	 */
	public Model compute(Model model) throws CivetException {
		try {
			return orchestration.compute(model);
		} catch (Exception e) {
			throw new CivetException(e);
		}
	}

	/**
	 * Computes metric result values for a dcat:Dataset in the given Model.
	 * 
	 * @param model A model containing a dcat:Dataset and related concepts like
	 *              dcat:Distribution.
	 * 
	 * @return A new model with added result values.
	 * 
	 * @throws CivetException if any exception occurs on computation.
	 */

	public Future<Model> computeFuture(Model model) throws CivetException {
		try {
			return executor.submit(() -> {
				return orchestration.compute(model);
			});
		} catch (Exception e) {
			throw new CivetException(e);
		}
	}
}