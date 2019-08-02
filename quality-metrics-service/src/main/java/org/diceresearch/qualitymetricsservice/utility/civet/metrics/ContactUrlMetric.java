package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.Arrays;
import java.util.Collection;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;
import org.diceresearch.common.vocabulary.Opal;

/**
 * Single metric.
 *
 * @author Adrian Wilke
 */
public class ContactUrlMetric extends Metric {

	private static final String DESCRIPTION = "Checks, if a contact homepage URL is available.";
	private static final String ID = ContactUrlMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.PUBLISHER_HOMEPAGE);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_CONTACT_URL.toString();

	public ContactUrlMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		if (getValues(dataContainer, DataObjects.PUBLISHER_HOMEPAGE).isEmpty()) {
			// No contact homepage URL available
			return 0f;

		} else {
			// At least one contact homepage URL available
			return 5f;
		}

	}
}