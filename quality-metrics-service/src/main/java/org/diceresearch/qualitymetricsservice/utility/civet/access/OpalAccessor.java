package org.diceresearch.qualitymetricsservice.utility.civet.access;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.update.UpdateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.Orchestration;
import org.diceresearch.qualitymetricsservice.utility.civet.access.ResultsContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.access.SparqlEndpointAccessor;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.ParsingException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetResultExtractor;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DistributionQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DistributionResultExtractor;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.InsertBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.vocabulary.Dcat;
import org.diceresearch.qualitymetricsservice.utility.civet.vocabulary.DublinCore;

/**
 * Data accessor for OPAL SPARQL endpoint.
 * 
 * RDF graph data is accessed and written into data container.
 *
 * @author Adrian Wilke
 */
public class OpalAccessor extends SparqlEndpointAccessor {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final String VAR_DATASET = "DATASET";
	protected static final String VAR_DISTRIBUTION = "DISTRIBUTION";

	protected static final String VAR_METRIC_URI = "METRIC_URI";
	protected static final String VAR_NAMED_GRAPH = "NAMED_GRAPH";

	protected static final String DELETE_PREFIX = "PREFIX dqv: <http://www.w3.org/ns/dqv#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
	protected static final String DELETE_GRAPH = "WITH <NAMED_GRAPH> ";
	protected static final String DELETE_DELETE = "DELETE { ";
	protected static final String DELETE_ENTRY_DELETE = "?m ?po ?o . ?s ?ps ?m ";
	protected static final String DELETE_WHERE = "} WHERE { ";
	protected static final String DELETE_ENTRY_WHERE = "DATASET dqv:hasQualityMeasurement ?m . "
			+ "?m dqv:isMeasurementOf METRIC_URI . " + "?m ?po ?o . ?s ?ps ?m ";

	protected Orchestration orchestration;

	public OpalAccessor(Orchestration orchestration) {
		super(orchestration.getConfiguration().getSparqlQueryEndpoint(),
				orchestration.getConfiguration().getSparqlUpdateEndpoint());
		this.orchestration = orchestration;
	}

	@Override
	public OpalAccessor connectQueryEndpoint() {
		super.connectQueryEndpoint();
		return this;
	}

	public void writeMetricResults(Map<String, DataContainer> dataContainers) {
		if (dataContainers.isEmpty()) {
			LOGGER.warn("WRITE no datasets -> aborting");
			return;
		} else {
			LOGGER.info("WRITE datasets " + dataContainers.size());
		}

		// Ensure connection
		if (!isUpdateEndpointConnected()) {
			connectUpdateEndpoint();
		}

		// Delete metric values, which will be inserted in next step
		UpdateRequest deleteRequest = new UpdateRequest();
		for (Entry<String, DataContainer> dataContainer : dataContainers.entrySet()) {
			for (Metric metric : dataContainer.getValue().getMetricResults().keySet()) {
				String sparqlDelete = getSparqlDelete(dataContainer.getKey(), metric.getResultsUri());
				LOGGER.debug(sparqlDelete);
				deleteRequest.add(sparqlDelete);
			}
		}
		rdfUpdateConnection.update(deleteRequest);

		// Insert metric values
		UpdateRequest insertRequest = new UpdateRequest();

		InsertBuilder insertBuilder = new InsertBuilder();
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			insertBuilder.setNamedGraph(orchestration.getConfiguration().getNamedGraph());
		}
		String sparqlInsert = insertBuilder.getSparqlInsert(dataContainers);

		LOGGER.debug(sparqlInsert);
		insertRequest.add(sparqlInsert);
		rdfUpdateConnection.update(insertRequest);
	}

	/**
	 * Gets data for several datasets.
	 * 
	 * @param dataContainer A data-container with pre-defined IDs
	 * @param limit         Number of items per request
	 * @param offset        Starting number
	 * @throws NullPointerException
	 */
	public ResultsContainer getData(DataContainer dataContainer, int limit, int offset) throws NullPointerException {
		LOGGER.info("GET offset " + offset);

		// Ensure connection
		if (!isQueryEndpointConnected()) {
			connectQueryEndpoint();
		}

		// Build query
		DatasetQueryBuilder datasetqueryBuilder = new DatasetQueryBuilder();
		datasetqueryBuilder.setAddInitialDataset(true).setLimit(limit).setOffset(offset);
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			datasetqueryBuilder.setNamedGraph(orchestration.getConfiguration().getNamedGraph());
		}
		Query query = datasetqueryBuilder.getQuery(dataContainer);

		// Execute query
		LOGGER.debug(query.toString());
		QueryExecution queryExecution = rdfQueryConnection.query(query);
		ResultSet resultSet = queryExecution.execSelect();
		ResultsContainer resultsContainer = new DatasetResultExtractor().extractResults(resultSet, dataContainer);
		queryExecution.close();

		// Get related distribution data
		getDistributionData(dataContainer, resultsContainer);

		// TODO: Add publisher data

		return resultsContainer;
	}

	public void getData(URI datasetUri, DataContainer dataContainer) {

		// TODO: Check dataset with multiple categories

		// Ensure connection
		if (!isQueryEndpointConnected()) {
			connectQueryEndpoint();
		}

		// Build query
		DatasetQueryBuilder datasetqueryBuilder = new DatasetQueryBuilder();
		datasetqueryBuilder.setDatasetUri(datasetUri.toString());
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			datasetqueryBuilder.setNamedGraph(orchestration.getConfiguration().getNamedGraph());
		}
		Query query = datasetqueryBuilder.getQuery(dataContainer);

		// Execute query
		LOGGER.debug(query.toString());
		QueryExecution queryExecution = rdfQueryConnection.query(query);
		ResultSet resultSet = queryExecution.execSelect();

		// Process results
		int categories = 0;
		if (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();

			// Iterator only returns variables with values (optional properties are skipped)
			Iterator<String> iterator = querySolution.varNames();
			while (iterator.hasNext()) {
				String id = iterator.next();
				try {
					if (id.equals(DataObjects.THEME)) {
						categories++;
					}
					dataContainer.getDataObject(id).addValue(querySolution.get(id).toString().trim());
				} catch (ParsingException e) {
					try {
						// Try to remove RDF type information. Assumes integer expressed as float.
						int intVal = Math.round(querySolution.get(id).asLiteral().getFloat());
						dataContainer.getDataObject(id).addValue("" + intVal);
					} catch (ParsingException e1) {
						LOGGER.error(e + " (value)");
					}
				}
			}
		}
		try {
			if (dataContainer.getIds().contains(DataObjects.NUMBER_OF_CATEGORIES))
				dataContainer.getDataObject(DataObjects.NUMBER_OF_CATEGORIES).addValue("" + categories);
		} catch (ParsingException e) {
			LOGGER.error(e + " (category)");
		}
		if (resultSet.hasNext()) {
			LOGGER.debug("More than one result returned.");
		}
		queryExecution.close();

		getDistributionData(datasetUri, dataContainer);

		// TODO: Add publisher data
	}

	private void getDistributionData(DataContainer dataContainer, ResultsContainer resultsContainer) {

		// Get query
		DistributionQueryBuilder distributionQueryBuilder = new DistributionQueryBuilder();
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			distributionQueryBuilder.setNamedGraph(orchestration.getConfiguration().getNamedGraph());
		}
		Query query = distributionQueryBuilder.getQuery(dataContainer, resultsContainer);

		// Execute
		LOGGER.debug(query.toString());
		QueryExecution queryExecution = rdfQueryConnection.query(query);
		ResultSet resultSet = queryExecution.execSelect();

		// Add results
		new DistributionResultExtractor().extractResults(resultSet, resultsContainer);
		queryExecution.close();
	}

	private void getDistributionData(URI datasetUri, DataContainer dataContainer) {

		// Build query
		SelectBuilder selectBuilder = new SelectBuilder();

		// Use named graph or default graph
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			selectBuilder.from(orchestration.getConfiguration().getNamedGraph());
		}

		selectBuilder.addWhere("?" + VAR_DATASET, NodeFactory.createURI(Dcat.PROPERTY_DISTRIBUTION),
				"?" + VAR_DISTRIBUTION);
		for (DataObject<?> dataObject : dataContainer.getDataObjects()) {

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.ACCESS_URL,
					Dcat.PROPERTY_ACCESS_URL))
				continue;

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.DOWNLOAD_URL,
					Dcat.PROPERTY_DOWNLOAD_URL))
				continue;

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.LICENSE,
					DublinCore.PROPERTY_LICENSE))
				continue;
		}
		selectBuilder.setVar(Var.alloc(VAR_DATASET), "<" + datasetUri.toString() + ">");

		// Execute query
		Query query = selectBuilder.build();
		LOGGER.debug(query.toString());
		QueryExecution queryExecution = rdfQueryConnection.query(query);
		ResultSet resultSet = queryExecution.execSelect();

		// Deduplication
		Map<String, Set<String>> data = new HashMap<>();

		// Process results
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();

			// Iterator only returns variables with values (optional properties are skipped)
			Iterator<String> iterator = querySolution.varNames();
			while (iterator.hasNext()) {
				String id = iterator.next();
				if (id.equals(VAR_DISTRIBUTION)) {
					// variable just used to access data
					continue;
				}
				if (!data.containsKey(id)) {
					data.put(id, new HashSet<String>());
				}
				data.get(id).add(querySolution.get(id).toString().trim());
			}
		}
		queryExecution.close();

		// Put data in container
		for (Entry<String, Set<String>> entry : data.entrySet()) {
			for (String value : entry.getValue()) {
				try {
					dataContainer.getDataObject(entry.getKey()).addValue(value);
				} catch (ParsingException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	private boolean addDistributionRelation(SelectBuilder selectBuilder, String dataObjectIdActual,
			String dataObjectIdExpected, String predicate) {
		if (dataObjectIdActual.equals(dataObjectIdExpected)) {
			selectBuilder.addVar(dataObjectIdExpected).addOptional("?" + VAR_DISTRIBUTION,
					NodeFactory.createURI(predicate), NodeFactory.createVariable(dataObjectIdExpected));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets SPARQL DELETE for given URIs of dataset and metric.
	 */
	private String getSparqlDelete(String datasetUri, String metricUri) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(DELETE_PREFIX);
		stringBuilder.append(System.lineSeparator());

		// Use named graph or default graph
		if (orchestration.getConfiguration().getNamedGraph() != null) {
			stringBuilder.append(new String(DELETE_GRAPH).replace(VAR_NAMED_GRAPH,
					orchestration.getConfiguration().getNamedGraph()));
		}

		stringBuilder.append(DELETE_DELETE);
		stringBuilder.append(DELETE_ENTRY_DELETE);
		stringBuilder.append(DELETE_WHERE);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(new String(DELETE_ENTRY_WHERE)

				.replace(VAR_DATASET, "<" + datasetUri + ">")

				.replace(VAR_METRIC_URI, "<" + metricUri + ">"));

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}