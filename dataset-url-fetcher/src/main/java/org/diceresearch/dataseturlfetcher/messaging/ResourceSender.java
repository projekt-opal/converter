package org.diceresearch.dataseturlfetcher.messaging;

import org.apache.jena.rdf.model.Resource;
import org.diceresearch.common.utility.Pair;
import org.diceresearch.dataseturlfetcher.model.Portal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class ResourceSender {
    private static final Logger logger = LoggerFactory.getLogger(ResourceSender.class);

    private final Source source;

    @Autowired
    public ResourceSender(Source source) {
        this.source = source;
    }

    public boolean send(Resource resource, Portal portal) {
        logger.info(" {}", kv("datasetUrl", resource.getURI()));
        Pair<String, String> pair = Pair.of(resource.getURI(), portal.getName());
        return this.source.output().send(MessageBuilder.withPayload(pair).build());
    }
}
