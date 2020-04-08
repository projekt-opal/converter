package org.diceresearch.datacleanerservice.utility;

import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.Catfish;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.jena.rdf.model.Model;

@Component
public class DataCleaner {

    private static final Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    public byte[] clean(byte[] bytes) {
        Model model;
        try {
            model = ModelSerialization.deserialize(bytes);
            logger.trace("called: convert, {}", StructuredArguments.kv("model", model));
        } catch (Exception e) {
            logger.error("Exception in deserialize the byte code ", e);
            return bytes;
        }
        try {
            Catfish catfish = new Catfish();
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                catfish.processModel(model, dataSet.getURI());
                return ModelSerialization.serialize(model);
            }
        } catch (Exception e) {
            logger.error("Exception in cleaning th model", e);
        }

        return bytes;
    }
}
