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
 * TODO: Multiple titles can be supplied in distributions.
 *
 * @author Adrian Wilke
 */
public class DescriptionMetric extends Metric {

	private static final String DESCRIPTION = "Calculates a score based on the extend of the description.";
	private static final String ID = DescriptionMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.TITLE,
			DataObjects.DESCRIPTION);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_DESCRIPTION.toString();

	public DescriptionMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		// Get longest title
		String title = "";
		for (String value : getValues(dataContainer, DataObjects.TITLE)) {
			if (value.length() > title.length()) {
				title = value;
			}
		}

		// Single descriptions are unique.
		// Concatenate all describing information.
		StringBuilder stringBuilder = new StringBuilder();
		for (String value : getValues(dataContainer, DataObjects.DESCRIPTION)) {
			stringBuilder.append(value);
			stringBuilder.append(System.lineSeparator());
		}
		String description = stringBuilder.toString();

		if (title.isEmpty() && description.isEmpty()) {
			// No title or description set
			return 0f;

		} else if (title.isEmpty()) {
			// At least title set
			return 1f;

		} else if (description.isEmpty()) {
			// At least description set
			return 1f;

		} else if (description.equals(title)) {
			// Bad use of description text
			return 1f;

		} else if (title.length() < 15) {
			// Bad title
			return 1f;

		} else if (description.length() <= 25) {
			return 2f;

		} else if (description.length() <= 50) {
			return 3f;

		} else if (description.length() <= 75) {
			return 4f;

		} else {
			return 5f;
		}
	}
}