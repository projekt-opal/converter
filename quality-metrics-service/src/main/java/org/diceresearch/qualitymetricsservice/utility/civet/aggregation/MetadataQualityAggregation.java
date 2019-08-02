package org.diceresearch.qualitymetricsservice.utility.civet.aggregation;

import java.util.Collection;
import java.util.LinkedList;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.IntegerDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.UnknownIdRuntimeException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metrics;
import org.diceresearch.common.vocabulary.Opal;

/**
 * Aggregation of all metrics. The final score.
 *
 * @author Adrian Wilke
 */
public class MetadataQualityAggregation extends Metric {

	private static final String DESCRIPTION = "Calculates the average of all available metric results.";
	private static final String ID = MetadataQualityAggregation.class.getSimpleName();
	private static final MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = new LinkedList<String>();
	private static final String RESULTS_URI = Opal.OPAL_METRIC_METADATA_QUALITY.toString();

	static {
		// Adding all 5-star metrics to required properties
		for (Metric metric : Metrics.getMetrics().values()) {
			if (metric.getType().equals(MetricType.FIVE_STAR)) {
				REQUIRED_PROPERTIES.add(metric.getId());
			} else {
				LOGGER.info("Not adding " + metric.getId());
			}
		}
	}

	public MetadataQualityAggregation() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		// Get 5-Star metric values
		int sum = 0, number = 0;
		for (Metric metric : Metrics.getMetrics().values()) {
			if (metric.getType().equals(MetricType.FIVE_STAR)) {
				IntegerDataObject dataObject = null;
				try {
					dataObject = dataContainer.getIntegerDataObject(metric.getId());
				} catch (UnknownIdRuntimeException e) {
					continue;
				}

				if (!dataObject.isEmpty()) {
					sum += dataObject.getValues().get(0);
					number++;
				}
			}
		}

		// Return average
		if (number == 0) {
			return 0;
		} else {
			return Math.round(1.0 * sum / number);
		}
	}
}