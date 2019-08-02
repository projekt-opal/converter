package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.Arrays;
import java.util.Collection;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.data.IntegerDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;
import org.diceresearch.common.vocabulary.Opal;

/**
 * Single metric.
 *
 * @author Adrian Wilke
 */
public class CategorizationMetric extends Metric {

	private static final String DESCRIPTION = "Calculates a score based on the number of categories.";
	private static final String ID = CategorizationMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.NUMBER_OF_CATEGORIES,
			DataObjects.THEME);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_CATEGORIZATION.toString();

	public CategorizationMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {
		IntegerDataObject dataObject = dataContainer.getIntegerDataObject(DataObjects.NUMBER_OF_CATEGORIES);

		int numberOfCategories = 0;
		if (dataObject.isEmpty()) {
			return 0f;
		} else {
			numberOfCategories = dataObject.getValues().get(0);
		}

		if (numberOfCategories <= 0) {
			// No categories used
			return 0f;
		} else if (numberOfCategories <= 1) {
			// Categories used
			return 4f;
		} else {
			// More than 1 category: Indicator for extensive use
			return 5f;
		}
	}

}