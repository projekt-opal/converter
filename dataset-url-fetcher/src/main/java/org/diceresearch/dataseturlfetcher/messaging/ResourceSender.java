package org.diceresearch.dataseturlfetcher.messaging;

import org.apache.jena.rdf.model.Resource;
import org.diceresearch.common.utility.Pair;
import org.diceresearch.dataseturlfetcher.model.Portal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ResourceSender {

    private final Source source;

    @Autowired
    public ResourceSender(Source source) {
        this.source = source;
    }

    public boolean send(Resource resource, Portal portal) {
        Pair<String, String> pair = Pair.of(resource.getURI(), portal.getName());
        return this.source.output().send(MessageBuilder.withPayload(pair).build());
    }
}
