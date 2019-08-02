package org.diceresearch.qualitymetricsservice.utility.civet.metrics;

/**
 * Return values of metrics can represent a
 * 
 * {@link MetricType#COUNTER} (1, 2, 3, ...) or a
 * 
 * {@link MetricType#FIVE_STAR} (0, 1, 2, 3, 4, 5) or a
 * 
 * {@link MetricType#SCALE} [0, 1].
 *
 * @author Adrian Wilke
 */
public enum MetricType {

	/**
	 * Float return value (1, 2, 3, ...) represents counter.
	 */
	COUNTER,

	/**
	 * Float return value (0, 1, 2, 3, 4, 5) represents stars.
	 */
	FIVE_STAR,

	/**
	 * Float return value [0, 1] represents scale.
	 */
	SCALE
}