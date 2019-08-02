package org.diceresearch.qualitymetricsservice.utility.civet.data;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.IntegerDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.StringDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.UnknownIdRuntimeException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.MetricType;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metrics;

/**
 * Defines IDs and types of {@link org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject}s.
 * 
 * Data objects represent data types which are required to calculate metrics.
 * Accessors must supply these based on available data.
 * 
 * Data object IDs are defined via {@link DataObjects} class constants.
 * 
 * Data object types are defined in static initializer of {@link DataObjects}.
 * 
 * Data objects are used in {@link DataContainer}.
 *
 * @author Adrian Wilke
 */
public abstract class DataObjects {

	protected static final Logger LOGGER = LogManager.getLogger();

	// Definition of data object IDs
	public final static String NUMBER_OF_CATEGORIES = "NUMBER_OF_CATEGORIES";

	// Dataset properties
	public final static String CATALOG = "CATALOG";
	public final static String DESCRIPTION = "DESCRIPTION";
	public final static String ISSUED = "ISSUED";
	public final static String PUBLISHER = "PUBLISHER";
	public final static String THEME = "THEME";
	public final static String TITLE = "TITLE";

	// Distribution properties
	public final static String ACCESS_URL = "ACCESS_URL";
	public final static String DOWNLOAD_URL = "DOWNLOAD_URL";
	public final static String LICENSE = "LICENSE";

	// Publisher properties
	public final static String PUBLISHER_NAME = "PUBLISHER_NAME";
	public final static String PUBLISHER_MBOX = "PUBLISHER_MBOX";
	public final static String PUBLISHER_HOMEPAGE = "PUBLISHER_HOMEPAGE";

	private static List<String> integerDataObjects = new LinkedList<String>();
	private static List<String> stringDataObjects = new LinkedList<String>();

	/**
	 * Definition of data object types.
	 */
	static {
		integerDataObjects.add(NUMBER_OF_CATEGORIES);

		stringDataObjects.add(CATALOG);
		stringDataObjects.add(DESCRIPTION);
		stringDataObjects.add(ISSUED);
		stringDataObjects.add(PUBLISHER);
		stringDataObjects.add(THEME);
		stringDataObjects.add(TITLE);

		stringDataObjects.add(ACCESS_URL);
		stringDataObjects.add(DOWNLOAD_URL);
		stringDataObjects.add(LICENSE);

		stringDataObjects.add(PUBLISHER_NAME);
		stringDataObjects.add(PUBLISHER_MBOX);
		stringDataObjects.add(PUBLISHER_HOMEPAGE);

		// 5-Star metric IDs for aggregation
		for (Metric metric : Metrics.getMetrics().values()) {
			if (metric.getType().equals(MetricType.FIVE_STAR)) {
				integerDataObjects.add(metric.getId());
			} else {
				LOGGER.info("Not adding " + metric.getId());
			}
		}
	}

	/**
	 * Creates data object for given ID.
	 * 
	 * @throws UnknownIdRuntimeException if the given ID has not been defined.
	 * @throws NullPointerException      if the given ID is null.
	 */
	public static DataObject<?> createDataObject(String id) throws UnknownIdRuntimeException {
		if (id == null) {
			throw new NullPointerException("Data object ID is null");
		} else if (integerDataObjects.contains(id)) {
			return new IntegerDataObject(id);
		} else if (stringDataObjects.contains(id)) {
			return new StringDataObject(id);
		} else {
			throw new UnknownIdRuntimeException("Unknown data object ID: " + id);
		}
	}

	/**
	 * Gets list of all defined data object IDs.
	 */
	public static List<String> getDataObjectIds() {
		List<String> dataObjectIds = new LinkedList<String>();
		dataObjectIds.addAll(integerDataObjects);
		dataObjectIds.addAll(stringDataObjects);
		return dataObjectIds;
	}

}