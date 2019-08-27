package org.diceresearch.qualitymetricsservice.utility.civet.sparql;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.diceresearch.qualitymetricsservice.utility.civet.access.ResultsContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataContainer;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObject;
import org.diceresearch.qualitymetricsservice.utility.civet.data.DataObjects;
import org.diceresearch.qualitymetricsservice.utility.civet.sparql.DatasetQueryBuilder;

public class PublisherQueryBuilder {

	protected static final String VAR_DATASET = DatasetQueryBuilder.VAR_DATASET;
	public static final String VAR_PUBLISHER = "PUBLISHER";
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

		selectBuilder.addWhere("?" + VAR_DATASET, DCTerms.publisher, "?" + VAR_PUBLISHER);
		for (DataObject<?> dataObject : dataContainer.getDataObjects()) {

			if (addRelation(selectBuilder, dataObject.getId(), DataObjects.PUBLISHER_NAME, FOAF.name))
				continue;

			if (addRelation(selectBuilder, dataObject.getId(), DataObjects.PUBLISHER_MBOX, FOAF.mbox))
				continue;

			if (addRelation(selectBuilder, dataObject.getId(), DataObjects.PUBLISHER_HOMEPAGE, FOAF.homepage))
				continue;
		}

		for (String datasetUri : resultsContainer.dataContainers.keySet()) {
			selectBuilder.addValueVar(Var.alloc(VAR_DATASET), "<" + datasetUri + ">");
		}

		return selectBuilder.build();
	}

	public PublisherQueryBuilder setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
		return this;
	}

	private boolean addRelation(SelectBuilder selectBuilder, String dataObjectIdActual, String dataObjectIdExpected,
			Property predicate) {

		if (dataObjectIdActual.equals(dataObjectIdExpected)) {
			selectBuilder.addVar(dataObjectIdExpected).addOptional("?" + VAR_PUBLISHER, predicate,
					NodeFactory.createVariable(dataObjectIdExpected));
			return true;
		} else {
			return false;
		}
	}
}