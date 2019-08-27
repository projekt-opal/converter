package org.diceresearch.qualitymetricsservice.utility.civet;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.access.OpalAccessor;
import org.diceresearch.qualitymetricsservice.utility.civet.access.ResultsContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.SparqlEndpointRuntimeException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metrics;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetResultExtractor;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DistributionQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DistributionResultExtractor;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.InsertBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.PublisherQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.PublisherResultExtractor;

/**
 * Civet management
 * 
 * @author Adrian Wilke
 */
public class Orchestration {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected org.diceresearch.qualitymetricsservice.utility.civet.Configuration configuration = new org.diceresearch.qualitymetricsservice.utility.civet.Configuration();
	protected OpalAccessor opalAccessor;

	/**
	 * Computes metric result values for a dcat:Dataset in the given Model.
	 * 
	 * @param model A model containing a dcat:Dataset and related concepts like
	 *              dcat:Distribution.
	 * 
	 * @return A new model with added result values.
	 */
	public Model compute(Model model) {

		// Duplicate model object
		model = ModelFactory.createDefaultModel().add(model);

		// Get required data object-IDs for all metrics.
		// Create data-container for object-IDs.
		Set<String> metricIds = Metrics.getMetrics().keySet();
		Set<String> dataObjectIds = getDataobjectIds(metricIds);
		DataContainer dataContainer = createDataContainer(dataObjectIds);

		// Build query
		DatasetQueryBuilder datasetQueryBuilder = new DatasetQueryBuilder();
		datasetQueryBuilder.setAddInitialDataset(true);
		Query query = datasetQueryBuilder.getQuery(dataContainer);

		// Execute
		LOGGER.debug(query.toString());
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExecution.execSelect();

		// Extract
		DatasetResultExtractor resultExtractor = new DatasetResultExtractor();
		ResultsContainer resultsContainer = resultExtractor.extractResults(resultSet, dataContainer);

		// Finish
		queryExecution.close();

		// Add distribution data
		query = new DistributionQueryBuilder().getQuery(dataContainer, resultsContainer);
		LOGGER.debug(query.toString());
		queryExecution = QueryExecutionFactory.create(query, model);
		resultSet = queryExecution.execSelect();
		new DistributionResultExtractor().extractResults(resultSet, resultsContainer);

		// Add publisher data
		query = new PublisherQueryBuilder().getQuery(dataContainer, resultsContainer);
		LOGGER.debug(query.toString());
		queryExecution = QueryExecutionFactory.create(query, model);
		resultSet = queryExecution.execSelect();
		new PublisherResultExtractor().extractResults(resultSet, resultsContainer);

		// Calculate metrics
		for (DataContainer container : resultsContainer.dataContainers.values()) {
			container.calculateMetrics(metricIds);
		}

		// Add metric results to model
		String insert = new InsertBuilder().getSparqlInsert(resultsContainer.dataContainers);
		UpdateAction.parseExecute(insert, model);

		return model;
	}

	/**
	 * Computes metrics for a dataset.
	 * 
	 * @param dataset   the URI of the dataset
	 * @param metricIds a collection of metrics to compute
	 * @return
	 */
	public Map<String, Float> compute(URI dataset, Collection<String> metricIds) {

		// Get required data object IDs
		Set<String> dataObjectIds = getDataobjectIds(metricIds);

		// Get data
		if (opalAccessor == null) {
			opalAccessor = new OpalAccessor(this).connectQueryEndpoint();
		}
		DataContainer dataContainer = createDataContainer(dataObjectIds);
		try {
			opalAccessor.getData(dataset, dataContainer);
		} catch (SparqlEndpointRuntimeException e) {
			LOGGER.error("Not connected to SPARQL endpoint.", e);
		}

		// Calculate
		dataContainer.calculateMetrics(metricIds);

		// Return
		Map<String, Float> metricResults = new HashMap<>();
		for (Entry<Metric, Float> metricResult : dataContainer.getMetricResults().entrySet()) {
			metricResults.put(metricResult.getKey().getId(), metricResult.getValue());
		}
		return metricResults;
	}

	/**
	 * Computes metrics for multiple datasets. Writes results back to graph.
	 * 
	 * Note: limit should be smaller than 1000, as
	 * {@link OpalAccessor#writeMetricResults(Map)} tends to throw
	 * {@link StackOverflowError}.
	 * 
	 * @param offset    Starting number (number of results, not datasets)
	 * @param endOffset Ending number (number of results, not datasets). Use -1 to
	 *                  process all results.
	 * @param limit     Number of items per request
	 * @param metricIds a collection of metrics to compute
	 * 
	 * @return Offset for continuation
	 */
	public int compute(int offset, int endOffset, int limit, Collection<String> metricIds) {

		// Get required data object IDs
		Set<String> dataObjectIds = getDataobjectIds(metricIds);

		// Prepare request
		if (opalAccessor == null) {
			opalAccessor = new OpalAccessor(this).connectQueryEndpoint();
		}
		DataContainer dataContainer = createDataContainer(dataObjectIds);

		// Process as long as end is not reached (or no end defined) AND
		// results have received in last iteration
		int numberOfResults = -1;
		int loopOffset = offset;
		while ((loopOffset < endOffset || endOffset == -1) && numberOfResults != 0) {

			// Get data
			ResultsContainer resultsContainer = opalAccessor.getData(dataContainer, limit, loopOffset);

			// Calculate metrics
			for (DataContainer container : resultsContainer.dataContainers.values()) {
				container.calculateMetrics(metricIds);
			}

			// Write back
			opalAccessor.writeMetricResults(resultsContainer.dataContainers);

			// Prepare next iteration
			int repeat = limit - resultsContainer.refreshIndex;
			int newLoopOffset = loopOffset + limit - repeat;
			numberOfResults = resultsContainer.dataContainers.size();

			// Prevent repeated queries, if limit would produce requesting the same results
			if (newLoopOffset == loopOffset) {
				loopOffset = newLoopOffset + limit;
				LOGGER.error("Preventing infinitive loop at offset " + newLoopOffset);
			} else {
				loopOffset = newLoopOffset;
			}
		}

		return loopOffset;
	}

	/**
	 * Closes OPAL accessor endpoint connection.
	 */
	public void close() {
		opalAccessor.close();
	}

	/**
	 * Creates new data container with data objects.
	 */
	protected DataContainer createDataContainer(Collection<String> dataObjectIds) {
		DataContainer dataContainer = new DataContainer();
		for (String id : dataObjectIds) {
			dataContainer.putDataObject(DataObjects.createDataObject(id));
		}
		return dataContainer;
	}

	public org.diceresearch.qualitymetricsservice.utility.civet.Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Gets required data-object IDs for given metrics.
	 */
	private Set<String> getDataobjectIds(Collection<String> metricIds) {
		Set<String> dataObjectIds = new HashSet<String>();
		Map<String, Metric> availableMetrics = Metrics.getMetrics();
		for (String metricId : metricIds) {
			dataObjectIds.addAll(availableMetrics.get(metricId).getRequiredProperties());
		}
		return dataObjectIds;
	}
}