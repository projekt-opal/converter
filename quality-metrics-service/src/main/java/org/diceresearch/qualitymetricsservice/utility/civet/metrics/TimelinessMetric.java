package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

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
public class TimelinessMetric extends Metric {

	private static final String DESCRIPTION = "Calculate the difference of the current time and the time of creation or the last update.";
	private static final String ID = TimelinessMetric.class.getSimpleName();
	private static final org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType METRIC_TYPE = MetricType.FIVE_STAR;
	private static final Collection<String> REQUIRED_PROPERTIES = Arrays.asList(DataObjects.ISSUED,
			DataObjects.CATALOG);
	private static final String RESULTS_URI = Opal.OPAL_METRIC_TIMELINESS.toString();

	private static final int MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;
	private static final SimpleDateFormat dateFormatGerman = new SimpleDateFormat("dd.MM.yyyy");
	private static final SimpleDateFormat dateFormatInternational = new SimpleDateFormat("yyyy-MM-dd");

	public TimelinessMetric() {
		this.description = DESCRIPTION;
		this.id = ID;
		this.metricType = METRIC_TYPE;
		this.requiredProperties = REQUIRED_PROPERTIES;
		this.resultsUri = RESULTS_URI;
	}

	@Override
	public float getScore(DataContainer dataContainer) {

		// Get catalog for parsing information
		StringDataObject dataObject = dataContainer.getStringDataObject(DataObjects.CATALOG);
		SimpleDateFormat simpleDateFormat;
		String catalog;
		if (dataObject == null) {
			LOGGER.error("No catalog provided.");
			return 0;
		} else {
			catalog = dataObject.getValues().get(0);
		}

		// Get date format parser
		if (catalog.equals(Opal.OPAL_CATALOG_EUROPEANDATAPORTAL.toString())) {
			// TODO: Not available in current OPAL graph.
			return 0;
		} else if (catalog.equals(Opal.OPAL_CATALOG_GOVDATA.toString())) {
			simpleDateFormat = dateFormatInternational;
		} else if (catalog.equals(Opal.OPAL_CATALOG_MCLOUD.toString())) {
			simpleDateFormat = dateFormatGerman;
		} else {
			LOGGER.error("Catalog unknown.");
			return 0;
		}

		// Get date values
		dataObject = dataContainer.getStringDataObject(DataObjects.ISSUED);

		// Get newest date
		Date newestDate = new Date(0);
		for (String value : getValues(dataContainer, DataObjects.ISSUED)) {

			// Cut long date format
			if (catalog.equals(Opal.OPAL_CATALOG_GOVDATA.toString()) && value.length() >= 10) {
				value = value.substring(0, 10);
			}

			try {
				Date date = simpleDateFormat.parse(value);
				if (date.after(newestDate)) {
					newestDate = date;
				}
			} catch (ParseException e) {
				LOGGER.debug("Error parsing " + value);
			}
		}

		// Compute score based on age
		long daysAge = (new Date().getTime() - newestDate.getTime()) / MILLIS_TO_DAYS;
		if (daysAge <= 7) {
			return 5f;
		} else if (daysAge <= 14) {
			return 4f;
		} else if (daysAge <= 30) {
			return 3f;
		} else if (daysAge <= 180) {
			return 2f;
		} else if (daysAge <= 365) {
			return 1f;
		} else {
			return 0f;
		}
	}
}