package org.diceresearch.datacleanerservice.messaging;

import org.diceresearch.datacleanerservice.utility.DataCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final DataCleaner dataCleaner;

    @Autowired
    public MessageBusController(DataCleaner dataCleaner) {
        this.dataCleaner = dataCleaner;
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public byte[] getDatasetGraph(byte[] bytes) {
        return dataCleaner.clean(bytes);
    }

}
