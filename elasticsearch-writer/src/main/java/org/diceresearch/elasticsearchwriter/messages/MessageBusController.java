package org.diceresearch.elasticsearchwriter.messages;

import org.diceresearch.elasticsearchwriter.utility.ElasticSearchWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final ElasticSearchWriter elasticSearchWriter;

    @Autowired
    public MessageBusController(ElasticSearchWriter elasticSearchWriter) {
        this.elasticSearchWriter = elasticSearchWriter;
    }

    @StreamListener(Sink.INPUT)
    public void getDatasetGraph(byte[] bytes) {
        elasticSearchWriter.write(bytes);
    }

}
