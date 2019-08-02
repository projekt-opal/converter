package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.Arrays;
import java.util.Collection;

import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.data.StringDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;
import org.diceresearch.common.vocabulary.Opal;

/**
 * Single metric.
 *
 * @author Adrian Wilke
 */
public class KnownLicenseMetric extends Metric {

	private static final String DESCRIPTION = "Checks, if a license is given.";
	private static final String ID = KnownLicenseMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.LICENSE);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_KNOWN_LICENSE.toString();

	public KnownLicenseMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		StringDataObject dataObject = dataContainer.getStringDataObject(DataObjects.LICENSE);
		boolean licenceSpecified = false;
		if (dataObject.isEmpty()) {
			return 0f;
		} else {
			for (String value : dataObject.getValues()) {
				if (value != null && !value.isEmpty()) {
					licenceSpecified = true;
					break;
				}
			}
		}

		if (licenceSpecified) {
			// At least one specified license
			return 5f;

		} else {
			// No license specified
			return 0f;
		}

	}
}