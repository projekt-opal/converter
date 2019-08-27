package org.diceresearch.qualitymetricsservice.utility.civet.access;

import java.util.HashMap;
import java.util.Map;

import org.diceresearch.qualitymetricsservice.utility.civet.access.OpalAccessor;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;

/**
 * Simple container for return values in {@link OpalAccessor} requests.
 * 
 * @author Adrian Wilke
 */
public class ResultsContainer {

	public Map<String, DataContainer> dataContainers = new HashMap<>();
	public int refreshIndex;

}