package org.diceresearch.qualitymetricsservice.utility.civet.sparql;

import java.util.Map;
import java.util.Map.Entry;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;

public class InsertBuilder {

	protected static final String INSERT_PREFIX = "PREFIX dqv: <http://www.w3.org/ns/dqv#> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
	protected static final String INSERT_INSERT = "INSERT DATA { ";
	protected static final String INSERT_GRAPH = "GRAPH <NAMED_GRAPH> { ";
	protected static final String INSERT_ENTRY = "DATASET dqv:hasQualityMeasurement _:bBLANK_INDEX . "
			+ "_:bBLANK_INDEX a dqv:qualityMeasurement . " + "_:bBLANK_INDEX dqv:value \"RESULT_VALUE\"^^xsd:float . "
			+ "_:bBLANK_INDEX dqv:isMeasurementOf METRIC_URI . ";

	protected static final String VAR_DATASET = DatasetQueryBuilder.VAR_DATASET;

	protected static final String VAR_BLANK_INDEX = "BLANK_INDEX";
	protected static final String VAR_RESULT_VALUE = "RESULT_VALUE";
	protected static final String VAR_METRIC_URI = "METRIC_URI";
	protected static final String VAR_NAMED_GRAPH = "NAMED_GRAPH";

	private String namedGraph = null;

	/**
	 * Gets SPARQL INSERT for all given datasets and metrics.
	 * 
	 * Metrics have to be pre-calculated.
	 */
	public String getSparqlInsert(Map<String, DataContainer> dataContainers) {

		StringBuilder stringBuilder = new StringBuilder();
		int blankNodeCounter = 0;

		stringBuilder.append(INSERT_PREFIX);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(INSERT_INSERT);
		stringBuilder.append(System.lineSeparator());

		// Use named graph or default graph
		boolean additionalClose = false;
		if (namedGraph != null) {
			additionalClose = true;
			stringBuilder.append(new String(INSERT_GRAPH).replace(VAR_NAMED_GRAPH, namedGraph));
			stringBuilder.append(System.lineSeparator());
		}

		for (Entry<String, DataContainer> dataContainer : dataContainers.entrySet()) {
			for (Entry<Metric, Float> metric : dataContainer.getValue().getMetricResults().entrySet()) {
				stringBuilder.append(new String(INSERT_ENTRY)

						.replace(VAR_BLANK_INDEX, "" + blankNodeCounter++)

						.replace(VAR_DATASET, "<" + dataContainer.getKey() + ">")

						.replace(VAR_METRIC_URI, "<" + metric.getKey().getResultsUri() + ">")

						.replace(VAR_RESULT_VALUE, "" + metric.getValue()));

				stringBuilder.append(System.lineSeparator());
			}
		}

		stringBuilder.append("} ");
		if (additionalClose) {
			// Close named graph part
			stringBuilder.append("} ");
		}

		return stringBuilder.toString();
	}

	public InsertBuilder setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
		return this;
	}
}