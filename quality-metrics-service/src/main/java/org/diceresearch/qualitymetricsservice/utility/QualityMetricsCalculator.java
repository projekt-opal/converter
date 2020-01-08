package org.diceresearch.qualitymetricsservice.utility;

import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.civet.Civet;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.dice_research.opal.common.vocabulary.Dqv;
import org.dice_research.opal.common.vocabulary.Opal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class QualityMetricsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(QualityMetricsCalculator.class);

    private static AtomicLong measurementCounter = new AtomicLong();

    public byte[] calculate(byte[] bytes) {
        try {
            Model model = ModelSerialization.deserialize(bytes);
            logger.trace("called: calculate, {}", StructuredArguments.kv("model.graph", model.getGraph()));
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();

                String originalUriValue = "";
                try {
                    NodeIterator nodeIterator = model.listObjectsOfProperty(dataSet, Opal.originalUri);
                    if(nodeIterator.hasNext()) originalUriValue = nodeIterator.next().toString();
                } catch (Exception ignored) {}
                logger.info("{} {}", StructuredArguments.kv("originalUri", originalUriValue),
                        StructuredArguments.kv("dataSetUri", dataSet.getURI()));

                Civet civet = new Civet();
                // If existing measurements should be removed
                // (optional method call, default: true)
                civet.setRemoveMeasurements(true);

                // If it should be logged, if a measurement could not be computed
                // (optional method call, default: true)
                civet.setLogNotComputed(true);

                civet.processModel(model, dataSet.getURI());
                makeOpalConfirmedQualityMeasurements(model, dataSet);
                logger.trace("return: calculate, {}", StructuredArguments.kv("model.graph", model.getGraph()));
                return ModelSerialization.serialize(model);
            }
        } catch (Exception e) {
            logger.error("An error occurred in CIVET ", e);
        }
        return bytes;
    }

    private void makeOpalConfirmedQualityMeasurements(Model model, Resource dataSet) {
        logger.trace("called: makeOpalConfirmedQualityMeasurements, {}, {}",
                StructuredArguments.kv("dataSet", dataSet),
                StructuredArguments.kv("model.graph", model.getGraph()));

        List<Statement> qualityMeasurementStmtIterator = model.listStatements(
                new SimpleSelector(dataSet, Dqv.HAS_QUALITY_MEASUREMENT, (RDFNode) null)).toList();
        for (Statement statement : qualityMeasurementStmtIterator) {
            assignIdToBlankNodeMeasurements(model, dataSet, statement);
        }
    }

    private void assignIdToBlankNodeMeasurements(Model model, Resource dataSet, Statement statement) {
        logger.trace("called: assignIdToBlankNodeMeasurements, {}, {}, {}", StructuredArguments.kv("model", model),
                StructuredArguments.kv("dataSet", dataSet), StructuredArguments.kv("statement", statement));

        Resource oldMeasurementResource = statement.getObject().asResource();
        long number = measurementCounter.incrementAndGet();
        Resource newMeasurementResource = ResourceFactory.createResource("http://projekt-opal.de/measurement" + number);
        logger.trace("new measurement resource id assign, {}, {}", StructuredArguments.kv("statement", statement),
                StructuredArguments.kv("newMeasurementResource", newMeasurementResource));


        StmtIterator oldIterator = model.listStatements(new SelectorImpl(oldMeasurementResource, null, (RDFNode) null));
        List<Statement> newResourceStatements = new ArrayList<>();
        while (oldIterator.hasNext()) {
            Statement stmt = oldIterator.nextStatement();
            newResourceStatements.add(new StatementImpl(newMeasurementResource, stmt.getPredicate(), stmt.getObject()));
        }
        oldIterator = model.listStatements(new SelectorImpl(oldMeasurementResource, null, (RDFNode) null));
        model.remove(oldIterator);
        model.add(newResourceStatements);

        model.remove(dataSet, Dqv.HAS_QUALITY_MEASUREMENT, oldMeasurementResource);
        model.add(dataSet, Dqv.HAS_QUALITY_MEASUREMENT, newMeasurementResource);
    }
}
