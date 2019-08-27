package org.diceresearch.triplestorewriter.messages;

import org.diceresearch.triplestorewriter.utility.TripleStoreWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final TripleStoreWriter tripleStoreWriter;

    @Autowired
    public MessageBusController(TripleStoreWriter tripleStoreWriter) {
        this.tripleStoreWriter = tripleStoreWriter;
    }

    @StreamListener(Sink.INPUT)
    public void getDatasetGraph(byte[] bytes) {
        tripleStoreWriter.write(bytes);
    }

}
