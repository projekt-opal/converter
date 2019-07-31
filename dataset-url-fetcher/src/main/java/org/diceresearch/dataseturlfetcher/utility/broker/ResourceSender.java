package org.diceresearch.dataseturlfetcher.utility.broker;

import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ResourceSender {
    @Autowired
    private Source source;

    public boolean send(Resource resource) {
        return this.source.output().send(MessageBuilder.withPayload(resource.getURI().getBytes()).build());
    }
}
