package org.diceresearch.opalconfirmconversionservice.messaging;

import org.diceresearch.opalconfirmconversionservice.utility.OpalConfirmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final OpalConfirmer opalConfirmer;

    @Autowired
    public MessageBusController(OpalConfirmer opalConfirmer) {
        this.opalConfirmer = opalConfirmer;
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public byte[] getDatasetGraph(byte[] bytes) {
        return opalConfirmer.convert(bytes);
    }

}
