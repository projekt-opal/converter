package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
public class MultipleSerializationsMetric extends Metric {

	private static final String DESCRIPTION = "Checks, if links to different file formats are provided..";
	private static final String ID = MultipleSerializationsMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.ACCESS_URL,
			DataObjects.DOWNLOAD_URL);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_MULTIPLE_SERIALIZATIONS.toString();

	public MultipleSerializationsMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		Set<String> extensions = new HashSet<>();
		Set<String> urls = new HashSet<>();

		// Collect unique URLs
		// Collect unique extensions
		for (String value : getValues(dataContainer, DataObjects.ACCESS_URL)) {
			urls.add(value);
			addExtension(value, extensions);
		}
		for (String value : getValues(dataContainer, DataObjects.DOWNLOAD_URL)) {
			urls.add(value);
			addExtension(value, extensions);
		}

		if (extensions.size() > 2) {
			// 3 or more formats
			return 5f;

		} else if (extensions.size() > 1) {
			// 2 or more formats
			return 4f;

		} else if (urls.size() > 1) {
			// More than 1 URL, maybe there are several formats
			return 1f;

		} else {

			// Only one URL
			return 0f;
		}

	}

	private void addExtension(String value, Set<String> set) {
		int dotIndex = value.lastIndexOf('.');
		// Has to contain dot
		if (dotIndex != -1) {
			// Allow 4 characters for extension
			if (value.length() - dotIndex < (4 + 2)) {
				set.add(value.substring(dotIndex + 1));
			}
		}
	}

}