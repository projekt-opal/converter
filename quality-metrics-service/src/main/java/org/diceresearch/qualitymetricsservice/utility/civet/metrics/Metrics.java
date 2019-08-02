package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

import java.util.HashMap;
import java.util.Map;

import org.diceresearch.qualitymetricsservice.utility.civet.aggregation.MetadataQualityAggregation;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.*;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;

/**
 * Metrics catalog.
 *
 * @author Adrian Wilke
 */
public abstract class Metrics {

	/**
	 * Gets all metrics of the dimension contactability.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsContactability() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new ContactUrlMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics of the dimension expressiveness.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsExpressiveness() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new CategorizationMetric(), metrics);
		putMetricIntoMap(new DescriptionMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics of the dimension interlinking.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsInterlinking() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new LinkedDataMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics of the dimension rights.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsRights() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new KnownLicenseMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics of the dimension temporal.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsTemporal() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new TimelinessMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics of the dimension versatility.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsVersatility() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new MultipleSerializationsMetric(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics aggregation scores.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetricsAggregation() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		putMetricIntoMap(new MetadataQualityAggregation(), metrics);
		return metrics;
	}

	/**
	 * Gets all metrics.
	 */
	public static Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> getMetrics() {
		Map<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric> metrics = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric>();
		metrics.putAll(getMetricsContactability());
		metrics.putAll(getMetricsExpressiveness());
		metrics.putAll(getMetricsTemporal());
		metrics.putAll(getMetricsRights());
		metrics.putAll(getMetricsVersatility());
		metrics.putAll(getMetricsInterlinking());
		metrics.putAll(getMetricsAggregation());
		return metrics;
	}

	/**
	 * Puts ID of metric and the metric itself in the map.
	 */
	private static void putMetricIntoMap(org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric metric, Map<String, Metric> map) {
		map.put(metric.toString(), metric);
	}
}
