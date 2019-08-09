package org.diceresearch.qualitymetricsservice.utility;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.diceresearch.qualitymetricsservice.utility.civet.CivetApi;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.diceresearch.common.vocabulary.Dqv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class QualityMetricsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(QualityMetricsCalculator.class);

    private static AtomicLong measurementCounter = new AtomicLong();

    public byte[] calculate(byte[] bytes) {
        try {
            Model model = RdfSerializerDeserializer.deserialize(bytes);
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                logger.info("{}", kv("datasetUrl", dataSet.getURI()));
                CivetApi civetApi = new CivetApi();
                model = civetApi.computeFuture(model).get();
                makeOpalConfirmedQualityMeasurements(model, dataSet);
                return RdfSerializerDeserializer.serialize(model);
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
