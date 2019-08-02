package org.diceresearch.qualitymetricsservice.utility.civet.sparql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.access.ResultsContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.ParsingException;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DistributionQueryBuilder;

public class DatasetResultExtractor {

	protected static final Logger LOGGER = LogManager.getLogger();
	protected static final String VAR_DATASET = DatasetQueryBuilder.VAR_DATASET;
	protected static final String VAR_DISTRIBUTION = DistributionQueryBuilder.VAR_DISTRIBUTION;

	public ResultsContainer extractResults(ResultSet resultSet, DataContainer dataContainer) {

		Map<String, DataContainer> dataContainers = new HashMap<>();

		// Process results (datasets may be splitted into multiple results)
		int resultIndex = -1;
		int refreshIndex = 0;
		String datasetUri = null;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			resultIndex++;

			// Extract IDs and values of the current result
			// Iterator only returns variables with values (optional properties are skipped)
			Map<String, String> idToValue = new HashMap<>();
			Iterator<String> iterator = querySolution.varNames();
			while (iterator.hasNext()) {
				String id = iterator.next();
				idToValue.put(id, querySolution.get(id).toString().trim());
			}

			// Create or get data-container
			DataContainer resultsDataContainer;
			if (!idToValue.containsKey(VAR_DATASET)) {
				// Every result has to provide the URI of the current dataset
				LOGGER.error("Could not find dataset URI at result index: " + resultIndex);
				continue;
			} else {

				// Check change of URI
				if (!idToValue.get(VAR_DATASET).equals(datasetUri)) {
					// Remember the index of the last dataset URI in results.
					// Used to re-request that dataset in next iteration and to ensure completeness
					// of requested data. This could be solved with other types of SPARQL queries.
					// However, the current request type was fastest in tests.
					refreshIndex = resultIndex;
				}

				datasetUri = idToValue.get(VAR_DATASET);
				if (dataContainers.containsKey(datasetUri)) {
					resultsDataContainer = dataContainers.get(datasetUri);
				} else {
					try {
						resultsDataContainer = DataContainer.create(dataContainer);
					} catch (IOException e) {
						LOGGER.error("Could not create new data container.", e);
						continue;
					}
				}
			}

			// Update data-objects in data-container
			for (String id : idToValue.keySet()) {
				if (id.equals(VAR_DATASET)) {
					continue;
				} else {
					try {
						resultsDataContainer.getDataObject(id).addValueUnique(idToValue.get(id));
					} catch (ParsingException e) {
						LOGGER.error(e);
					}
				}
			}

			dataContainers.put(datasetUri, resultsDataContainer);
		}

		// Update number of categories
		for (DataContainer dc : dataContainers.values()) {
			if (dc.getIds().contains(DataObjects.NUMBER_OF_CATEGORIES)) {
				try {
					dc.getDataObject(DataObjects.NUMBER_OF_CATEGORIES)
							.setValue("" + dc.getDataObject(DataObjects.THEME).getValues().size());
				} catch (ParsingException e) {
					LOGGER.error(e);
				}
			}
		}

		// Check completeness
		if (refreshIndex == 0) {
			// The complete results contain data for exactly one resource. Therefore,
			// requests with the given limit are not able to fetch this dataset completely.
			LOGGER.warn("Completeness of SPARQL request is maybe not given.");
		}

		// Put results into container
		ResultsContainer resultsContainer = new ResultsContainer();
		resultsContainer.dataContainers = dataContainers;
		resultsContainer.refreshIndex = refreshIndex;

		return resultsContainer;
	}

}