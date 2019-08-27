package org.diceresearch.qualitymetricsservice.messages;

import org.diceresearch.qualitymetricsservice.utility.QualityMetricsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final QualityMetricsCalculator qualityMetricsCalculator;

    @Autowired
    public MessageBusController(QualityMetricsCalculator qualityMetricsCalculator) {
        this.qualityMetricsCalculator = qualityMetricsCalculator;
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public byte[] getDatasetGraph(byte[] bytes) {
        return qualityMetricsCalculator.calculate(bytes);
    }

}
