package org.diceresearch.qualitymetricsservice.utility;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.dice_research.opal.common.vocabulary.Dqv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.dice_research.opal.civet.Civet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.dice_research.opal.common.vocabulary.Opal.originalUri;

@Component
public class QualityMetricsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(QualityMetricsCalculator.class);

    private static AtomicLong measurementCounter = new AtomicLong();

    public byte[] calculate(byte[] bytes) {
        try {
            Model model = ModelSerialization.deserialize(bytes);
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();

                String originalUriValue = "";
                try {
                    NodeIterator nodeIterator = model.listObjectsOfProperty(dataSet, originalUri);
                    if(nodeIterator.hasNext()) originalUriValue = nodeIterator.next().toString();
                } catch (Exception ignored) {}
                logger.info("{} {}", kv("originalUri", originalUriValue), kv("dataSetUri", dataSet.getURI()));

                Civet civet = new Civet();
                // If existing measurements should be removed
                // (optional method call, default: true)
                civet.setRemoveMeasurements(true);

                // If it should be logged, if a measurement could not be computed
                // (optional method call, default: true)
                civet.setLogNotComputed(true);

                civet.processModel(model, dataSet.getURI());
                makeOpalConfirmedQualityMeasurements(model, dataSet);
                return ModelSerialization.serialize(model);
            }
        } catch (Exception e) {
            logger.error("An error occurred in CIVET ", e);
        }
        return bytes;
    }

    private void makeOpalConfirmedQualityMeasurements(Model model, Resource dataSet) {
        List<Statement> qualityMeasurementStmtIterator = model.listStatements(
                new SimpleSelector(dataSet, Dqv.HAS_QUALITY_MEASUREMENT, (RDFNode) null)).toList();
        for (Statement statement : qualityMeasurementStmtIterator) {
            Resource oldMeasurementResource = statement.getObject().asResource();
            long number = measurementCounter.incrementAndGet();
            Resource newMeasurementResource = ResourceFactory.createResource("http://projekt-opal.de/measurement" + number);

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
}
