package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.StringDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;

/**
 * Computes metadata quality metric.
 *
 * @author Adrian Wilke
 */
public abstract class Metric {

	/**
	 * The respective description of a metric is set in the concrete implementation.
	 */
	protected String description;

	/**
	 * The respective ID of a metric is set in the concrete implementation.
	 */
	protected String id;

	/**
	 * The URI for results in triple stores.
	 */
	protected String resultsUri;

	/**
	 * The respective type of a metric is set in the concrete implementation.
	 */
	protected MetricType metricType;

	/**
	 * The respective required properties for a metric are set in the concrete
	 * implementation.
	 */
	protected Collection<String> requiredProperties;

	/**
	 * Logger for all metrics
	 */
	protected static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Returns description of the metric.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the ID of the metric
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the URI for results in triple stores.
	 */
	public String getResultsUri() {
		return this.resultsUri;
	}

	/**
	 * Returns list of required properties
	 */
	public Collection<String> getRequiredProperties() {
		return this.requiredProperties;
	}

	/**
	 * Computes and returns score of metric.
	 */
	public abstract float getScore(DataContainer dataContainer);

	/**
	 * Returns type of metric.
	 */
	public MetricType getType() {
		return this.metricType;
	}

	/**
	 * Gets simple class name of metric.
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Gets String-data-object from data-container. Removes empty values.
	 */
	protected List<String> getValues(DataContainer dataContainer, String dataObjectId) {
		List<String> values = new LinkedList<>();
		StringDataObject stringDataObject = dataContainer.getStringDataObject(dataObjectId);
		for (String value : stringDataObject.getValues()) {
			if (value != null && !value.trim().isEmpty()) {
				values.add(value);
			}
		}
		return values;
	}
}