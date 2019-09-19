package org.diceresearch.elasticsearchwriter.utility.impl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.diceresearch.elasticsearchwriter.utility.ElasticSearchWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class ElasticSearchWriterImpl implements ElasticSearchWriter {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchWriter.class);

    @Override
    public void write(byte[] bytes) {
        Model model = RdfSerializerDeserializer.deserialize(bytes);
        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                logger.info("{}", kv("datasetUrl", dataSet.getURI()));
            }
        } catch (Exception ignored) {
        }
        // TODO: 9/16/19 call es writer here
    }
}
