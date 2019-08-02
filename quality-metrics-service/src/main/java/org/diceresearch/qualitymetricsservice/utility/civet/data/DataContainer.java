package org.diceresearch.qualitymetricsservice.utility.civet.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.diceresearch.qualitymetricsservice.utility.civet.data.AbstractDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.data.IntegerDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.StringDataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.UnknownIdRuntimeException;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metric;
import org.diceresearch.qualitymetricsservice.utility.civet.metrics.Metrics;

/**
 * Data container for {@link org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject}s.
 * 
 * Data-object IDs and types are defined in {@link org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects}.
 *
 * @author Adrian Wilke
 */
public class DataContainer {

	// Data-object ID to object
	private Map<String, org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?>> dataObjects = new HashMap<String, org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?>>();

	// Metrixc-ID to metric-result
	private Map<Metric, Float> metricResults = new HashMap<Metric, Float>();

	/**
	 * Creates new data-container with data-objects of source data-container.
	 * Data-objects will have same types and IDs, but no values.
	 * 
	 * @param dataContainer Source data-container
	 * 
	 * @throws IOException          If type of source data-object is unknown.
	 * @throws NullPointerException if the one of given IDs is null.
	 */
	public static DataContainer create(DataContainer dataContainer) throws NullPointerException, IOException {
		DataContainer newDataContainer = new DataContainer();
		for (org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?> dataObject : dataContainer.getDataObjects()) {
			newDataContainer.putDataObject(AbstractDataObject.create(dataObject));
		}
		return newDataContainer;
	}

	/**
	 * Gets data object.
	 * 
	 * @param id as specified in class constants of {@link DataObjects}
	 * 
	 * @throws NullPointerException      if the given ID is null.
	 * @throws UnknownIdRuntimeException if the given ID has not been defined.
	 */
	public org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?> getDataObject(String id) throws NullPointerException, UnknownIdRuntimeException {
		if (id == null) {
			throw new NullPointerException("Data object ID is null");
		} else if (dataObjects.containsKey(id)) {
			return dataObjects.get(id);
		} else {
			throw new UnknownIdRuntimeException("Unknown data object ID: " + id);
		}
	}

	/**
	 * Adds all values, if not already contained.
	 * 
	 * @throws NullPointerException if a data-object is null.
	 * @throws ClassCastException   if type of a data object does not fit.
	 */
	public DataContainer addAllUnique(DataContainer dataContainer) {
		// Process all data-objects
		for (String dataObjectId : dataContainer.getIds()) {
			if (this.getIds().contains(dataObjectId)) {
				// ID exists: Add single values, if not already contained
				this.getDataObject(dataObjectId).addValuesUnique(dataContainer.getDataObject(dataObjectId));
			} else {
				// ID does not exist now: Just add
				putDataObject(dataContainer.getDataObject(dataObjectId));
			}
		}
		return this;
	}

	/**
	 * Gets casted data object.
	 * 
	 * @throws NullPointerException      if the given ID is null.
	 * @throws UnknownIdRuntimeException if the given ID has not been defined.
	 * @throws ClassCastException        if type of data object does not fit.
	 */
	public IntegerDataObject getIntegerDataObject(String id)
			throws NullPointerException, UnknownIdRuntimeException, ClassCastException {
		return IntegerDataObject.cast(getDataObject(id));
	}

	/**
	 * Gets casted data object.
	 * 
	 * @throws NullPointerException      if the given ID is null.
	 * @throws UnknownIdRuntimeException if the given ID has not been defined.
	 * @throws ClassCastException        if type of data object does not fit.
	 */
	public StringDataObject getStringDataObject(String id)
			throws NullPointerException, UnknownIdRuntimeException, ClassCastException {
		return StringDataObject.cast(getDataObject(id));
	}

	/**
	 * Puts data object in container.
	 * 
	 * @param dataObject implementation of {@link AbstractDataObject}
	 * 
	 * @throws NullPointerException if the given data object is null.
	 */
	public DataContainer putDataObject(org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?> dataObject) throws NullPointerException {
		if (dataObject == null) {
			throw new NullPointerException("Data object is null");
		} else {
			dataObjects.put(dataObject.getId(), dataObject);
			return this;
		}
	}

	/**
	 * Calculates metric results and stores them in this object.
	 */
	public DataContainer calculateMetrics(Collection<String> metricIds) {
		Map<String, Metric> availableMetrics = Metrics.getMetrics();
		for (String metricId : metricIds) {
			metricResults.put(availableMetrics.get(metricId), availableMetrics.get(metricId).getScore(this));
		}
		return this;
	}

	/**
	 * Gets metric results calculated by {@link #calculateMetrics(Collection)}.
	 */
	public Map<Metric, Float> getMetricResults() {
		return metricResults;
	}

	/**
	 * Gets contained data object IDs.
	 */
	public Set<String> getIds() {
		return dataObjects.keySet();
	}

	/**
	 * Gets contained data objects.
	 */
	public Collection<org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject<?>> getDataObjects() {
		return dataObjects.values();
	}

	/**
	 * Gets all contained data as string representation.
	 */
	public String toExtendedString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, DataObject<?>> dataObject : dataObjects.entrySet()) {
			stringBuilder.append(dataObject.getKey());
			stringBuilder.append(System.lineSeparator());
			for (Object value : dataObject.getValue().getValues()) {
				stringBuilder.append(" ");
				stringBuilder.append(value.toString());
				stringBuilder.append(System.lineSeparator());
			}
		}
		for (Entry<Metric, Float> metricResult : metricResults.entrySet()) {
			stringBuilder.append(metricResult.getValue());
			stringBuilder.append(" ");
			stringBuilder.append(metricResult.getKey().getId());
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}
}