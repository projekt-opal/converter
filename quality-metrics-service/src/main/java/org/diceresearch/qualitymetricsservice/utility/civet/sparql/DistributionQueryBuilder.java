package org.diceresearch.qualitymetricsservice.utility.civet.sparql;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.access.ResultsContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;
import org.diceresearch.qualitymetricsservice.utility.civet.vocabulary.Dcat;
import org.diceresearch.qualitymetricsservice.utility.civet.vocabulary.DublinCore;

public class DistributionQueryBuilder {

	protected static final String VAR_DATASET = DatasetQueryBuilder.VAR_DATASET;
	public static final String VAR_DISTRIBUTION = "DISTRIBUTION";
	protected static final Logger LOGGER = LogManager.getLogger();

	private String namedGraph = null;

	public Query getQuery(DataContainer dataContainer, ResultsContainer resultsContainer) {

		// Build query
		SelectBuilder selectBuilder = new SelectBuilder();
		selectBuilder.setDistinct(true);
		selectBuilder.addVar(VAR_DATASET);

		// Use named graph or default graph
		if (namedGraph != null) {
			selectBuilder.from(namedGraph);
		}

		selectBuilder.addWhere("?" + VAR_DATASET, NodeFactory.createURI(Dcat.PROPERTY_DISTRIBUTION),
				"?" + VAR_DISTRIBUTION);
		for (DataObject<?> dataObject : dataContainer.getDataObjects()) {

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.ACCESS_URL,
					Dcat.PROPERTY_ACCESS_URL))
				continue;

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.DOWNLOAD_URL,
					Dcat.PROPERTY_DOWNLOAD_URL))
				continue;

			if (addDistributionRelation(selectBuilder, dataObject.getId(), DataObjects.LICENSE,
					DublinCore.PROPERTY_LICENSE))
				continue;
		}

		for (String datasetUri : resultsContainer.dataContainers.keySet()) {
			selectBuilder.addValueVar(Var.alloc(VAR_DATASET), "<" + datasetUri + ">");
		}

		return selectBuilder.build();
	}

	public DistributionQueryBuilder setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
		return this;
	}

	private boolean addDistributionRelation(SelectBuilder selectBuilder, String dataObjectIdActual,
			String dataObjectIdExpected, String predicate) {
		if (dataObjectIdActual.equals(dataObjectIdExpected)) {
			selectBuilder.addVar(dataObjectIdExpected).addOptional("?" + VAR_DISTRIBUTION,
					NodeFactory.createURI(predicate), NodeFactory.createVariable(dataObjectIdExpected));
			return true;
		} else {
			return false;
		}
	}
}